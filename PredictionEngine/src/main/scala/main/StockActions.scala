package main



object Action extends Enumeration {
  type Action = Value
  val BUY, SELL, HOLD = Value
}

import java.time.{Instant, LocalDateTime, ZoneOffset}

import main.Action.Action
import cs4624.pricingdata.StockPrices._
import cs4624.pricingdata.{StockPrice, StockPrices}
import org.apache.hadoop.hbase.client.ConnectionFactory
import java.time.temporal.ChronoUnit

/**
  * Created by Eric on 2/11/2017.
  */
object StockActions {

  // TODO: improve this caching mechanism (uses tons of memory for long experiments)
  private var stockPricingCache = Map[String, Seq[StockPrice]]()
  implicit val connection = ConnectionFactory.createConnection()

  def makeRun(): Unit ={
    val action = getAction("A", Instant.now(), Instant.now())
    println(s"The action for stock A is $action")
  }
  def getAction(stock:String, startTime : Instant, endTime : Instant): Action = {
    val tweets = StockDecisions.getTweets(stock,startTime,endTime)
    val prices = (getPrice(stock, startTime), getPrice(stock, endTime))
    prices match {
      case (Some(startPrice), Some(endPrice)) => {
        val finalScore = StockDecisions.finalScore(tweets, startPrice, endPrice)
        if (finalScore >= 0.9) {
          if (startPrice < endPrice) Action.BUY else Action.SELL
        } else Action.HOLD
      }
      case _ => Action.HOLD
    }
  }
  def getActions(stocks: List[String], startTime : Instant, endTime : Instant): List[(String, Action)] = {
    stockPricingCache = stocks.map { stock => (stock, StockPrices.query(stock, startTime.minus(5, ChronoUnit.DAYS), endTime)) }.toMap
    stocks.map(stock => (stock, getAction(stock, startTime, endTime)))
  }


  def getPrice(symbol : String,time: Instant): Option[Double] = {
    stockPricingCache.get(symbol).flatMap(_.getPrice(symbol, time))
  }

}
