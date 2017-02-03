package main.Interfaces

import main.DataTypes.PredictionResult
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
  * Created by ericrw96 on 2/2/17.
  */
trait IClassifierModel  extends java.io.Serializable {
  def saveModel() : Unit

  def predict(labeledFeatures: RDD[LabeledPoint]): RDD[PredictionResult]

}
