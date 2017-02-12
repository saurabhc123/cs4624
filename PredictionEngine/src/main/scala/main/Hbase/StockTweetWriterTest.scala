package main.Hbase

import java.time.Instant

import main.StockTweet

/**
  * Created by Eric on 2/12/2017.
  */
object StockTweetWriterTest {
  def doTest(): Unit ={
    val st = StockTweet("id1", "text", "j1", Instant.now)
    StockTweetWriter.write(st)
  }
}
