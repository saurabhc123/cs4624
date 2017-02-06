package cs4624.pricingdata

import com.github.nscala_time.time.Imports._
import play.api.libs.ws.WSClient
import play.api.libs.ws.ning.NingWSClient

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Example of using this library to get end-of-day stock quotes.
  *
  * Created by joeywatts on 2/6/17.
  */
object APITester extends App {
  val ws: WSClient = NingWSClient()
  val api = new YahooFinanceAPI(ws)
  val future = api.getQuotes(symbol = "AAPL", startDate = new LocalDate(2017, 1, 1), endDate = LocalDate.yesterday())
  try {
    import scala.concurrent.duration._
    Await.result(future, Duration.Inf) foreach println
  } finally {
    ws.close()
  }
}
