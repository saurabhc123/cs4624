package cs4624.microblog

import cs4624.microblog.sources.HBaseMicroblogDataSource
import cs4624.microblog.sources.HBaseMicroblogDataSource.Default

/**
  * Created by joeywatts on 3/1/17.
  */
object Test extends App {

  import cs4624.common.spark.SparkContextManager._

  val dataSource = new HBaseMicroblogDataSource(Default)
  dataSource.query().take(100).foreach(println)

}
