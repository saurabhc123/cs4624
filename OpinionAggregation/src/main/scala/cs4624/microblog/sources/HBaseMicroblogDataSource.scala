package cs4624.microblog.sources

import java.time.Instant

import cs4624.common.OptionalArgument
import cs4624.microblog.sentiment.{Bearish, Bullish, Neutral}
import cs4624.microblog.{MicroblogAuthor, MicroblogPost}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import it.nerdammer.spark.hbase._

/**
  * Created by joeywatts on 2/28/17.
  */
class HBaseMicroblogDataSource(table: HBaseMicroblogDataSource.Table)
                              (implicit val sc: SparkContext)
  extends MicroblogDataSource {

  override def query(startTime: OptionalArgument[Instant],
                     endTime: OptionalArgument[Instant]): RDD[MicroblogPost] = {
    sc.hbaseTable[(String, String, String, String, Option[String], Option[String])](table.name)
      .select("base_data:timestamp", "base_data:text", "base_data:judgeid",
        "options:sentiment", "options:symbol")
      .map { case (row, timestamp, text, judgeid, sentiment, symbol) =>
        MicroblogPost(
          id = row,
          text = text,
          author = MicroblogAuthor(judgeid),
          sentiment = sentiment.map {
            case "POSITIVE" => Bullish
            case "NEGATIVE" => Bearish
            case _ => Neutral
          },
          time = Instant.parse(timestamp),
          symbols = symbol.getOrElse("").split("::::")
        )
    }.filter(_.time.isAfter(startTime.getOrElse(Instant.MIN)))
      .filter(_.time.isBefore(endTime.getOrElse(Instant.MAX)))
  }

}

object HBaseMicroblogDataSource {
  sealed trait Table { def name: String }
  case object Default extends Table {
    override def name = "stock_tweets"
  }
}