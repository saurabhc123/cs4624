package cs4624.prices

import cs4624.common.spark.SparkContextManager
import cs4624.prices.sources.HBaseStockPriceDataSource
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client._
import org.apache.spark.SparkContext

/**
  * Created by joeywatts on 2/12/17.
  */
object HBaseTester extends App {
  implicit val connection = ConnectionFactory.createConnection(HBaseConfiguration.create())

  // Read all the quotes from the CSV and print them out.
  import scala.io.Source
  val quotes = EndOfDayStockQuotes.fromCsv(Source.fromFile("vrng.csv").getLines.drop(1))
  //quotes.foreach(println)
  val prices = quotes.flatMap(q => q.openStockPrice :: q.closeStockPrice :: Nil)
  implicit val sc: SparkContext = SparkContextManager.getContext
  val dataSource = new HBaseStockPriceDataSource(HBaseStockPriceDataSource.YahooFinance)
  dataSource.write(prices)
}
