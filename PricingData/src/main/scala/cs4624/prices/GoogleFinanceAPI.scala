package cs4624.prices

import play.api.libs.ws.WSClient
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.net.URLEncoder
import java.nio.charset.Charset

import cs4624.common.CSV

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by joeywatts on 2/6/17.
  */
class GoogleFinanceAPI(implicit ws: WSClient) extends EODStockQuoteAPI {

  val dateTimeFormatter = DateTimeFormatter.ofPattern("d-MMM-yy")
  val queryParameterFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

  override def getQuotes(symbol: String, startDate: LocalDate, endDate: LocalDate)(implicit ec: ExecutionContext): Future[Seq[EndOfDayStockQuote]] = {
    ws.url ("http://www.google.com/finance/historical").withQueryString (
      "q" -> s"NASDAQ:$symbol",
      "startdate" -> startDate.format(queryParameterFormatter),
      "enddate" -> endDate.format(queryParameterFormatter),
      "output" -> "csv"
    ).get ().map (resp => {
      CSV.parse(resp.body).drop(1).flatMap {
        case dateStr :: openStr :: highStr :: lowStr :: closeStr :: volumeStr :: _ =>
          val date = LocalDate.parse(dateStr, dateTimeFormatter)
          val open = openStr.toDouble
          val high = highStr.toDouble
          val low = lowStr.toDouble
          val close = closeStr.toDouble
          val volume = volumeStr.toLong
          Some(EndOfDayStockQuote(symbol, date, open, high, low, close, volume, close))
        case _ => None
      }
    })
  }
}
