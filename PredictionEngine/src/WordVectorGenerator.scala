import org.apache.spark.SparkContext
import org.apache.spark.mllib.feature.{Word2Vec, Word2VecModel}
import org.apache.spark.mllib.linalg.{Vector, VectorPub, Vectors}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import sun.plugin.dom.exception.InvalidStateException

import scala.util.Try
/**
  * A feature generator for word vector generation
  * Created by Eric on 2/2/2017.
  */
class WordVectorGenerator extends FeatureGenerator{
  var Model: Word2VecModel = _
  override def train(tweets: RDD[Tweet]): Unit = {

    val cleanTrainingTweets = tweets map cleanTweetHtml

    val wordOnlyTrainSample = cleanTrainingTweets map wordOnlySample

    val samplePairs = wordOnlyTrainSample.map(s => s.identifier -> s)
    val reviewWordsPairs: RDD[(String, Iterable[String])] = samplePairs.mapValues(_.text.split(" ").toIterable)

    Model = new Word2Vec().fit(reviewWordsPairs.values)
  }

  override def generateFeatures(tweets: RDD[Tweet]): RDD[LabeledPoint] = {
    checkModel()
    val cleanTrainingTweets = tweets map cleanTweetHtml
    val wordOnlyTrainSample = cleanTrainingTweets map wordOnlySample
    val samplePairs = wordOnlyTrainSample.map(s => s.identifier -> s)
    val reviewWordsPairs = samplePairs.mapValues(_.text.split(" ").toIterable)

    def wordFeatures(words: Iterable[String]): Iterable[Vector] = words.map(w => Try(Model.transform(w)))
                                        .filter(_.isSuccess).map(x => x.get)

    def avgWordFeatures(wordFeatures: Iterable[Vector]): Vector = VectorPub.BreezeVectorPublications(
      wordFeatures.map(VectorPub.VectorPublications(_).toBreeze).reduceLeft((x, y) => x + y) / wordFeatures.size.toDouble)
      .fromBreeze

    def filterNullFeatures(wordFeatures: Iterable[Vector]): Iterable[Vector] =
      if (wordFeatures.isEmpty) wordFeatures.drop(1) else wordFeatures

    // Create feature vectors
    val wordFeaturePairTrain = reviewWordsPairs mapValues wordFeatures
    //val intermediateVectors = wordFeaturePair.mapValues(x => x.map(_.asBreeze))
    val inter2Train = wordFeaturePairTrain.filter(_._2.nonEmpty)
    val avgWordFeaturesPairTrain = inter2Train mapValues avgWordFeatures
    val featuresPairTrain = avgWordFeaturesPairTrain join samplePairs mapValues {
      case (features, Tweet(id, tweetText, label)) => LabeledPoint(label.get, features)
    }
    val trainingSet = featuresPairTrain.values
    trainingSet
  }



  override def saveGenerator(filePath: String, sc :SparkContext): Unit = {
    checkModel()
    Model.save(sc,filePath)
  }

  override def loadGenerator(filePath: String, sc: SparkContext): Unit = {
    Model = Word2VecModel.load(sc,filePath)
  }

  def checkModel(): Unit = {
    if (Model == null){
      throw new InvalidStateException("Model has not been loaded or trained!")
    }
  }


  private def cleanHtml(str: String) = str.replaceAll( """<(?!\/?a(?=>|\s.*>))\/?.*?>""", "")

  private def cleanTweetHtml(sample: Tweet) = sample copy (text = cleanHtml(sample.text))

  private def cleanWord(str: String) = str.split(" ").map(_.trim.toLowerCase).filter(_.nonEmpty)
    .map(_.replaceAll("\\W", "")).reduceOption((x, y) => s"$x $y")

  private def wordOnlySample(sample: Tweet) = sample copy (text = cleanWord(sample.text).getOrElse(""))



}
