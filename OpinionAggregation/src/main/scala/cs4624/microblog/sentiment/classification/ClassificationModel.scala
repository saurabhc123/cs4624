package cs4624.microblog.sentiment.classification

import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Vector

/**
  * Created by joeywatts on 3/1/17.
  */
trait ClassificationModel {
  def classify(data: Vector): Double
  def save(file: String)(implicit sc: SparkContext): Unit
}
