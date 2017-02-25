package cs4624.pricingdata

import org.apache.hadoop.hbase.client.ConnectionFactory
import java.time.{ZoneOffset, LocalDate}

/**
  * Created by joeywatts on 2/12/17.
  */
object HBaseReadTester extends App {

  implicit val connection = ConnectionFactory.createConnection()
  val prices = StockPrices.query("AAPL",
    LocalDate.of(2014, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant,
    LocalDate.of(2014, 12, 31).atStartOfDay(ZoneOffset.UTC).toInstant)
  prices.foreach(println)

}
