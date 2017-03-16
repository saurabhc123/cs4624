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
case class Portfolio(time: Instant, cash: Double, stocks: Map[String, Int] = Map.empty.withDefaultValue(0),
                     initialCashArgument: OptionalArgument[Double] = None)
                    (implicit stockPrices: StockPriceDataSource) {

  val initialCash: Double = initialCashArgument.getOrElse(cash)

  private def currentStockPrice(symbol: String, time: Instant = this.time): Double = {
    stockPrices.priceAtTime(symbol, time).map(_.price).getOrElse(0)
  }

  /**
    * @return total value of this portfolio (sum of cash and all owned stock values)
    */
  def portfolioValue: Double = stocks.foldLeft(cash) { case (value, (symbol, amount)) =>
    value + currentStockPrice(symbol) * amount
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
    val stockPrice = currentStockPrice(symbol, time = time)
    val amountDifferential = targetAmount - stocks(symbol)
    val cashDifferential = amountDifferential * stockPrice
    val cashAfterTransaction = cash - cashDifferential
    if (targetAmount < 0)
      Right(TransactionError(this, s"Attempted to sell shares of $$$symbol that are not owned."))
    else if (cashAfterTransaction < 0)
      Right(TransactionError(this, s"Not enough cash remaining to buy $amountDifferential shares of $$$symbol."))
    else
      Left(
        copy(
          time = time,
          cash = cashAfterTransaction,
          stocks = stocks.updated(symbol, targetAmount),
          initialCashArgument = initialCash
        )
      )
  }

  def withSharesPurchased(time: Instant, symbol: String, buyAmount: Int): Either[Portfolio, TransactionError] = {
    withSharesAtTargetAmount(time, symbol, stocks(symbol) + buyAmount)
  }

  def withSharesPurchasedAtValue(time: Instant, symbol: String, buyValue: Double): Either[Portfolio, TransactionError] = {
    withSharesAtTargetAmount(time, symbol, stocks(symbol) + (buyValue / currentStockPrice(symbol, time = time)).toInt)
  }

  def withSharesSold(time: Instant, symbol: String, sellAmount: Int): Either[Portfolio, TransactionError] = {
    withSharesAtTargetAmount(time, symbol, stocks(symbol) - sellAmount)
  }

  def withAllSharesSold(time: Instant): Portfolio = {
    copy(
      time = time,
      cash = portfolioValue,
      stocks = Map.empty.withDefaultValue(0),
      initialCashArgument = cash
    )
  }

  /**
    * Get the current value of the shares held for a given symbol.
    * @param symbol - the stock symbol.
    * @return the value of all of the shares in the portfolio with this symbol.
    */
  def stockValue(symbol: String): Double = {
    currentStockPrice(symbol) * stocks(symbol)
  }

  override def toString: String = super.toString + " Value: " + portfolioValue
}