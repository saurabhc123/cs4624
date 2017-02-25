package main.portfolio
import scala.collection.mutable.ListBuffer
import scala.io


/**
  * Created by joeywatts on 2/11/17.
  */
class Portfolio {


  var stockList: scala.collection.mutable.ListBuffer[Stock] = ListBuffer()
  var currentCash: Double = 0


  def buy(stockName : String, stockPurchaseAmount : Double, buyDate : String) {
    var buyingStock : Stock = stockInPortfolio(stockName)
    if(buyingStock == null){
      buyingStock = new Stock(stockName)
      stockList += buyingStock
    }
    try {
      currentCash -= buyingStock.buy(stockPurchaseAmount, buyDate)
    }
    catch
      {
        case e: IllegalArgumentException => System.err.println("Invalid Trade\t" + stockName + stockPurchaseAmount.toString + buyDate)
      }

  }

  def sell(stockName : String, sellDate : String){
    var sellStock : Stock = stockInPortfolio(stockName)
    if(sellStock != null){
      try {
        stockList -= sellStock
        currentCash += sellStock.sell(sellDate)
      }
      catch
        {
          case e: IllegalArgumentException => System.err.println("Invalid Trade\t" + stockName + sellDate)
        }


    }
  }

  def stockInPortfolio(stockName: String): Stock = {
    for (currStock <- stockList if currStock.name == stockName) return currStock

    null
  }

  def portfolioValue(portDate: String): Double = {
    var value : Double = 0
    for (currStock <- stockList) value += currStock.stockValue(portDate)
    value
  }

  def portfolioOverview(portDate: String): String = {
    var overview : String = ""

    overview += this.toString(portDate)
    overview += "\n" + "Stock Value:\t" + portfolioValue(portDate).toString
    overview += "\n" + "Cash:\t" + currentCash.toString
    overview += "\n" + "Overall Value:\t" + (portfolioValue(portDate) + currentCash).toString +"\n"
    overview
  }

  override def toString: String =
  {
    var returnString : String = ""
    for (currStock <- stockList) returnString += currStock.toString + "\n"
    var cashString : String = String.valueOf(currentCash)

    returnString += cashString
    returnString
  }

  def toString(date: String): String =
  {
    var returnString : String = ""
    for (currStock <- stockList) returnString += currStock.toString + ", " + currStock.stockValue(date) + "\n"
    var cashString : String = String.valueOf(currentCash)


    returnString
  }
}

object PortfolioApp extends App {
  val port:Portfolio = new Portfolio
  var currentDate: String = ""
  val bufferedSource = io.Source.fromFile("TransacDemo.csv")
  for (line <- bufferedSource.getLines) {
    val cols = line.split(",").map(_.trim)
    if(cols.length == 1)
      {
        if(currentDate != ""){
          println(port.portfolioOverview(currentDate))
        }
        currentDate = cols(0)
        println(currentDate)
      }
    else if(cols.length == 2)
      {
        port.sell(cols(1), currentDate)
      }
    else if(cols.length == 3)
      {
        port.buy(cols(1), cols(2).toDouble, currentDate)
      }

  }
  println(port.portfolioOverview(currentDate))
  bufferedSource.close




}
