package cs4624.trading

import java.time.{Duration, Instant, OffsetDateTime, ZoneOffset}

import cs4624.trading.events.{MarketEventsEmitter, StockPriceEventEmitter}
import cs4624.common.spark.SparkContextManager._
import cs4624.microblog.sources.HBaseMicroblogDataSource
import cs4624.microblog.sources.HBaseMicroblogDataSource.Default
import cs4624.portfolio.Portfolio
import cs4624.prices.sources.HBaseStockPriceDataSource
import cs4624.prices.sources.HBaseStockPriceDataSource.YahooFinance
import org.apache.hadoop.hbase.client.ConnectionFactory

object TradingSimulation extends App {

  implicit val hbaseConnection = ConnectionFactory.createConnection()

  val symbols = Seq("AAPL", "FB", "GILD", "KNDI", "MNKD", "NQ", "PLUG", "QQQ", "SPY", "TSLA", "VRNG")
  implicit val hBaseStockPriceDataSource = new HBaseStockPriceDataSource(YahooFinance)
  val stockPriceEventEmitter = new StockPriceEventEmitter(symbols, hBaseStockPriceDataSource)
  val marketEventsEmitter = new MarketEventsEmitter()

  object MyStrategy extends TradingStrategy {
    override def eventSources = Set(stockPriceEventEmitter, marketEventsEmitter)

    override def on(event: TradingEvent, portfolio: Portfolio) = {
      println(event)
      portfolio
    }
  }

  val start = OffsetDateTime.of(2014, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).toInstant
  val end = OffsetDateTime.of(2014, 12, 31, 23, 59, 59, 999999999, ZoneOffset.UTC).toInstant
  val context = new TradingContext(MyStrategy, start, end, Portfolio(start, 1000))
  context.run

  val hbaseTweetDataSource = new HBaseMicroblogDataSource(Default)
  //val tweetEventEmitter = new TweetEventEmitter(hbaseTweetDataSource)
}
