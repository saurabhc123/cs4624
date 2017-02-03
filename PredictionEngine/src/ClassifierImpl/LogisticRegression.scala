package ClassifierImpl

import main.Classifier
import org.apache.spark.SparkContext
import org.apache.spark.mllib.classification.{LogisticRegressionModel, LogisticRegressionWithLBFGS}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
/**
  * Created by Eric on 2/2/2017.
  */
class LogisticRegression extends Classifier{
  var Model : LogisticRegressionModel = _
  override def train(labels: RDD[LabeledPoint]): Unit = {
    val num_labels = labels.map(x => x.label).distinct().count().toInt
    val lrClassifier = new LogisticRegressionWithLBFGS()
    Model = lrClassifier.setNumClasses(num_labels).run(labels)
  }

  override def predict(toBePredicted: RDD[Vector]): RDD[Double] = {
    Model.predict(toBePredicted)
  }

  override def saveClassifier(filePath: String, sc: SparkContext): Unit = {
    Model.save(sc,filePath)
  }

  override def loadClassifier(filePath: String, sc: SparkContext): Unit = {
    Model = LogisticRegressionModel.load(sc,filePath)
  }
}
