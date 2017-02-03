package main

import main.DataTypes.Tweet
import main.Factories.{ClassifierFactory, FeatureGeneratorFactory}
import main.Factories.ClassifierType.ClassifierType
import main.Factories.FeatureGeneratorType.FeatureGeneratorType
import main.Interfaces.{DataType, IPreFeatureGeneratorAction}
import org.apache.spark.rdd.RDD
import scala.collection.mutable.ListBuffer

/**
  * Created by Eric on 2/2/2017.
  */
class Orchestrator(featureGeneratorType: FeatureGeneratorType, classifierType: ClassifierType) {

  private val PreFeatureHooks: ListBuffer[IPreFeatureGeneratorAction] = ListBuffer()

  def addPreFeatureGeneratorAction(action: IPreFeatureGeneratorAction): Unit ={
    PreFeatureHooks +=  action
  }
  def performExperiment(trainingSet: RDD[Tweet], testSet : RDD[Tweet]) : ExperimentalMetrics = {
    val featureGenerator = FeatureGeneratorFactory.getFeatureGenerator(featureGeneratorType)

    val trainingSetWithActions = performPreFeatureGeneratorActions(trainingSet)
    val trainingSetFeatures = featureGenerator.generateFeatures(trainingSetWithActions, DataType.TRAINING)

    val classifier = ClassifierFactory.getClassifier(classifierType)

    val classifierModel = classifier.train(trainingSetFeatures)

    val testSetWithActions = performPreFeatureGeneratorActions(testSet)
    val testSetFeatures = featureGenerator.generateFeatures(testSetWithActions, DataType.TEST)

    val predictionResults = classifierModel.predict(testSetFeatures)

    MetricsCalculator.GenerateClassifierMetrics(predictionResults)
  }

  private def performPreFeatureGeneratorActions(tweets : RDD[Tweet]): RDD[Tweet] = {
    var currentTweetMapping = tweets
    for(action <- PreFeatureHooks){
      currentTweetMapping = action.performAction(currentTweetMapping)
    }
    currentTweetMapping
  }

}
