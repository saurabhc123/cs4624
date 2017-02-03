package main

import main.ClassifierType.ClassifierType
import main.FeatureGeneratorType.FeatureGeneratorType
import org.apache.spark.rdd.RDD

/**
  * Created by Eric on 2/2/2017.
  */
class Orchestrator(featureGeneratorType: FeatureGeneratorType, classifierType: ClassifierType) {


  def performExperiment(trainingSet: RDD[Tweet], testSet : RDD[Tweet]) : ExperimentalMetrics = {
    val featureGenerator = FeatureGeneratorFactory.getFeatureGenerator(featureGeneratorType)

    val trainingSetFeatures = featureGenerator.generateFeatures(trainingSet, DataType.TRAINING)

    val classifier = ClassifierFactory.getClassifier(classifierType)

    val classifierModel = classifier.train(trainingSetFeatures)

    val testSetFeatures = featureGenerator.generateFeatures(testSet, DataType.TEST)

    val predictionResults = classifierModel.predict(testSetFeatures)

    MetricsCalculator.GenerateClassifierMetrics(predictionResults)
  }

}
