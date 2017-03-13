package cs4624.trading.events

import java.time.{Instant, OffsetDateTime, OffsetTime, ZoneOffset}

import cs4624.trading.{TradingEvent, TradingEventEmitter}

/**
  * Events to react to the market open and close.
  */
case class MarketOpen(override val time: Instant) extends TradingEvent
case class MarketClose(override val time: Instant) extends TradingEvent

class MarketEventsEmitter extends TradingEventEmitter {
  val openTime = OffsetTime.of(8, 0, 0, 0, ZoneOffset.UTC)
  val closeTime = OffsetTime.of(17, 0, 0, 0, ZoneOffset.UTC)

  private class MarketEventIterator(val start: OffsetDateTime, val end: OffsetDateTime) extends Iterator[TradingEvent] {
    var dateTime = start

    private def nextOpen(dateTime: OffsetDateTime): OffsetDateTime = {
      OffsetDateTime.of(dateTime.toLocalDate.plusDays(
        if (!dateTime.toOffsetTime.isBefore(openTime)) 1 else 0
      ), openTime.toLocalTime, ZoneOffset.UTC)
    }
    private def nextClose(dateTime: OffsetDateTime): OffsetDateTime = {
      OffsetDateTime.of(dateTime.toLocalDate.plusDays(
        if (!dateTime.toOffsetTime.isBefore(closeTime)) 1 else 0
      ), closeTime.toLocalTime, ZoneOffset.UTC)
    }
    private def peekNext: OffsetDateTime = {
      val nextOpenTime = nextOpen(dateTime)
      val nextCloseTime = nextClose(dateTime)
      if (nextOpenTime.isBefore(nextCloseTime)) {
        nextOpenTime
      } else {
        nextCloseTime
      }
    }

    override def hasNext: Boolean = {
      peekNext.isBefore(end)
    }

    override def next: TradingEvent = {
      val nextOpenTime = nextOpen(dateTime)
      val nextCloseTime = nextClose(dateTime)
      if (nextOpenTime.isBefore(nextCloseTime)) {
        dateTime = nextOpenTime
        MarketOpen(dateTime.toInstant)
      } else {
        dateTime = nextCloseTime
        MarketClose(dateTime.toInstant)
      }
    }
  }

  override def eventsForInterval(start: Instant, end: Instant): Iterator[TradingEvent] = {
    val startDateTime = start.atOffset(ZoneOffset.UTC)
    val endDateTime = end.atOffset(ZoneOffset.UTC)
    new MarketEventIterator(startDateTime, endDateTime)
  }
}