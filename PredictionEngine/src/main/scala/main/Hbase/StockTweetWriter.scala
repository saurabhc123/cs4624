package main.Hbase

import java.time.Instant

import main.{Sentiment, StockTweet}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.client.{Get, Put, Scan}
import org.apache.hadoop.hbase.filter.{Filter, RowFilter}
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

  def read(tweetID: String): StockTweet = {
    val get = new Get(Bytes.toBytes(tweetID))
    get.addFamily(Bytes.toBytes("base_data")).addFamily(Bytes.toBytes("options"))
    val result = interactor.get(get)
    var cell = result.getColumnLatestCell(Bytes.toBytes("base_data"), Bytes.toBytes("timestamp"))
    val ts = Bytes.toString(cell.getValueArray, cell.getValueOffset, cell.getValueLength)
    cell = result.getColumnLatestCell(Bytes.toBytes("base_data"), Bytes.toBytes("text"))
    val text = Bytes.toString(cell.getValueArray, cell.getValueOffset, cell.getValueLength)


    cell = result.getColumnLatestCell(Bytes.toBytes("base_data"), Bytes.toBytes("judgeid"))
    var jid:String = null
    if (cell != null) {
      jid = Bytes.toString(cell.getValueArray, cell.getValueOffset, cell.getValueLength)
    }
    cell = result.getColumnLatestCell(Bytes.toBytes("options"), Bytes.toBytes("sentimentorder"))
    var sentO:String = null
    if (cell != null) {
      sentO = Bytes.toString(cell.getValueArray, cell.getValueOffset, cell.getValueLength)
    }
    cell = result.getColumnLatestCell(Bytes.toBytes("options"), Bytes.toBytes("rawPredictionScore"))
    var rawScore : String = null
    if (cell != null) {
      rawScore = Bytes.toString(cell.getValueArray, cell.getValueOffset, cell.getValueLength)
    }

    cell = result.getColumnLatestCell(Bytes.toBytes("options"), Bytes.toBytes("symbol"))
    var symbol : String = null
    if (cell != null) {
      symbol = Bytes.toString(cell.getValueArray, cell.getValueOffset, cell.getValueLength)
    }
    cell = result.getColumnLatestCell(Bytes.toBytes("options"), Bytes.toBytes("sentiment"))
    var sentiment : String = null
    if (cell != null) {
      sentiment = Bytes.toString(cell.getValueArray, cell.getValueOffset, cell.getValueLength)
    }

    StockTweet(id = tweetID, text = text, judgeId = jid, timestamp = Instant.parse(ts),
      rawPredictionScore = if (rawScore != null) Some(rawScore.toDouble) else None,
      sentiment = if (sentiment != null) Some(Sentiment.withName(sentiment)) else None,
      symbol = Option(symbol),
      sentimentOrder = if (sentO != null) Some(sentO.toInt) else None
    )
  }

}
