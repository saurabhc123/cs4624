package cs4624.pricingdata

import org.apache.hadoop.hbase.client.ConnectionFactory
import org.joda.time.{DateTime, DateTimeZone, LocalDate}

/**
  * Created by joeywatts on 2/12/17.
  */
object HBaseReadTester extends App {

  implicit val connection = ConnectionFactory.createConnection()
  val prices = StockPrices.query("AAPL",
    new LocalDate(2014, 1, 1).toDateTimeAtStartOfDay(DateTimeZone.UTC).toInstant,
    new LocalDate(2014, 12, 31).toDateTimeAtStartOfDay(DateTimeZone.UTC).toInstant)
  prices.foreach(println)

}
