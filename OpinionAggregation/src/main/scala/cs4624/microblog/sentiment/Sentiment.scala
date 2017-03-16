package cs4624.microblog.sentiment

/**
  * Created by joeywatts on 3/1/17.
  */
sealed trait Sentiment {
  def label: Double
}
object Sentiment {
  def fromLabel(label: Double): Sentiment = {
    if (label < 0.9) Bearish else Bullish
  }
}
case object Bullish extends Sentiment {
  override def label = 1
}
case object Bearish extends Sentiment {
  override def label = 0
}
