package cs4624.trading.strategies

import cs4624.microblog.aggregation.AggregatedOpinions
import cs4624.microblog.sentiment.{Bearish, Bullish, SentimentAnalysisModel}
import cs4624.microblog.sources.MicroblogDataSource
import cs4624.portfolio.Portfolio
import cs4624.prices.sources.StockPriceDataSource
import cs4624.trading.{TradingEvent, TradingStrategy}
import cs4624.common.OptionalArgument
import java.time.{Duration, Instant}

import cs4624.portfolio.error.TransactionError
import cs4624.trading.events._
import org.apache.log4j.LogManager

/**
  * Created by joeywatts on 3/15/17.
  */
class TestStrategy(stocks: Set[String],
                   sentimentAnalysisModel: SentimentAnalysisModel,
                   stockPriceDataSource: StockPriceDataSource,
                   microblogDataSource: MicroblogDataSource,
                   fileName: OptionalArgument[String] = None) extends TradingStrategy[Map[String, Portfolio]] {
  
  val aggregatedOpinions = new AggregatedOpinions(sentimentAnalysisModel, stockPriceDataSource, Duration.ofDays(1))
  private val log = LogManager.getRootLogger
  var isFirst = true
  var lastTimeOption: Option[Instant] = None

  //val stockPrices: Map[String, StockPriceEventEmitter] = stocks.map(s => (s, new StockPriceEventEmitter(s, stockPriceDataSource))).toMap

  override def eventSources = Set(
    new MicroblogEventEmitter(microblogDataSource),
    new MarketEventsEmitter()
  )

  override def on(event: TradingEvent, portfolios: Map[String, Portfolio]): Map[String, Portfolio] = {
    event match {
      case MicroblogPostEvent(post) =>
        aggregatedOpinions.on(post)
        portfolios
      case MarketOpen(time) =>
        lastTimeOption match {
          case Some(lastTime) =>
            val opinions = stocks.map { stock =>
              (stock, aggregatedOpinions.sentimentForStock(stock, (lastTime, time)))
            }
            println(opinions)
            aggregatedOpinions.reset()
            val resultPortfolio = opinions.foldLeft(portfolios) {
              case (portfolioMap, (stock, Some(Bullish))) =>
                portfolioMap.get(stock).map(portfolio =>
                  handleTrade(portfolio.withSharesPurchasedAtValue(time, stock, portfolio.cash))
                ) match {
                  case Some(portfolio) => portfolioMap.updated(stock, portfolio)
                  case None => portfolioMap
                }
              case (portfolioMap, (stock, Some(Bearish))) =>
                portfolioMap.get(stock).map(portfolio =>
                  handleTrade(portfolio.withSharesSold(time, stock, portfolio.stocks(stock)))
                ) match {
                  case Some(portfolio) => portfolioMap.updated(stock, portfolio)
                  case None => portfolioMap
                }
              case (portfolioMap, _) => portfolioMap
            }
            resultPortfolio.foreach { kv => log.info(kv) }
            log.info("Total portfolio value: " + resultPortfolio.map(_._2.portfolioValue).reduce(_ + _))
            resultPortfolio
          case None =>
            lastTimeOption = Some(time)
            portfolios
        }
      case _ => portfolios
    }
  }

  private def handleTrade(retValue: Either[Portfolio, TransactionError]): Portfolio = {
    retValue match {
      case Left(portfolio) => portfolio
      case Right(transactionError) =>
        log.error(transactionError.message)
        transactionError.portfolio
    }
  }

}
