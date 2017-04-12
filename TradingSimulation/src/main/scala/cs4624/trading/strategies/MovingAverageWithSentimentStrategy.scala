package cs4624.trading.strategies

import cs4624.portfolio.Portfolio
import cs4624.prices.StockPrice
import cs4624.prices.sources.StockPriceDataSource
import cs4624.trading.{TradingEvent, TradingStrategy}
import java.time._

import cs4624.portfolio.error.TransactionError
import cs4624.trading.events._
import org.apache.log4j.LogManager

import cs4624.microblog.aggregation.AggregatedOpinions
import cs4624.microblog.sentiment._

import scala.collection.mutable

class MovingAverageWithSentimentStrategy(stock: String,
  portfolio: Portfolio,
  stockPriceDataSource: StockPriceDataSource,
  short: Int = 5, long: Int = 10)
    extends MovingAverageStrategy(stock, portfolio, stockPriceDataSource, short, long) {

  var sentiment: Option[Sentiment] = None
  override val log = LogManager.getLogger("MovingAverageWithSentiment")
  val aggregatedOpinions = new AggregatedOpinions(
    stockPriceDataSource,
    Duration.ofDays(1)
  )

  override def on(event: TradingEvent): MovingAverageStrategy = {
    event match {
      case MarketClose(time) if prices.size >= long =>
        aggregatedOpinions.sentimentForStock(stock).foreach { s => sentiment = Some(s) }
        aggregatedOpinions.reset()
        stockPriceDataSource.priceAtTime(stock, time).foreach { p =>
          val shortAverage = runningAverage(short)
          val longAverage = runningAverage(long)
          if (p.price > shortAverage && p.price > longAverage &&
            (!sentiment.isDefined || sentiment.get == Bullish)) {
            decision = Buy
          } else if (p.price < shortAverage && p.price < longAverage &&
            (!sentiment.isDefined || sentiment.get == Bearish)) {
            decision = Sell
          } else {
            decision = Wait
          }
        }
      case MicroblogPostEvent(post) if post.symbols.contains(stock) =>
        aggregatedOpinions.on(post)
      case _ => super.on(event)
    }
    this
  }
}
