package cs4624.trading

import java.io.PrintWriter
import java.time.{Duration, Instant, OffsetDateTime, ZoneOffset, LocalDate}

import cs4624.trading.events.{MarketEventsEmitter, MarketOpen, StockPriceEventEmitter, MicroblogEventEmitter}
import cs4624.common.spark.SparkContextManager._
import cs4624.microblog.sentiment.SentimentAnalysisModel
import cs4624.microblog.sources.HBaseMicroblogDataSource
import cs4624.microblog.sources.HBaseMicroblogDataSource.Default
import cs4624.portfolio.Portfolio
import cs4624.prices.sources.HBaseStockPriceDataSource
import cs4624.prices.sources.HBaseStockPriceDataSource.WRDSTradesMinuteRes
import cs4624.prices.splits.StockSplit
import cs4624.prices.splits.sources.HardcodedStockSplitDataSource
import cs4624.trading.strategies.{BaselineStrategy, BuyHoldStrategy}
import org.apache.hadoop.hbase.client.ConnectionFactory
import cs4624.common.App
import cs4624.common.Http.client

object TradingSimulation extends App {

  implicit val hbaseConnection = ConnectionFactory.createConnection()

  val symbols = Set("AAPL", "FB", "GILD", "KNDI", "MNKD", "NQ", "PLUG", "QQQ", "SPY", "TSLA", "VRNG")
  implicit val hBaseStockPriceDataSource = new HBaseStockPriceDataSource(WRDSTradesMinuteRes)
  implicit val stockSplitDataSource = new HardcodedStockSplitDataSource(Seq(
    StockSplit("AAPL", LocalDate.of(2014, 6, 9), 7, 1),
    StockSplit("VRNG", LocalDate.of(2015, 11, 30), 1, 10)
  ).map(split => ((split.symbol, split.date), split)).toMap)
  val hbaseMicroblogDataSource = new HBaseMicroblogDataSource(Default)

  SentimentAnalysisModel.load("../cs4624_sentiment_analysis_model") match {
    case Some(sentimentAnalysisModel) =>
      val start = OffsetDateTime.of(2014, 1, 2, 7, 59, 59, 0, ZoneOffset.UTC).toInstant
      val end = OffsetDateTime.of(2014, 12, 31, 23, 59, 59, 999999999, ZoneOffset.UTC).toInstant
      val strategies = symbols.toSeq.map(s => new BaselineStrategy(s, Portfolio(start, 100000, transactionFee = BigDecimal("6.0")), sentimentAnalysisModel, hBaseStockPriceDataSource)) ++
        Seq(new BuyHoldStrategy("^GSPC", Portfolio(start, symbols.size * 100000.0, transactionFee = BigDecimal("6.0"))))
      val eventSources = Seq(new MarketEventsEmitter(), new MicroblogEventEmitter(hbaseMicroblogDataSource))
      val context = new TradingContext(strategies, eventSources, start, end)
      val printWriter = new PrintWriter("../results.csv")
      context.run {
        case (MarketOpen(time), strategies) =>
          val baselines = strategies.filter {
            case _: BaselineStrategy => true
            case _ => false
          }
          val buyHold = strategies.find {
            case _: BuyHoldStrategy => true
            case _ => false
          }.get
          val baselinePortfolioValue = baselines.map(_.currentPortfolio.portfolioValue).reduce(_ + _)
          val indexPortfolioValue = buyHold.currentPortfolio.portfolioValue
          println(s"Baseline Portfolio Value: $$$baselinePortfolioValue")
          println(s"Index Portfolio Value: $$$indexPortfolioValue")
          printWriter.println("\"" + time + "\"," + baselinePortfolioValue + "," + indexPortfolioValue)
          printWriter.flush()
        case _ =>
      }
      printWriter.close()
    case None => println("Could not load sentiment analysis model!")
  }
}
