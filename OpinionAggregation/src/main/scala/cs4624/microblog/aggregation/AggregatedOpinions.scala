package cs4624.microblog.aggregation

import java.time.{Duration, Instant}

import cs4624.microblog.{MicroblogAuthor, MicroblogPost}
import cs4624.microblog.contribution.MicroblogAuthorContributions
import cs4624.microblog.sentiment._
import cs4624.prices.sources.StockPriceDataSource
import org.apache.log4j.LogManager

import scala.collection.mutable

/**
  * Created by joeywatts on 3/15/17.
  */
class AggregatedOpinions(sentimentAnalysisModel: SentimentAnalysisModel,
                         stockPriceDataSource: StockPriceDataSource,
                         opinionConfirmationTimeWindow: Duration) {

  private val stockSentimentSum = mutable.Map[(String, Sentiment), Double]().withDefaultValue(0.0)
  private val authorContributions = mutable.Map[MicroblogAuthor, MicroblogAuthorContributions]()
  private val sentimentCount = mutable.Map[(String, Sentiment), Long]().withDefaultValue(1L)
  private val log = LogManager.getRootLogger

  private val lambda = 0.05
  private val postsToProcess = mutable.Queue[MicroblogPost]()

  def on(post: MicroblogPost) = {
    while (postsToProcess.headOption.exists(_.time.isBefore(post.time.minus(opinionConfirmationTimeWindow)))) {
      val postToProcess = postsToProcess.dequeue()
      val postScore = rawPostScore(postToProcess)
      authorContributions.get(postToProcess.author) match {
        case Some(currentAuthorContribution) =>
          currentAuthorContribution.transform(postToProcess, postScore)
          currentAuthorContribution
        case None =>
          val currentAuthorContribution = new MicroblogAuthorContributions(
            meanOfPostScores = postScore,
            sumOfSquaredDifferencesFromMean = 0,
            numberOfPosts = 1
          )
          authorContributions.update(postToProcess.author, currentAuthorContribution)
      }
    }

    val postWithSentiment = post.copy(sentiment = post.sentiment.orElse {
      sentimentAnalysisModel.predict(post).flatMap(Sentiment.fromLabel)
    })
    postWithSentiment.sentiment match {
      case Some(sentiment) => {
        // update stock aggregated opinion
        val authorContribution = authorContributions.get(post.author)
        authorContribution match {
          case Some(ac) if !ac.weight.isNaN && !ac.weight.isInfinity =>
            postWithSentiment.symbols.foreach(symbol => {
              val sentimentOrder = sentimentCount((symbol, sentiment))
              sentimentCount((symbol, sentiment)) += 1
              val degreeOfIndependence = Math.exp(1 - lambda * (sentimentOrder - 1))
              stockSentimentSum((symbol, sentiment)) += degreeOfIndependence * ac.weight
            })
          case _ =>
        }
      }
      case None =>
        log.warn(s"Got no sentiment for post! (text: ${post.text})")
    }
    postsToProcess += postWithSentiment
  }

  // TODO: handle time interval better.
  def opinionForStock(stock: String): Double = {
    val bullishSum = Math.max(0, stockSentimentSum((stock, Bullish)))
    val bearishSum = Math.max(0, stockSentimentSum((stock, Bearish)))
    val sum = bullishSum + bearishSum
    if (sum == 0)
      return 0.5
    val bullishWeight = bullishSum / sum
    val bearishWeight = bearishSum / sum
    log.info(s"Bullish Weight: $bullishWeight Bearish Weight: $bearishWeight")
    1 - 0.5 * (Math.pow(1 - bullishWeight, 2) + Math.pow(- bearishWeight, 2))

    /*val (startTime, endTime) = timeInterval
    (stockPriceDataSource.priceAtTime(stock, startTime).map(_.price),
      stockPriceDataSource.priceAtTime(stock, endTime).map(_.price)) match {
        case (Some(startPrice), Some(endPrice)) =>
          if (endPrice > startPrice) {
            1 - 0.5 * (Math.pow(1 - bullishWeight, 2) + Math.pow(- bearishWeight, 2))
          } else {
            1 - 0.5 * (Math.pow(- bullishWeight, 2) + Math.pow(1 - bearishWeight, 2))
          }
        case _ => 0
      }*/
  }

  def sentimentForStock(stock: String): Option[Sentiment] = {
    val opinion = opinionForStock(stock)
    log.info(s"Opinion for $stock: $opinion")
    if (opinion >= 0.9) Some(Bullish)
    else if (opinion <= 0.1) Some(Bearish)
    else None
  }

  def reset(): Unit = {
    sentimentCount.clear()
    stockSentimentSum.clear()
  }

  private def rawPostScore(post: MicroblogPost): Double = {
    val opinion = post.sentiment match {
      case Some(Bearish) => -1
      case Some(Bullish) => 1
      case _ => 0
    }
    (stockPriceDataSource.priceAtTime(post.symbols.head, post.time).map(_.price),
      stockPriceDataSource.priceAtTime(post.symbols.head, post.time.plus(opinionConfirmationTimeWindow)).map(_.price)) match {
        case (Some(priceAtTweetTime), Some(priceAfterConfirmationWindow)) =>
          opinion * (priceAfterConfirmationWindow - priceAtTweetTime) / priceAtTweetTime
        case _ => 0
      }
  }
}
