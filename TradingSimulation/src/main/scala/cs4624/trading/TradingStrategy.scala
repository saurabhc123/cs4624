package cs4624.trading

import cs4624.portfolio.Portfolio

/**
  * A trading strategy simply manipulates a portfolio by reacting to data received from various events.
  */
trait TradingStrategy {
  def eventSources: Set[TradingEventEmitter]
  def on(event: TradingEvent, portfolio: Portfolio): Portfolio
}
