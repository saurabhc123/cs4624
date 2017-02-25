package cs4624.pricingdata

import play.api.libs.ws.WSClient
import play.api.libs.ws.ning.NingWSClient

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

import java.time.{LocalDate, LocalTime, ZonedDateTime, ZoneOffset}

/**
  * Example of using this library to get end-of-day stock quotes.
  *
  * Created by joeywatts on 2/6/17.
  */
object APITester extends App {
  val ws: WSClient = NingWSClient()
  val api = new YahooFinanceAPI(ws)

  val symbols = Seq("AAPL", "FB", "GILD", "KNDI", "MNKD", "NQ", "PLUG", "QQQ", "SPY", "TSLA", "VRNG")
  val future = Future.sequence(symbols.map { sym =>
    api.getQuotes(symbol = sym, startDate = LocalDate.of(2014, 1, 1), endDate = LocalDate.of(2014, 12, 31))
  })

  // Await the results from the future, write to a CSV, cleanup http client.
  try {
    import scala.concurrent.duration._
    val lines = Await.result(future, Duration.Inf).flatten.map(_.toCsv)
    import java.io._
    val pw = new PrintWriter(new File("2014quotes.csv"))
    // print a header.
    pw.println("Symbol,Date,Open,High,Low,Close,Volume,AdjustedClose")
    lines.foreach(pw.println)
    pw.close()
  } finally {
    ws.close()
  }

  // Read all the quotes from the CSV and print them out.
  import scala.io.Source
  val quotes = EndOfDayStockQuotes.fromCsv(Source.fromFile("2014quotes.csv").getLines.drop(1))
  //quotes.foreach(println)
  val prices = quotes.flatMap(q => q.openStockPrice :: q.closeStockPrice :: Nil)
  import StockPrices._ // Import the implicit class with the "getPrice" method.
  println(prices.getPrice("AAPL", ZonedDateTime.of(LocalDate.of(2014, 6, 26), LocalTime.of(12, 14), ZoneOffset.UTC).toInstant))
}
