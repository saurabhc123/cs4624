package main

import java.time.Instant

import main.DataTypes.Tweet
import org.apache.spark.rdd.RDD
object Sentiment extends Enumeration {
  type Sentiment = Value
  val POSITIVE, NEGATIVE = Value
}

import main.Sentiment.Sentiment
case class StockTweet(symbol: String, text:String, sentiment: Sentiment, judgeId:String, timestamp: Instant ){}
/**
  * Created by Eric on 2/11/2017.
  */
object StockDecisions{


  val start = 0.5
  val end = 0.1
  def makeRun(): Unit ={
    val stockTweets = getTweets("A", Instant.now(), Instant.now())
    stockTweets.collect().foreach(tweet => CalcJudgeIndividualWeight(tweet.judgeId,stockTweets,tweet.timestamp))
    val finalScoreOfTweets = finalScore(stockTweets, start, end)
    println(s"The final score is stock A is $finalScoreOfTweets")
  }
  private val lambda = 0.05
  private def getRawPrediction(tweet: StockTweet): Double = {
    val priceAtTweetTime = StockActions.getPrice(tweet.symbol, tweet.timestamp)
    val priceAfterTimeInterval = StockActions.getPrice(tweet.symbol, tweet.timestamp.plus(Judge.confirmationTimeWindow))

    val deltaResult = (priceAfterTimeInterval - priceAtTweetTime) / priceAtTweetTime
    val opinion = if (tweet.sentiment == Sentiment.POSITIVE) 1 else -1
    deltaResult * opinion
  }

  private val weightMap: scala.collection.mutable.Map[(String,Instant),Double] = scala.collection.mutable.Map[(String,Instant),Double]()
  private def CalcJudgeIndividualWeight(judgeId : String, stockTweets: RDD[StockTweet], timestamp: Instant): Double = {
    val myTweets = stockTweets.filter(st => judgeId == st.judgeId)
    val tweetsBeforeTime = myTweets.filter(tweet => tweet.timestamp.isBefore(timestamp.minus(Judge.confirmationTimeWindow)))
    val rawPredictionScores = tweetsBeforeTime.map(tweet => getRawPrediction(tweet))
    val scoreMean = rawPredictionScores.mean()
    val scoreStdDev = rawPredictionScores.stdev()
    val result = scoreMean / scoreStdDev
    weightMap((judgeId,timestamp)) = result
    weightMap((judgeId,timestamp))
  }
  def GetJudgeIndividualWeight(judgeId: String, timeStamp :Instant): Double ={
    weightMap.getOrElse((judgeId,timeStamp), Double.NaN)
  }

  def getTweets(symbol: String, startTime: Instant, endTime: Instant): RDD[StockTweet] = {
    val text = SparkContextManager.getContext.textFile("dummy_stock_tweets.txt")
    text.map(x => x.split(','))
      .map(x => StockTweet(x(0),x(1),
           if (x(2) == "1")Sentiment.POSITIVE else Sentiment.NEGATIVE,
            x(3), Instant.parse(x(4))))
  }


  def getDegreesOfIndependence(tweets: RDD[StockTweet]): RDD[(StockTweet, Double)] ={
    val sentiments = tweets.map(tweet => tweet.sentiment).distinct().collect()
    var rdds : RDD[(StockTweet,Double)] = SparkContextManager.getContext.emptyRDD
    for(sentiment <- sentiments){
      rdds = rdds.union(tweets.filter(tweets => tweets.sentiment == sentiment)
        .sortBy(st => st.timestamp).zipWithIndex()
        .map(tup => (tup._1, Math.exp(1 - (lambda * (tup._2 - 1))))))
    }
    rdds
  }

  def finalScore(tweets: RDD[StockTweet], startPrice: Double, endPrice: Double): Double = {
    val o_positive = if (endPrice > startPrice) 1 else 0
    val o_negative = Math.abs(o_positive - 1)
    val sumOfAllSentiments = calcNumerator(Sentiment.POSITIVE, tweets) + calcNumerator(Sentiment.NEGATIVE, tweets)

    val positiveSentimentWeight = calcNumerator(Sentiment.POSITIVE, tweets)/ sumOfAllSentiments

    val negativeSentimentWeight = calcNumerator(Sentiment.NEGATIVE, tweets) / sumOfAllSentiments


    val a = 100
    val b = -50
    a  + (b * (Math.pow(o_positive - positiveSentimentWeight, 2) + Math.pow(o_negative- negativeSentimentWeight, 2) ))
  }

  def calcNumerator(sentiment: Sentiment, tweets : RDD[StockTweet]): Double= {
    val only_these_sent = tweets.filter(x => x.sentiment == sentiment)
    val TweetAndInd = getDegreesOfIndependence(only_these_sent)
    TweetAndInd
      .map(tweet => GetJudgeIndividualWeight(tweet._1.judgeId,tweet._1.timestamp) * tweet._2).filter(!_.isNaN).filter(!_.isInfinity)
      .sum()
  }



}
