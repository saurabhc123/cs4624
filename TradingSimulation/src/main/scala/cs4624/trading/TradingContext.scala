package cs4624.trading

import java.time.{Duration, Instant}

import cs4624.portfolio.Portfolio

import scala.collection.mutable

/**
  * Created by joeywatts on 2/27/17.
  */
class TradingContext[T](val strategy: TradingStrategy[T],
                     val start: Instant,
                     val end: Instant,
                     val initialPortfolio: T) {

  private class Events(val map: Map[TradingEventEmitter, Iterator[TradingEvent]]) extends Iterator[TradingEvent] {
    val queue: mutable.PriorityQueue[(TradingEventEmitter, TradingEvent)] =
      mutable.PriorityQueue()(Ordering.by { case (_, evt) => -evt.time.toEpochMilli })

    map.foreach { case (emitter, iter) =>
      if (iter.hasNext)
        queue.enqueue(emitter -> iter.next)
    }

    def next: TradingEvent = {
      val (emitter, event) = queue.dequeue()
      val iter = map(emitter)
      if (iter.hasNext)
        queue.enqueue(emitter -> iter.next)
      event
    }

    override def hasNext: Boolean = queue.nonEmpty
  }

  def run(afterEvent: (T, TradingEvent) => Unit = { (_, _) => () }): T = {
    // Get events for time interval.
    val events = new Events(
      strategy.eventSources.map(emitter => (emitter, emitter.eventsForInterval(start, end))).toMap
    )
    events.foldLeft(initialPortfolio) { case (portfolio, event) =>
      val result = strategy.on(event, portfolio)
      afterEvent(result, event)
      result
    }
  }
}
