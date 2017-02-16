package main.Factories

import java.time.Instant

import main.Implementations.StockDataRetrieverImpl.HBaseReaderRetriever
import main.Interfaces.IStockDataRetriever

/**
  * Created by Eric on 2/12/2017.
  */
object StockDataRetrieverFactory {
  def getStockDataRetriever(start:Instant, end: Instant): IStockDataRetriever ={
    val ret = new HBaseReaderRetriever(start,end)
    ret
  }

}
