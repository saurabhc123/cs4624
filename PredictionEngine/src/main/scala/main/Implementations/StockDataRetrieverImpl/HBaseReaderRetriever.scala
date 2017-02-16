package main.Implementations.StockDataRetrieverImpl

import java.time.Instant

import main.Interfaces.IStockDataRetriever
import main.copied_module.pricingdata.{StockPrice, StockPrices,_}

/**
  * Created by Eric on 2/12/2017.
  */
class HBaseReaderRetriever(symbols: Seq[String], start: Instant, end: Instant) extends IStockDataRetriever{

  private val joda_start = new org.joda.time.Instant(start)
  private val joda_end = new org.joda.time.Instant(end)
  private val priceMap = symbols.map(sym => sym -> StockPrices.query(sym,joda_start,joda_end)).toMap

  override def getPriceOfStock(stock: String, currentTime: Instant): Double = {
    val trav : Traversable[StockPrice] = priceMap.getOrElse(stock, Seq[StockPrice]())
    StockPrices.RichTraversableOnce(trav).getPrice(stock,new org.joda.time.Instant(currentTime)).getOrElse(-1)
  }

  override def getAllStockPrices(stock: String): Seq[(Double, Instant)] = {
    priceMap.getOrElse(stock, Seq[StockPrice]()).map(sp => (sp.price,new Instant(sp.time)))
  }
}
