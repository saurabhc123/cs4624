package main.Interfaces

import main.StockTweet
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 2/12/17.
 */
trait IScheduledJob {

  def run(labels: RDD[StockTweet])

}
