package cs4624.pricingdata

import com.github.nscala_time.time.Imports._

/**
  * Created by joeywatts on 2/6/17.
  */
case class EndOfDayStockQuote(symbol: String,
                              date: LocalDate,
                              open: Double,
                              high: Double,
                              low: Double,
                              close: Double,
                              volume: Long,
                              adjustedClose: Double)
