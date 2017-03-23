package cs4624.microblog.sentiment

/**
  * Created by joeywatts on 3/1/17.
  */
sealed trait Sentiment {
  def label: Double
}
object Sentiment {
  def fromLabel(label: Double): Option[Sentiment] = {
    label match {
      case 1.0 => Some(Bullish)
      case 0.0 => Some(Bearish)
      case label => 
        println(label)
        None
    }
  }
}
case object Bullish extends Sentiment {
  override def label = 1
}
case object Bearish extends Sentiment {
  override def label = 0
}
