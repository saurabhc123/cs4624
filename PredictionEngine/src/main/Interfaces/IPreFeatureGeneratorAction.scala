package main.Interfaces

import main.DataTypes.Tweet
import org.apache.spark.rdd.RDD

/**
  * Created by Eric on 2/3/2017.
  */
trait IPreFeatureGeneratorAction {
  def performAction(tweets: RDD[Tweet]): RDD[Tweet]
}
