package main.Jobs

import java.time.{Duration, Instant}

import main.DataTypes.JudgeScore
import main.Interfaces.IScheduledJob
import main.{Judge, StockTweet}
import org.apache.spark.rdd.RDD

/**
 * Created by saur6410 on 2/12/17.
 */
class JudgeScoresProcessorJob extends IScheduledJob{

  val dayDuration = Duration.ZERO.plusDays(1)
  override def run(tweets: RDD[StockTweet]) =
  {


    //Now that we have the tweets, we assume all the values in the Tweets are populated. We filter to ensure that they
    //have the values
    val filteredTweets = tweets.filter(tweet => tweet.sentiment.isDefined)
    .filter(tweet => tweet.symbol.isDefined)
    .filter(tweet => tweet.rawPredictionScore.isDefined)
    .filter(tweet => tweet.sentimentOrder.isDefined)

    //Get the last day that we updated the scores for.
    //ToDo: Read this from the DB
    val lastUpdateDate = Instant.now()

    //Start from a day after the last updated date
    var currentTime = lastUpdateDate.plus(dayDuration)
    //Process until we have processed till today
    while (currentTime.isBefore(Instant.now().plus(dayDuration))) {
      val judgeScores = GetScoresSnapshot(currentTime, filteredTweets)
      //Write the scores for that day.
      //judgeScores
      //ToDo:Finish this.
      //Move to the next day
      currentTime = currentTime.plus(dayDuration)
    }


  }

  def GetScoresSnapshot(snapshotInstant : Instant, tweets : RDD[StockTweet]): RDD[JudgeScore] =
  {
    //Get all the tweets until that day since the last k-days (The moving window)
    def relevantTweets = tweets.filter(tweet => tweet.timestamp.isAfter(snapshotInstant.minus(Judge.confirmationTimeWindow)))
                            .filter(tweet => tweet.timestamp.isBefore(snapshotInstant.plus(dayDuration)))

    //Group by JudgeIds
    val tweetsGroupedByJudgeIdForKeys = relevantTweets.groupBy(tweet => tweet.judgeId)
    //ToDo: The following line can be improved or optimized further.
    val tweetsGroupedByJudgeId = tweetsGroupedByJudgeIdForKeys.map(tweet => (tweet._1, relevantTweets.filter(t => t.judgeId == tweet._1)))
    //Create a snapshot of scores for all judges for that day.
    val judges = tweetsGroupedByJudgeId.map(judgeTweetsGroup => new Judge(judgeTweetsGroup._1, null, judgeTweetsGroup._2))
    val judgeScores = judges.map(judge => new JudgeScore(judge.identifier, judge.getJudgeIndividualWeight(snapshotInstant), snapshotInstant))
    return judgeScores
  }
}
