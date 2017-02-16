package main.Interfaces

import java.time.Instant

/**
  * Created by Eric on 2/3/2017.
  */
trait IStockDataRetriever
{

  def getPriceOfStock(stock: String, currentTime : Instant): Double

  def getAllStockPrices(stock: String): Seq[(Double, Instant)]

}
