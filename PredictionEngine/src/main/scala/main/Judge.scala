package main
import java.time.temporal.TemporalAmount
import java.time.{Duration, Instant}

import main.Interfaces.{DataType, IStockDataRetriever, ITweetDataRetriever}
/**
  *
  * This class requires that the tweets that can be read have stocks, judges, times, and label defined
  * Created by Eric on 2/3/2017.
  */
class Judge(val identifier: String, iTweetDataRetriever: ITweetDataRetriever, iStockDataRetriever: IStockDataRetriever) extends java.io.Serializable{

  private val myTweets = iTweetDataRetriever.readTweets(DataType.TEST)
    .filter(tweet => tweet.symbol.isDefined)
    .filter(tweet => tweet.sentiment.isDefined)
    .filter(tweet => tweet.rawPredictionScore.isDefined)
    .filter(tweet => tweet.sentimentOrder.isDefined)
    .cache()


  def getJudgeIndividualWeight(currentTime: Instant): Double ={
    def tweetsBeforeTime = myTweets.filter(tweet => tweet.timestamp.isBefore(currentTime.minus(Judge.confirmationTimeWindow)))
    val rawPredictionScores = myTweets.map(tweet => getRawPrediction(tweet))
    val scoreMean = rawPredictionScores.mean()
    val scoreStdDev = rawPredictionScores.stdev()
    scoreMean / scoreStdDev
  }

  private def getRawPrediction(tweet: StockTweet): Double = {
      val priceAtTweetTime = iStockDataRetriever
        .getPriceOfStock(tweet.symbol.get, tweet.timestamp)
      val priceAfterTimeInterval = iStockDataRetriever
        .getPriceOfStock(tweet.symbol.get, tweet.timestamp.plus(Judge.confirmationTimeWindow))
      val deltaResult = (priceAfterTimeInterval - priceAtTweetTime) / priceAtTweetTime
      val opinion = if(tweet.sentiment.get == Sentiment.POSITIVE) 1 else -1
      deltaResult * opinion
  }


}

object Judge{
  var confirmationTimeWindow: TemporalAmount = Duration.ofHours(24)

  val PositiveLabel: Double = 1.0
  val NegativeLabel: Double = -1.0
}
