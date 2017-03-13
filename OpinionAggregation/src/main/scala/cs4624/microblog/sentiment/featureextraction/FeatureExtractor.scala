package cs4624.microblog.sentiment.featureextraction

import cs4624.microblog.MicroblogPost
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

/**
  * Created by joeywatts on 3/1/17.
  */
trait FeatureExtractor {
  def train(data: RDD[MicroblogPost])(implicit sc: SparkContext): FeatureExtractionModel
  def load(file: String)(implicit sc: SparkContext): FeatureExtractionModel
}
