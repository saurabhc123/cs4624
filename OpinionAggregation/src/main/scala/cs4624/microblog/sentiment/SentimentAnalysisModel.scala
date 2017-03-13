package cs4624.microblog.sentiment

import cs4624.microblog.MicroblogPost
import cs4624.microblog.sentiment.classification.logisticregression.LogisticRegressionClassification
import cs4624.microblog.sentiment.classification.{ClassificationModel, Classifier}
import cs4624.microblog.sentiment.featureextraction.word2vec.Word2VecFeatureExtraction
import cs4624.microblog.sentiment.featureextraction.{FeatureExtractionModel, FeatureExtractor}
import org.apache.spark.SparkContext
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

/**
  * Created by joeywatts on 3/1/17.
  */
case class SentimentAnalysisModel(featureExtractor: FeatureExtractionModel, classifier: ClassificationModel) {
  /**
    * Predict the sentiment of posts.
    * @param posts - the posts to analyze.
    * @return an RDD of posts with their sentiments.
    */
  def predict(posts: RDD[MicroblogPost]): RDD[(MicroblogPost, Double)] = {
    posts.map(p => (p, classifier.classify(featureExtractor.extract(p))))
  }
  def save(filePrefix: String)(implicit sc: SparkContext) = {
    featureExtractor.save(s"$filePrefix-feature-extractor")
    classifier.save(s"$filePrefix-classifier")
  }
}

object SentimentAnalysisModel {
  def apply(trainingData: RDD[MicroblogPost],
            featureExtractor: FeatureExtractor = Word2VecFeatureExtraction,
            classifier: Classifier = LogisticRegressionClassification)
           (implicit sc: SparkContext): SentimentAnalysisModel = {
    val filteredTrainingData = trainingData.filter(_.sentiment.isDefined)
    val featureExtractionModel= featureExtractor.train(filteredTrainingData)
    val labeledPoints = filteredTrainingData.map(p =>
      LabeledPoint(p.sentiment.get.label, featureExtractionModel.extract(p))
    )
    val classificationModel = classifier.train(labeledPoints)
    SentimentAnalysisModel(featureExtractionModel, classificationModel)
  }
}
