package main.Interfaces

import main.DataTypes.Tweet
import main.Interfaces.DataType.DataType
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
/**
  * A common train shared by all feature generators.
  * Allows a "training" phase followed by a generation phase
  * Created by Eric on 2/1/2017.
  */
trait IFeatureGenerator  extends java.io.Serializable {

  def generateFeatures(tweets: RDD[Tweet], dataType: DataType): RDD[LabeledPoint]

}



object DataType extends Enumeration{
  type DataType = Value
  val TRAINING, TEST = Value
}
