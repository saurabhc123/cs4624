package cs4624.microblog.sentiment.featureextraction

import cs4624.microblog.MicroblogPost
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.linalg.Vector

/**
  * Created by joeywatts on 3/1/17.
  */
trait FeatureExtractionModel {
  def extract(data: MicroblogPost): Vector
  def save(file: String)(implicit sc: SparkContext): Unit
}
