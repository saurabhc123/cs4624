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
                              adjustedClose: Double) {
  def toCsv: String = s"$symbol,$date,$open,$high,$low,$close,$volume,$adjustedClose"
}
object EndOfDayStockQuote {
  def fromCsv(row: String): EndOfDayStockQuote = {
    val columns = row.split(",")
    EndOfDayStockQuote(
      symbol = columns(0),
      date = LocalDate.parse(columns(1)),
      open = columns(2).toDouble,
      high = columns(3).toDouble,
      low = columns(4).toDouble,
      close = columns(5).toDouble,
      volume = columns(6).toLong,
      adjustedClose = columns(7).toDouble
    )
  }
}

object EndOfDayStockQuotes {
  def fromCsv(lines: TraversableOnce[String]): Seq[EndOfDayStockQuote] = {
    lines.map(EndOfDayStockQuote.fromCsv).toSeq
  }
}
