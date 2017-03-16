package cs4624.microblog.sentiment.classification.logisticregression

import cs4624.microblog.sentiment.classification.{ClassificationModel, Classifier}
import org.apache.spark.SparkContext
import org.apache.spark.mllib.classification.{LogisticRegressionModel, LogisticRegressionWithLBFGS}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
  * Created by joeywatts on 3/1/17.
  */
case class LogisticRegressionClassificationModel(model: LogisticRegressionModel) extends ClassificationModel {

  override def classify(data: Vector) = {
    model.predict(data)
  }

  override def save(file: String)(implicit sc: SparkContext) = {
    model.save(sc, file)
  }

}

object LogisticRegressionClassification extends Classifier {

  override def load(file: String)(implicit sc: SparkContext) = {
    val model = LogisticRegressionModel.load(sc, file)
    Some(LogisticRegressionClassificationModel(model))
  }

  override def train(labeledPoints: RDD[LabeledPoint])(implicit sc: SparkContext) = {
    val numLabels = Math.ceil(labeledPoints.map(x => x.label).max()).toInt + 1
    val lrClassifier = new LogisticRegressionWithLBFGS()
    lrClassifier.setNumClasses(numLabels)
    LogisticRegressionClassificationModel(lrClassifier.run(labeledPoints))
  }

}
