package main

import java.time.Instant
import java.time.temporal.TemporalAmount

/**
  * Created by Eric on 2/12/2017.
  */
object StockOrchestrator {
  private val stocks = List("AAPL", "FB", "GILD", "KNDI", "MNKD", "NQ", "PLUG", "QQQ", "SPY", "TSLA")
  def Orchestrate(startTime : Instant, endTime: Instant, timeWindow: TemporalAmount, judgeWindow: TemporalAmount) : Unit = {
    Judge.confirmationTimeWindow = judgeWindow
    var currentTime = startTime.plus(timeWindow)
    val startCash = 100000.0
    var cash = startCash
    val stockAmounts = scala.collection.mutable.Map[String, Int]()
    while(!currentTime.isAfter(endTime)) {
      val actions = StockActions.getActions(stocks, currentTime.minus(timeWindow), currentTime)
      actions.foreach { case (stock, action) =>
        if (action == Action.BUY) {
          StockActions.getPrice(stock, currentTime).foreach(price => {
            val amountBought = Math.floor(startCash / stocks.size / price).toInt
            stockAmounts(stock) = stockAmounts.get(stock).getOrElse(0) + amountBought
            cash -= amountBought * price
          })
        } else if (action == Action.SELL) {
          StockActions.getPrice(stock, currentTime).foreach(price => {
            val addedValue = stockAmounts.get(stock).getOrElse(0) * price
            stockAmounts(stock) = 0
            cash += addedValue
          })
        }
      }
      println(s"Total Cash: $cash")
      println(s"Stocks: $stockAmounts")
      currentTime = currentTime.plus(timeWindow)
    }
  }

}
