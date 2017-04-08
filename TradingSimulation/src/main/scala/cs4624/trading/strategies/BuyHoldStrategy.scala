package cs4624.trading.strategies

import cs4624.portfolio.Portfolio
import cs4624.portfolio.error.TransactionError
import cs4624.trading._
import cs4624.trading.events._
import org.apache.log4j.LogManager

class BuyHoldStrategy(stock: String,
  var portfolio: Portfolio) extends TradingStrategy {

  private val log = LogManager.getRootLogger

  override def currentPortfolio: Portfolio = portfolio

  override def on(event: TradingEvent): TradingStrategy = {
    event match {
      case MarketOpen(time) =>
        portfolio = portfolio.withSplitAdjustments(time)
        portfolio = handleTrade(portfolio.withSharesPurchasedAtValue(time, stock, portfolio.cash))
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
