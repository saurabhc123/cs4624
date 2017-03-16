package cs4624.prices

import java.time._
import java.time.temporal.ChronoUnit

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

  def openStockPrice: StockPrice = StockPrice(symbol, EndOfDayStockQuotes.openInstant(date), open)
  def closeStockPrice: StockPrice = StockPrice(symbol, EndOfDayStockQuotes.closeInstant(date), close)
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
  // One minute before the market open (the prices are set)
  def openInstant(date: LocalDate): Instant = OffsetDateTime.of(date, LocalTime.of(7, 59), ZoneOffset.UTC).toInstant
  // One minute before the market close (the prices are set)
  def closeInstant(date: LocalDate): Instant = OffsetDateTime.of(date, LocalTime.of(16, 59), ZoneOffset.UTC).toInstant
  def fromCsv(lines: TraversableOnce[String]): Seq[EndOfDayStockQuote] = {
    lines.map(EndOfDayStockQuote.fromCsv).toSeq
  }
}
