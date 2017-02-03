package main
import java.time.temporal.TemporalAmount
import java.time.{Duration, Instant}

import main.DataTypes.Tweet
import main.Interfaces.{DataType, IStockDataRetriever, ITweetDataRetriever}
/**
  *
  * This class requires that the tweets that can be read have stocks, judges, times, and label defined
  * Created by Eric on 2/3/2017.
  */
class Judge(val identifier: String, iTweetDataRetriever: ITweetDataRetriever, iStockDataRetriever: IStockDataRetriever) {

  private val myTweets = iTweetDataRetriever.readTweets(DataType.TEST)
    .filter(tweet => tweet.time.isDefined)
    .filter(tweet => tweet.judge.isDefined)
    .filter(tweet => tweet.judge.get == identifier)
    .filter(tweet => tweet.stock.isDefined)
    .filter(tweet => tweet.label.isDefined)
    .cache()
  def getJudgeIndividualWeight(currentTime: Instant): Double ={
    def tweetsBeforeTime = myTweets.filter(tweet => tweet.time.get.isBefore(currentTime.minus(Judge.confirmationTimeWindow)))
    val rawPredictionScores = myTweets.map(tweet => getRawPrediction(tweet))
    val scoreMean = rawPredictionScores.mean()
    val scoreStdDev = rawPredictionScores.stdev()
    scoreMean / scoreStdDev
  }

  private def getRawPrediction(tweet: Tweet): Double = {
      val priceAtTweetTime = iStockDataRetriever
        .getPriceOfStock(tweet.stock.get, tweet.time.get)
      val priceAfterTimeInterval = iStockDataRetriever
        .getPriceOfStock(tweet.stock.get, tweet.time.get.plus(Judge.confirmationTimeWindow))
      val deltaResult = (priceAfterTimeInterval - priceAtTweetTime) / priceAtTweetTime
      val opinion = tweet.label.get
      deltaResult * opinion
  }



}

object Judge{
  var confirmationTimeWindow: TemporalAmount = Duration.ofHours(24)

  val PositiveLabel: Double = 1.0
  val NegativeLabel: Double = -1.0
}
