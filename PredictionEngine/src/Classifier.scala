import org.apache.spark.SparkContext
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.linalg.{Vector, VectorPub}
/**
  * Created by Eric on 2/2/2017.
  */
trait Classifier {
  def train(labels: RDD[LabeledPoint]): Unit

  def predict(toBePredicted: RDD[Vector]): RDD[Double]

  def saveClassifier(filePath: String, sc: SparkContext) : Unit

  def loadClassifier(filePath: String, sc : SparkContext) : Unit

}
