package cs4624.prices.test

import cs4624.common.App

import cs4624.prices.sources.HBaseStockPriceDataSource
import cs4624.prices.sources.wrds.WRDSTradePrices

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.ConnectionFactory

import java.time._
import java.time.temporal._
import cs4624.prices.StockPrice

object WRDSTradesDBPopulate extends App {
  import cs4624.common.spark.SparkContextManager.sc
  implicit val connection = ConnectionFactory.createConnection(HBaseConfiguration.create())
  val hbaseStockPriceDataSource = new HBaseStockPriceDataSource(HBaseStockPriceDataSource.WRDSTradesMinuteRes)
  val iter = WRDSTradePrices.fromCSV("../WRDS_trades_2014.csv")
  var prices = List[StockPrice]()
  def truncatedTime(time: Instant) = OffsetDateTime.ofInstant(time, ZoneOffset.UTC).truncatedTo(ChronoUnit.MINUTES)
  while (iter.hasNext) {
    val nextPrice = iter.next
    val sameTick = prices.isEmpty || (truncatedTime(nextPrice.time) == truncatedTime(prices.head.time) &&
      nextPrice.symbol == prices.head.symbol)
    if (sameTick) {
      prices = nextPrice :: prices
    }
    if (!sameTick || !iter.hasNext) {
      val averagePrice = prices.map(_.price).sum / prices.size
      val tick = StockPrice(prices.head.symbol, truncatedTime(prices.head.time).toInstant, averagePrice)
      println(tick)
      hbaseStockPriceDataSource.write(tick)
    }
    if (!sameTick) {
      prices = nextPrice :: Nil
    }
  }
}
