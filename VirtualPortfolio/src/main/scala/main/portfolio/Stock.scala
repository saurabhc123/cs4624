package main.portfolio

import scala.io


/**
  * Created by Nick on 2/12/2017.
  */
class Stock (namec: String){
  var name :String = namec
  var holdingAmount :Int = 0
  var cost :Double = 0

  def buy(purchaseAmount: Double, purchaseDate: String) : Double ={
    //calculate amount by dividing puchase amount by stock price
    var purchaseUnit: Int = (purchaseAmount/currentPrice(purchaseDate)).toInt
    holdingAmount += purchaseUnit
    cost += purchaseUnit * currentPrice(purchaseDate)//stockprice
    cost
  }

  def sell(sellDate: String) : Double = {
    val sellValue: Double = holdingAmount / currentPrice(sellDate) //stockprice
    holdingAmount = 0
    cost = 0

    sellValue
  }



  def currentPrice(stockDate: String) : Double = {
    val bufferedSource = io.Source.fromFile("2014quotes.csv")
    for (line <- bufferedSource.getLines) {
      val cols = line.split(",").map(_.trim)
      if(cols(0) == name && cols(1)== stockDate){
        bufferedSource.close
        return cols(2).toDouble
      }
    }
    bufferedSource.close
    throw new IllegalArgumentException("Invalid Date or Symbol")
    //Retrieve from csv file
    
  }

  def stockValue(stockDate: String) : Double = {
    var value: Double = holdingAmount * currentPrice(stockDate)
    if(value < 0) return 0
    value
  }

  override def toString : String = {

    val outputString : String = name + ", "+ holdingAmount + ", " + cost
    outputString
  }


}

object StockApp extends App {
  var stock1: Stock = new Stock("GILD")
  println(stock1)
  stock1.buy(20000, "2014-01-10")
  println(stock1 + ", " + stock1.stockValue("2014-02-10").toString)
  println(stock1.currentPrice("2014-01-10"))
}

