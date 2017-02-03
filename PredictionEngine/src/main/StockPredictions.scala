package main

import main.Interfaces.{DataType, IStockDataRetriever, ITweetDataRetriever}

/**
  * Created by Eric on 2/3/2017.
  */
class StockPredictions(iTweetDataRetriever: ITweetDataRetriever, iStockDataRetriever: IStockDataRetriever) {
  private val all_judges = iTweetDataRetriever.readTweets(DataType.TEST)
    .filter(tweet => tweet.judge.isDefined)
    .map(tweet => tweet.judge.get).distinct().map(judge => new Judge(judge, iTweetDataRetriever, iStockDataRetriever))



}
