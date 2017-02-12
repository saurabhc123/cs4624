package main.Hbase

import java.time.Instant

import main.{Sentiment, StockTweet}

/**
  * Created by Eric on 2/12/2017.
  */
object StockTweetWriterTest {
  def doTest(): Unit ={
    val st = StockTweet("id1", "text", "j1", Instant.now)
    StockTweetWriter.write(st)
    val st2 = StockTweet("id2", "text", "j1", Instant.now, sentiment = Some(Sentiment.POSITIVE))
    StockTweetWriter.write(st2)
  }
}
