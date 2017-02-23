package main

import java.time.Instant
import java.time.temporal.TemporalAmount

/**
  * Created by Eric on 2/12/2017.
  */
object StockOrchestrator {
  private val stocks = List("AAPL", "FB", "GILD", "KNDI", "MNKD", "NQ", "PLUG", "QQQ", "SPY", "TSLA", "VRNG")
  def Orchestrate(startTime : Instant, endTime: Instant, timeWindow: TemporalAmount, judgeWindow: TemporalAmount) : Unit = {
    Judge.confirmationTimeWindow = judgeWindow
    var currentTime = startTime.plus(timeWindow)
    while(!currentTime.isAfter(endTime)) {
      val actions = StockActions.getActions(stocks, startTime,currentTime).zip(stocks)
      println(actions)
      currentTime = currentTime.plus(timeWindow)
    }
  }

}
