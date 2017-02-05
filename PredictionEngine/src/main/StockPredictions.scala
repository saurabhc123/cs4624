package main

import java.time.{Duration, Instant}

import main.Interfaces.{DataType, IStockDataRetriever, ITweetDataRetriever}


/**
  * Created by Eric on 2/3/2017.
  */
class StockPredictions(iTweetDataRetriever: ITweetDataRetriever, iStockDataRetriever: IStockDataRetriever) {
  private val TIME_INTERVAL = Duration.ZERO.plusDays(1)
  private val startTime = Instant.now().minus(TIME_INTERVAL).minus(TIME_INTERVAL)
  private val endTime = Instant.now()
  private val all_judges = iTweetDataRetriever.readTweets(DataType.TEST)
    .filter(tweet => tweet.judge.isDefined)
    .map(tweet => tweet.judge.get).distinct()
    .map(judge => new Judge(judge, iTweetDataRetriever, iStockDataRetriever))

  private var currentTime = startTime
  while (currentTime.isBefore(endTime)){
    val all_weights = all_judges.map(judge => judge.getJudgeIndividualWeight(currentTime))
    all_weights.foreach(println)

    currentTime = currentTime.plus(TIME_INTERVAL)
  }



}
