package cs4624.prices

import java.time._
import java.time.temporal.ChronoUnit

/**
  * Created by joeywatts on 2/6/17.
  */
case class EndOfDayStockQuote(symbol: String,
                              date: LocalDate,
                              open: BigDecimal,
                              high: BigDecimal,
                              low: BigDecimal,
                              close: BigDecimal,
                              volume: Long,
                              adjustedClose: BigDecimal) {
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
      open = BigDecimal(columns(2)),
      high = BigDecimal(columns(3)),
      low = BigDecimal(columns(4)),
      close = BigDecimal(columns(5)),
      volume = columns(6).toLong,
      adjustedClose = BigDecimal(columns(7))
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
