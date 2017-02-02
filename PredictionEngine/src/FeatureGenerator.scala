import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.regression.LabeledPoint
/**
  * A common train shared by all feature generators.
  * Allows a "training" phase followed by a generation phase
  * Created by Eric on 2/1/2017.
  */
trait FeatureGenerator {
  def train(tweets: RDD[Tweet]): Unit

  def generateFeatures(tweets: RDD[Tweet]): RDD[LabeledPoint]

  def saveGenerator(filePath: String): Unit

  def loadGenerator(filePath: String): Unit

}
