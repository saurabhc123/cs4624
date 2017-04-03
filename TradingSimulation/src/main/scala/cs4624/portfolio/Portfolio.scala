package cs4624.portfolio
import java.time.Instant

import cs4624.common.OptionalArgument
import cs4624.portfolio.error.TransactionError
import cs4624.prices.sources.StockPriceDataSource

/**
  * A class to represent a snapshot of your portfolio.
  * @param time - the time of the portfolio
  * @param cash - the amount of cash you can use to purchase stocks
  * @param stocks - a mapping of stock symbol to the amount that you currently own.
  */
case class Portfolio(time: Instant, cash: BigDecimal,
  stocks: Map[String, Int] = Map.empty.withDefaultValue(0),
  transactionFee: BigDecimal = BigDecimal("0.0"))
  (implicit stockPrices: StockPriceDataSource) {

  private def currentStockPrice(symbol: String, time: Instant = this.time): Option[BigDecimal] = {
    stockPrices.priceAtTime(symbol, time).map(_.price)
  }

  /**
    * @return total value of this portfolio (sum of cash and all owned stock values)
    */
  def portfolioValue: BigDecimal = stocks.foldLeft(cash) { case (value, (symbol, amount)) =>
    value + currentStockPrice(symbol).getOrElse(BigDecimal("0.0")) * amount
  }

  def withTime(time: Instant): Portfolio = copy(time = time)

  /**
    * Set the amount of shares for a symbol in this portfolio (selling/buying shares as needed to achieve that amount).
    * @param time - the time of this event.
    * @param symbol - the stock symbol
    * @param targetAmount - the amount of shares to own
    * @return a Portfolio with this transaction completed or an error detailing why the transaction did not occur.
    */
  def withSharesAtTargetAmount(time: Instant, symbol: String, targetAmount: Int): Either[Portfolio, TransactionError] = {
    currentStockPrice(symbol, time = time) match {
      case Some(stockPrice) =>
        val amountDifferential = targetAmount - stocks(symbol)
        val cashDifferential = amountDifferential * stockPrice
        val cashAfterTransaction = cash - cashDifferential - transactionFee
        if (amountDifferential == 0)
          Left(this.withTime(time))
        else if (targetAmount < 0)
          Right(TransactionError(this, s"Attempted to sell shares of $$$symbol that are not owned."))
        else if (cashAfterTransaction < 0)
          Right(TransactionError(this, s"Not enough cash remaining to buy $amountDifferential shares of $$$symbol."))
        else
          Left(
            copy(
              time = time,
              cash = cashAfterTransaction,
              stocks = stocks.updated(symbol, targetAmount)
            )
          )
      case None => Right(TransactionError(this, s"No price found for $$$symbol."))
    }
  }

  def withSharesPurchased(time: Instant, symbol: String, buyAmount: Int): Either[Portfolio, TransactionError] = {
    withSharesAtTargetAmount(time, symbol, stocks(symbol) + buyAmount)
  }

  def withSharesPurchasedAtValue(time: Instant, symbol: String, buyValue: BigDecimal): Either[Portfolio, TransactionError] = {
    currentStockPrice(symbol, time = time) match {
      case Some(stockPrice) =>
        withSharesAtTargetAmount(time, symbol, stocks(symbol) + ((buyValue - transactionFee) / stockPrice).toInt)
      case None => Right(TransactionError(this, s"No price found for $$$symbol."))
    }
  }

  def withSharesSold(time: Instant, symbol: String, sellAmount: Int): Either[Portfolio, TransactionError] = {
    withSharesAtTargetAmount(time, symbol, stocks(symbol) - sellAmount)
  }

  def withAllSharesSold(time: Instant): Portfolio = {
    copy(
      time = time,
      cash = portfolioValue - transactionFee,
      stocks = Map.empty.withDefaultValue(0)
    )
  }

  /**
    * Get the current value of the shares held for a given symbol.
    * @param symbol - the stock symbol.
    * @return the value of all of the shares in the portfolio with this symbol.
    */
  def stockValue(symbol: String): BigDecimal = {
    currentStockPrice(symbol).getOrElse(BigDecimal("0.0")) * stocks(symbol)
  }
}
