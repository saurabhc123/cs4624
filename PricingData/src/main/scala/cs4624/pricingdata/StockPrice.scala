package cs4624.pricingdata

import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.{Connection, Put}
import org.apache.hadoop.hbase.util.Bytes
import org.joda.time.{DateTime, Instant}

/**
  * Created by joeywatts on 2/12/17.
  */
case class StockPrice(symbol: String, time: Instant, price: Double) {
  def write(implicit connection: Connection): Unit = {
    val table = connection.getTable(StockPrices.tableName)
    val put = new Put(Bytes.toBytes(symbol))
    put.addColumn(Bytes.toBytes("price"), Bytes.toBytes(""), time.getMillis, Bytes.toBytes(price))
    table.put(put)
    table.close()
  }
}

object StockPrices {

  val tableName: TableName = TableName.valueOf(Bytes.toBytes("tweetstocktrading_prices"))

  implicit class RichTraversableOnce(val trav: TraversableOnce[StockPrice]) {
    def getPrice(symbol: String, instant: Instant): Option[Double] = {
      val filtered = trav.filter(price => price.symbol == symbol && price.time.isBefore(instant))
      if (filtered.isEmpty) None else Some(filtered.maxBy(_.time.getMillis).price)
    }
  }

  implicit class RichTraversable(val trav: Traversable[StockPrice]) {
    def writeAll(implicit connection: Connection): Unit = {
      trav.groupBy(_.symbol).foreach { case (symbol, prices) =>
        val table = connection.getTable(tableName)
        val put = prices.foldLeft(new Put(Bytes.toBytes(symbol))) { case (put, price) =>
          put.addColumn(Bytes.toBytes("price"), Bytes.toBytes(""), price.time.getMillis, Bytes.toBytes(price.price))
        }
        table.put(put)
        table.close()
      }
    }
  }
}