package main.Jobs

import main.Interfaces.IScheduledJob
import main.StockTweet
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 2/12/17.
 */
class JudgeScoresProcessorJob extends IScheduledJob{


  override def run(labels: RDD[StockTweet]) =
  {
    //Now that we have the tweets, we assume all the values in the Tweets are populated.

    //Get the last day that we updated the scores for.

    //Find a bunch of days to update the scores for.

    //For each day, update the score
      //Get all the tweets until that day since the last k-days (The moving window)
      //
  }
}
