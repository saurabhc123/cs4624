package cs4624.microblog.sentiment.featureextraction.word2vec

import cs4624.microblog.MicroblogPost
import cs4624.microblog.sentiment.featureextraction.{FeatureExtractionModel, FeatureExtractor}
import org.apache.spark.SparkContext
import org.apache.spark.mllib.feature.{Word2Vec, Word2VecModel}
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.linalg.{Vector, VectorPub, Vectors}

import scala.util.Try

/**
  * Created by joeywatts on 3/1/17.
  */
case class Word2VecFeatureExtractionModel(model: Word2VecModel) extends FeatureExtractionModel {

  override def extract(data: MicroblogPost): Option[Vector] = {
    def wordFeatures(words: Iterable[String]): Iterable[Vector] = words.map(w => Try(model.transform(w)))
      .filter(_.isSuccess).map(x => x.get)

    def avgWordFeatures(wordFeatures: Iterable[Vector]): Vector = {
      val features = wordFeatures.map(VectorPub.VectorPublications(_).toBreeze)
      VectorPub.BreezeVectorPublications(
        features.reduceLeft((x, y) => x + y) / wordFeatures.size.toDouble
      ).fromBreeze
    }

    val features = wordFeatures(Word2VecFeatureExtraction.textToWords(data.text))
    if (features.isEmpty)
      None
    else
      Some(avgWordFeatures(features))
  }

  override def save(file: String)(implicit sc: SparkContext) = {
    model.save(sc, file)
  }
}

object Word2VecFeatureExtraction extends FeatureExtractor {

  private[word2vec] def textToWords(text: String): Iterable[String] = {
    def cleanHtml(str: String) = str.replaceAll( """<(?!\/?a(?=>|\s.*>))\/?.*?>""", "")
    def cleanWord(str: String) = str.split(" ").map(_.trim.toLowerCase).filter(_.nonEmpty)
      .map(_.replaceAll("\\W", "")).reduceOption((x, y) => s"$x $y")

    cleanWord(cleanHtml(text)).getOrElse("").split(" ")
  }

  override def load(file: String)(implicit sc: SparkContext) = {
    val model = Word2VecModel.load(sc, file)
    Some(Word2VecFeatureExtractionModel(model))
  }

  override def train(data: RDD[MicroblogPost])(implicit sc: SparkContext) = {
    val words = data.map(post => textToWords(post.text))
    val model = new Word2Vec().fit(words)
    Word2VecFeatureExtractionModel(model)
  }

}