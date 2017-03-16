package cs4624.microblog.sentiment.classification

import org.apache.spark.SparkContext
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
  * Created by joeywatts on 3/1/17.
  */
trait Classifier {
  def train(labeledPoints: RDD[LabeledPoint])(implicit sc: SparkContext): ClassificationModel
  def load(file: String)(implicit sc: SparkContext): Option[ClassificationModel]
}
