package main.Interfaces

import main.Interfaces.DataType.DataType
import main.StockTweet
import org.apache.spark.rdd.RDD

/**
  * Created by Eric on 2/3/2017.
  */
trait ITweetDataRetriever extends java.io.Serializable {
  def readTweets(tweetType: DataType): RDD[StockTweet]
}
