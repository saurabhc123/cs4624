package cs4624.trading.strategies

import cs4624.microblog.aggregation.AggregatedOpinions
import cs4624.microblog.sentiment.{Bearish, Bullish, SentimentAnalysisModel}
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
class BaselineStrategy(stock: String,
                       var portfolio: Portfolio,
                       sentimentAnalysisModel: SentimentAnalysisModel,
                       stockPriceDataSource: StockPriceDataSource) extends TradingStrategy {
  
  val aggregatedOpinions = new AggregatedOpinions(sentimentAnalysisModel, stockPriceDataSource, Duration.ofDays(1))
  private val log = LogManager.getRootLogger
  var isFirst = true
  var lastTimeOption: Option[Instant] = None

  override def currentPortfolio: Portfolio = portfolio

  override def on(event: TradingEvent): BaselineStrategy = {
    event match {
      case MicroblogPostEvent(post) if post.symbols.contains(stock) =>
        aggregatedOpinions.on(post)
      case MarketOpen(time) =>
        lastTimeOption match {
          case Some(lastTime) =>
            val opinion = aggregatedOpinions.sentimentForStock(stock)
            aggregatedOpinions.reset()
            portfolio = opinion match {
              case Some(Bullish) =>
                handleTrade(portfolio.withSharesPurchasedAtValue(time, stock, portfolio.cash))
              case Some(Bearish) =>
                handleTrade(portfolio.withSharesSold(time, stock, portfolio.stocks(stock)))
              case None =>
                portfolio
            }
            log.info(stock + " -> " + portfolio)
          case None =>
            lastTimeOption = Some(time)
        }
      case _ =>
    }
    this
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
