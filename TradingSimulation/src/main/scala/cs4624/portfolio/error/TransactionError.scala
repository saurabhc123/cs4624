package cs4624.portfolio.error

import cs4624.portfolio.Portfolio

/**
  * Created by joeywatts on 3/1/17.
  */
case class TransactionError(portfolio: Portfolio, message: String)
