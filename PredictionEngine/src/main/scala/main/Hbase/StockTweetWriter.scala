package main.Hbase

import main.StockTweet
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.util.Bytes

/**
  * Created by Eric on 2/12/2017.
  */
object StockTweetWriter {
  val interactor = new HBaseInteraction("stock_tweets")


  def write(stockTweet : StockTweet) = {
    val put = new Put(Bytes.toBytes(stockTweet.id))
    put.addColumn(Bytes.toBytes("base_data"),Bytes.toBytes("timestamp"), Bytes.toBytes(stockTweet.timestamp.toString))
    put.addColumn(Bytes.toBytes("base_data"),Bytes.toBytes("text"), Bytes.toBytes(stockTweet.text))
    put.addColumn(Bytes.toBytes("base_data"),Bytes.toBytes("judgeid"), Bytes.toBytes(stockTweet.judgeId))
    if (stockTweet.sentimentOrder.isDefined){
      put.addColumn(Bytes.toBytes("options"), Bytes.toBytes("sentimentorder"), Bytes.toBytes(stockTweet.sentimentOrder.get.toString))
    }
    if (stockTweet.rawPredictionScore.isDefined){
      put.addColumn(Bytes.toBytes("options"), Bytes.toBytes("rawPredictionScore"), Bytes.toBytes(stockTweet.rawPredictionScore.get.toString))
    }
    if (stockTweet.symbol.isDefined){
      put.addColumn(Bytes.toBytes("options"), Bytes.toBytes("symbol"), Bytes.toBytes(stockTweet.symbol.get.toString))
    }
    if (stockTweet.sentiment.isDefined){
      put.addColumn(Bytes.toBytes("options"), Bytes.toBytes("sentiment"), Bytes.toBytes(stockTweet.sentiment.get.toString))
    }
    interactor.put(put)
  }

}
