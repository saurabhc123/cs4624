package cs4624.trading

import java.io.PrintWriter
import java.time.{Duration, Instant, OffsetDateTime, ZoneOffset}

import cs4624.trading.events.{MarketEventsEmitter, MarketOpen, StockPriceEventEmitter}
import cs4624.common.spark.SparkContextManager._
import cs4624.microblog.sentiment.SentimentAnalysisModel
import cs4624.microblog.sources.HBaseMicroblogDataSource
import cs4624.microblog.sources.HBaseMicroblogDataSource.Default
import cs4624.portfolio.Portfolio
import cs4624.prices.sources.HBaseStockPriceDataSource
import cs4624.prices.sources.HBaseStockPriceDataSource.YahooFinance
import cs4624.trading.strategies.TestStrategy
import org.apache.hadoop.hbase.client.ConnectionFactory
import cs4624.common.App
import cs4624.common.Http.client

object TradingSimulation extends App {

  implicit val hbaseConnection = ConnectionFactory.createConnection()

  val symbols = Set("AAPL", "FB", "GILD", "KNDI", "MNKD", "NQ", "PLUG", "QQQ", "SPY", "TSLA")
  implicit val hBaseStockPriceDataSource = new HBaseStockPriceDataSource(YahooFinance)
  val hbaseTweetDataSource = new HBaseMicroblogDataSource(Default)

  SentimentAnalysisModel.load("../cs4624_sentiment_analysis_model") match {
    case Some(sentimentAnalysisModel) =>
      val testStrategy = new TestStrategy(symbols, sentimentAnalysisModel, hBaseStockPriceDataSource, hbaseTweetDataSource, "results.csv")
      val start = OffsetDateTime.of(2014, 1, 2, 7, 59, 59, 0, ZoneOffset.UTC).toInstant
      val end = OffsetDateTime.of(2014, 12, 31, 23, 59, 59, 999999999, ZoneOffset.UTC).toInstant
      val startingPortfolios = symbols.map(_ -> Portfolio(start, 100000)).toMap
      val context = new TradingContext(testStrategy, start, end, startingPortfolios)
      val printWriter = new PrintWriter("../results.csv")
      context.run {
        case (portfolio, MarketOpen(time)) =>
          printWriter.println("\"" + time + "\"," + portfolio.foldLeft(0.0) { case (acc, (_, p)) => acc + p.portfolioValue })
          printWriter.flush()
        case _ =>
      }
      printWriter.close()
    case None => println("Could not load sentiment analysis model!")
  }
}
