package main.Interfaces

import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
/**
  * Created by Eric on 2/2/2017.
  */
trait IClassifier extends java.io.Serializable {
  def train(labels: RDD[LabeledPoint]): IClassifierModel

  def loadModel(): IClassifierModel
}
