package cs4624.trading.events

import java.time.Instant

import cs4624.microblog.MicroblogPost
import cs4624.microblog.sources.MicroblogDataSource
import cs4624.trading.{TradingEvent, TradingEventEmitter}

case class MicroblogPostEvent(post: MicroblogPost) extends TradingEvent {
  override def time = post.time
}

class MicroblogEventEmitter(microblogDataSource: MicroblogDataSource) extends TradingEventEmitter {

  override def eventsForInterval(start: Instant, end: Instant) = {
    microblogDataSource.query(startTime = start, endTime = end).map(MicroblogPostEvent)
  }

}
