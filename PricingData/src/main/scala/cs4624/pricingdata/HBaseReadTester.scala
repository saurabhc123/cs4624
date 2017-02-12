package cs4624.pricingdata

import org.apache.hadoop.hbase.client.ConnectionFactory
import org.joda.time.DateTime

/**
  * Created by joeywatts on 2/12/17.
  */
object HBaseReadTester extends App {

  val connection = ConnectionFactory.createConnection()
  val prices = StockPrices.query("AAPL", new DateTime(2014, 1, 1).toInstant, new DateTime(2014, 12, 31).toInstant)
  prices.foreach(println)

}
