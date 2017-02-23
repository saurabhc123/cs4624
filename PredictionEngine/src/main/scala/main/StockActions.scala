package main



object Action extends Enumeration {
  type Action = Value
  val BUY, SELL, HOLD = Value
}

import java.time.{Instant, LocalDateTime, ZoneOffset}

import main.Action.Action
/**
  * Created by Eric on 2/11/2017.
  */
object StockActions {

  def makeRun(): Unit ={
    val action = getAction("A", Instant.now(), Instant.now())
    println(s"The action for stock A is $action")
  }
  def getAction(stock:String, startTime : Instant, endTime : Instant): Action = {
    val tweets = StockDecisions.getTweets(stock,startTime,endTime)
    val finalScore = StockDecisions.finalScore(tweets, getPrice(stock, startTime), getPrice(stock, endTime))
    if (finalScore >= 0.9){
      return if(getPrice(stock, startTime) < getPrice(stock,endTime)) Action.BUY else Action.SELL
    }
    return Action.HOLD
  }
  def getActions(stocks: List[String], startTime : Instant, endTime : Instant): List[Action] = {
    stocks.map(stock => getAction(stock, startTime, endTime))
  }


  def getPrice(symbol : String,time: Instant): Double = {

    val m :Map[Instant, Double] = Map(
      LocalDateTime.of(2014, 1, 1, 0, 0).toInstant(ZoneOffset.UTC) -> 10,
      LocalDateTime.of(2014,1,2,0,0).toInstant(ZoneOffset.UTC) -> 200,
      LocalDateTime.of(2014,1,3,0,0).toInstant(ZoneOffset.UTC) -> 3000,
      LocalDateTime.of(2014,1,4,0,0).toInstant(ZoneOffset.UTC) -> 40000,
      LocalDateTime.of(2014,1,5,0,0).toInstant(ZoneOffset.UTC) -> 500000,
      LocalDateTime.of(2014,1,6,0,0).toInstant(ZoneOffset.UTC) -> 6000000
    )
    val minTup = m.filter(tup => tup._1.isBefore(time.plusNanos(1)))
      .min(Ordering.by[(Instant,Double),Double](tup => time.getEpochSecond - tup._1.getEpochSecond))


    return minTup._2
  }

}
