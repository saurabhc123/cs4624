package cs4624.microblog.sources

import java.time.Instant

import cs4624.common.OptionalArgument
import cs4624.microblog.sentiment.{Bearish, Bullish}
import cs4624.microblog.{MicroblogAuthor, MicroblogPost}
import cs4624.microblog.sources.HBaseMicroblogDataSource.Default
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.{Connection, Result, Scan}
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.JavaConversions._

/**
  * Created by joeywatts on 3/20/17.
  */
class HBaseMicroblogDataSource(table: HBaseMicroblogDataSource.Table = Default)
                              (implicit hbaseConnection: Connection)
  extends MicroblogDataSource {

  private val hbaseTable = hbaseConnection.getTable(TableName.valueOf(table.name))

  private val baseDataCF = Bytes.toBytes("base_data")
  private val optionsCF = Bytes.toBytes("options")

  private val timestampCQ = Bytes.toBytes("timestamp")
  private val textCQ = Bytes.toBytes("text")
  private val judgeCQ = Bytes.toBytes("judgeid")
  private val sentimentCQ = Bytes.toBytes("sentiment")
  private val symbolCQ = Bytes.toBytes("symbol")

  override def query(startTime: OptionalArgument[Instant],
                     endTime: OptionalArgument[Instant]): Iterator[MicroblogPost] = {
    val scan = new Scan()
    val realStartTime = startTime.getOrElse(Instant.MIN)
    val realEndTime = endTime.getOrElse(Instant.MAX)
    hbaseTable.getScanner(scan).iterator().map(resultToMicroblogPost)
      .filterNot(_.time.isBefore(realStartTime))
      .filterNot(_.time.isAfter(realEndTime))
  }

  private def resultToMicroblogPost(result: Result): MicroblogPost = {
    val row = Bytes.toString(result.getRow)
    val timestamp = Bytes.toString(result.getValue(baseDataCF, timestampCQ))
    val text = Bytes.toString(result.getValue(baseDataCF, textCQ))
    val judge = Bytes.toString(result.getValue(baseDataCF, judgeCQ))
    val sentiment = Option(result.getValue(optionsCF, sentimentCQ)).map(Bytes.toString) match {
      case Some("Bullish") => Some(Bullish)
      case Some("Bearish") => Some(Bearish)
      case _ => None
    }
    val symbol = Option(result.getValue(optionsCF, symbolCQ)).map(Bytes.toString) match {
      case Some(str) => str.split("::::").toSet
      case _ => Set[String]()
    }
    MicroblogPost(
      id = row,
      text = text,
      author = MicroblogAuthor(judge),
      time = Instant.parse(timestamp),
      sentiment = sentiment,
      symbols = symbol
    )
  }

}
object HBaseMicroblogDataSource {
  sealed trait Table { def name: String }
  case object Default extends Table {
    override def name = "stocktwits_microblogs"
  }
}