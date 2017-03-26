package cs4624.prices

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

import java.time.{LocalDate, LocalTime, ZonedDateTime, ZoneOffset}

import cs4624.common.App
import cs4624.common.Http.client

/**
  * Example of using this library to get end-of-day stock quotes.
  *
  * Created by joeywatts on 2/6/17.
  */
object APITester extends App {
  val api = new YahooFinanceAPI()

  val symbols = Seq("VRNG")// Seq("AAPL", "FB", "GILD", "KNDI", "MNKD", "NQ", "PLUG", "QQQ", "SPY", "TSLA", "VRNG")
  val future = Future.sequence(symbols.map { sym =>
    api.getQuotes(symbol = sym, startDate = LocalDate.of(2014, 1, 1), endDate = LocalDate.of(2014, 12, 31))
  })

  // Await the results from the future, write to a CSV, cleanup http client.
  import scala.concurrent.duration._
  val lines = Await.result(future, Duration.Inf).flatten.sortBy(_.date.toEpochDay).map(_.toCsv)
  //lines.foreach(println)
  /*val first = lines.head.openStockPrice
  val amountHeld = 1000000.0/first.price
  val csvLines = lines.map(_.openStockPrice).map(price => "\"" + price.time + "\"," + amountHeld*price.price)
  import java.io._
  val pw = new PrintWriter(new File("sp500.csv"))
  csvLines.foreach(pw.println)
  pw.close()*/
  import java.io._
  val pw = new PrintWriter(new File("vrng.csv"))
  // print a header.
  pw.println("Symbol,Date,Open,High,Low,Close,Volume,AdjustedClose")
  lines.foreach(pw.println)
  pw.close()

  // Read all the quotes from the CSV and print them out.
  /*import scala.io.Source
  val quotes = EndOfDayStockQuotes.fromCsv(Source.fromFile("2014quotes.csv").getLines.drop(1))
  quotes.foreach(println)*/
}
