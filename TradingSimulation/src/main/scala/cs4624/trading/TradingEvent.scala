package cs4624.trading

import java.time.Instant


/**
  * TradingEvent represents any event that a trading strategy could react to.
  */
trait TradingEvent {
  def time: Instant
}

trait TradingEventEmitter {
  /**
    * @param start - the start of the time interval to get events for.
    * @param end - the end of the time interval to get events for.
    * @return a stream of events (presumed to be emitted in order of event time)
    */
  def eventsForInterval(start: Instant = Instant.now, end: Instant = Instant.MAX): Iterator[TradingEvent]
}