package cs4624.prices

import java.time.Instant

/**
  * Created by joeywatts on 2/12/17.
  */
case class StockPrice(symbol: String, time: Instant, price: BigDecimal)

