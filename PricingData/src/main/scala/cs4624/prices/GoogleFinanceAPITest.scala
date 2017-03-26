package cs4624.prices

import cs4624.common.App
import cs4624.common.Http.client

import java.time.LocalDate
import java.io.PrintWriter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object GoogleFinanceAPITest extends App {
  val api = new GoogleFinanceAPI()
  val future = api.getQuotes("VRNG", LocalDate.of(2014, 1, 1), LocalDate.of(2014, 12, 31))
  val quotes = Await.result(future, Duration.Inf)
  val pw = new PrintWriter("vrng.csv")
  pw.println("Symbol,Date,Open,High,Low,Close,Volume,AdjustedClose")
  quotes.map(_.toCsv).foreach(pw.println)
  pw.close()
}