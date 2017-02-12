package main.Hbase

import java.time.Instant

import main.{Sentiment, SparkContextManager, StockTweet}

/**
  * Created by Eric on 2/12/2017.
  */
object StockTweetWriterTest {
  def doTest(): Unit ={
    val st = StockTweet("id1", "text", "j1", Instant.now)
    StockTweetWriter.write(st)
    val st2 = StockTweet("id2", "text", "j1", Instant.now, sentiment = Some(Sentiment.POSITIVE))
    StockTweetWriter.write(st2)

    // now to read

    println(StockTweetWriter.read("id1"))
    println(StockTweetWriter.read("id2"))
  }
  def readFromCsvWriteToDB(filename: String): Unit = {
    val file = SparkContextManager.getContext.textFile(filename)
    val tweets = file.map(line => line.split(",")).map(arr => StockTweet(arr(0), arr(2),arr(5) ,Instant.parse(arr(4)), symbol = Some(arr(8))))
    tweets.foreach(StockTweetWriter.write)
  }

}
