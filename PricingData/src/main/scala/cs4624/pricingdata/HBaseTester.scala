package cs4624.pricingdata

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.ConnectionFactory

/**
  * Created by joeywatts on 2/12/17.
  */
object HBaseTester extends App {
  val conf = HBaseConfiguration.create()
  conf.set("hbase.zookeeper.quorum", "")
  val connection = ConnectionFactory.createConnection()

  // Read all the quotes from the CSV and print them out.
  import scala.io.Source
  val quotes = EndOfDayStockQuotes.fromCsv(Source.fromFile("2014quotes.csv").getLines.drop(1))
  //quotes.foreach(println)
  val prices = quotes.flatMap(q => q.openStockPrice :: q.closeStockPrice :: Nil)
  prices.foreach(_.write)
}
