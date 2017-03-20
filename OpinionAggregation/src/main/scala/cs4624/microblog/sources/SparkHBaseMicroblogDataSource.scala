package cs4624.microblog.sources

import java.time.Instant

import cs4624.common.OptionalArgument
import cs4624.microblog.sentiment.{Bearish, Bullish}
import cs4624.microblog.{MicroblogAuthor, MicroblogPost}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import it.nerdammer.spark.hbase._

/**
  * Created by joeywatts on 2/28/17.
  */
class SparkHBaseMicroblogDataSource(table: SparkHBaseMicroblogDataSource.Table)
                                   (implicit val sc: SparkContext)
  extends MicroblogDataSource {

  override def query(startTime: OptionalArgument[Instant],
                     endTime: OptionalArgument[Instant]): Iterator[MicroblogPost] = {
    queryRDD(startTime, endTime).toLocalIterator
  }

  def queryRDD(startTime: OptionalArgument[Instant] = None,
               endTime: OptionalArgument[Instant] = None): RDD[MicroblogPost] = {
    sc.hbaseTable[(String, String, String, String, Option[String], Option[String])](table.name)
      .select("base_data:timestamp", "base_data:text", "base_data:judgeid",
        "options:sentiment", "options:symbol")
      .map { case (row, timestamp, text, judgeid, sentiment, symbol) =>
        MicroblogPost(
          id = row,
          text = text,
          author = MicroblogAuthor(judgeid),
          sentiment = sentiment match {
            case Some("Bullish") => Some(Bullish)
            case Some("Bearish") => Some(Bearish)
            case _ => None
          },
          time = Instant.parse(timestamp),
          symbols = symbol.getOrElse("").split("::::").toSet
        )
      }.filter(_.time.isAfter(startTime.getOrElse(Instant.MIN)))
      .filter(_.time.isBefore(endTime.getOrElse(Instant.MAX)))
  }

  def write(posts: RDD[MicroblogPost]) = {
    posts.map(post => {
      (post.id,
        post.time.toString,
        post.text,
        post.author.id,
        post.sentiment.map(_.toString),
        if (post.symbols.isEmpty) None else Some(post.symbols.mkString("::::")))
    }).toHBaseTable(table.name)
      .toColumns("base_data:timestamp", "base_data:text", "base_data:judgeid",
        "options:sentiment", "options:symbol")
      .save()
  }

}

object SparkHBaseMicroblogDataSource {
  sealed trait Table { def name: String }
  case object Default extends Table {
    override def name = "stocktwits_microblogs"
  }
}