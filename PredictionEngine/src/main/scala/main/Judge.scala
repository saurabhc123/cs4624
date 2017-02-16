package main
import java.time.temporal.TemporalAmount
import java.time.{Duration, Instant}

import main.Interfaces.IStockDataRetriever
import org.apache.spark.rdd.RDD

/**
  *
  * This class requires that the tweets that can be read have stocks, judges, times, and label defined
  * Created by Eric on 2/3/2017.
  */
class Judge(val identifier: String, iStockDataRetriever: IStockDataRetriever,
             tweets : RDD[StockTweet]) extends java.io.Serializable{

  /*private val tweets = iTweetDataRetriever.readTweets(DataType.TEST)
    .filter(tweet => tweet.symbol.isDefined)
    .filter(tweet => tweet.sentiment.isDefined)
    .filter(tweet => tweet.rawPredictionScore.isDefined)
    .filter(tweet => tweet.sentimentOrder.isDefined)
    .cache()*/


  def getJudgeIndividualWeight(currentTime: Instant): Double ={


    val rawPredictionScores = tweets.map(tweet => getRawPrediction(tweet))
    val scoreMean = rawPredictionScores.mean()
    val scoreStdDev = rawPredictionScores.stdev()
    scoreMean / scoreStdDev
  }

  private def getRawPrediction(tweet: StockTweet): Double = {
      val priceAtTweetTime = iStockDataRetriever
        .getPriceOfStock(tweet.symbol.get, tweet.timestamp)
      val priceAfterTimeInterval = iStockDataRetriever
        .getPriceOfStock(tweet.symbol.get, tweet.timestamp.plus(Judge.judgeScoringTimeWindow))
      val deltaResult = (priceAfterTimeInterval - priceAtTweetTime) / priceAtTweetTime
      val opinion = if(tweet.sentiment.get == Sentiment.POSITIVE) 1 else -1
      deltaResult * opinion
  }


}

object Judge{
  val movingWindowSize = 10
  var confirmationTimeWindow: TemporalAmount = Duration.ofDays(movingWindowSize)
  var judgeScoringTimeWindow: TemporalAmount = Duration.ofDays(3)

  val PositiveLabel: Double = 1.0
  val NegativeLabel: Double = -1.0

  var rawPredictionScore = 0;
  var scoreTimestamp = Instant.now()

}
