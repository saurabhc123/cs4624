package cs4624.pricingdata

import java.time.{Instant, LocalDate}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by joeywatts on 2/6/17.
  */
trait EODStockQuoteAPI {
  def getQuotes(symbol: String, startDate: LocalDate, endDate: LocalDate)(implicit ec: ExecutionContext): Future[Seq[EndOfDayStockQuote]]
  def getQuote(symbol: String, date: LocalDate)(implicit ec: ExecutionContext): Future[Option[EndOfDayStockQuote]] =
    getQuotes(symbol, date, date)(ec).map { _.headOption }
}
