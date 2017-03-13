package cs4624.microblog

import java.time.Instant

import cs4624.microblog.sentiment.Sentiment

/**
  * Created by joeywatts on 2/28/17.
  */
case class MicroblogPost(id: String,
                         text: String,
                         author: MicroblogAuthor,
                         time: Instant,
                         sentiment: Option[Sentiment] = None,
                         symbols: Seq[String] = Seq())