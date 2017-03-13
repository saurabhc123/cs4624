package cs4624.prices.sources

import java.time.Instant

import cs4624.common.OptionalArgument
import cs4624.prices.StockPrice
import it.nerdammer.spark.hbase._
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import scala.collection.JavaConversions._

/**
  * Created by joeywatts on 2/27/17.
  */
class HBaseStockPriceDataSource(val table: HBaseStockPriceDataSource.Table)
                               (implicit val connection: Connection, implicit val sc: SparkContext)
  extends StockPriceDataSource {

  private val hbaseTable = connection.getTable(TableName.valueOf(table.name))

  override def priceAtTime(symbol: String, time: Instant): Option[StockPrice] = {
    // this uses the HBase java API instead of the Spark API because
    // the Spark API doesn't support reverse scans.
    val scan = new Scan()
    scan.setStartRow(Bytes.toBytes(s"${symbol}_${time.toString}"))
    scan.setReversed(true)
    val scanner = hbaseTable.getScanner(scan)
    if (scanner.isEmpty) None else Some(resultToStockPrice(scanner.next))
  }

  def resultToStockPrice(result: Result): StockPrice = {
    val rowKey = Bytes.toString(result.getRow)
    val splitRowKey = rowKey.split("_")
    val symbol = splitRowKey(0)
    val time = Instant.parse(splitRowKey(1))
    val price = Bytes.toString(result.getValue(HBaseStockPriceDataSource.priceCF, HBaseStockPriceDataSource.priceCQ)).toDouble
    StockPrice(symbol, time, price)
  }

  override def query(symbol: String,
                     startTime: OptionalArgument[Instant],
                     endTime: OptionalArgument[Instant]): RDD[StockPrice] = {
    sc.hbaseTable[(String, String)](table.name)
      .select("price:price")
      .withStartRow(s"${symbol}_${startTime.map(_.toString).getOrElse("")}")
      .withStopRow(s"${symbol}_${endTime.map(_.toString).getOrElse("z")}") // z is used here to ensure that it will return all rows ('z' is greater than any digit)
      .map { case (row, price) =>
        val s = row.split("_")
        StockPrice(s(0), Instant.parse(s(1)), price.toDouble)
      }
  }

  def rowKey(symbol: String, time: Instant): Array[Byte] = Bytes.toBytes(symbol + "_" + time.toString)

  def write(prices: RDD[StockPrice]): Unit = {
    prices.map(price => {
      (price.symbol + "_" + price.time.toString, price.price.toString)
    }).toHBaseTable(table.name)
      .toColumns("price:price")
      .save()
  }
  def write(prices: Seq[StockPrice]): Unit = write(sc.parallelize(prices))
}

object HBaseStockPriceDataSource {
  sealed trait Table { def name: String }
  case object YahooFinance extends Table { override def name: String = "stockprices_yahoo" }

  val priceCF: Array[Byte] = Bytes.toBytes("price")
  val priceCQ: Array[Byte] = Bytes.toBytes("price")
}
