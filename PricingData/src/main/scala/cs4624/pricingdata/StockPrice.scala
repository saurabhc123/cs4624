package cs4624.pricingdata

import org.joda.time.{DateTime, Instant}

/**
  * Created by joeywatts on 2/12/17.
  */
case class StockPrice(symbol: String, time: Instant, price: Double)

object StockPrices {

  implicit class RichTraversable(val trav: TraversableOnce[StockPrice]) {
    def getPrice(symbol: String, instant: Instant): Option[Double] = {
      val filtered = trav.filter(price => price.symbol == symbol && price.time.isBefore(instant))
      if (filtered.isEmpty) None else Some(filtered.maxBy(_.time.getMillis).price)
    }
  }
}