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
    val put = new Put(Bytes.toBytes(s"$symbol"))
    put.addColumn(Bytes.toBytes("price"), Bytes.toBytes(""), time.getMillis, Bytes.toBytes(price))
    table.put(put)
    table.close()
  }
}

object StockPrices {

  val tableName: TableName = TableName.valueOf(Bytes.toBytes("cs4624:tweet_stock_trading:prices"))

  implicit class RichTraversable(val trav: TraversableOnce[StockPrice]) {
    def getPrice(symbol: String, instant: Instant): Option[Double] = {
      val filtered = trav.filter(price => price.symbol == symbol && price.time.isBefore(instant))
      if (filtered.isEmpty) None else Some(filtered.maxBy(_.time.getMillis).price)
    }
  }
}