import org.apache.spark.rdd.RDD

/**
  * Created by Eric on 2/2/2017.
  */
class Orchestrator(featureGen: FeatureGenerator, classifier : Classifier) {
  def performExperiment(trainingSet: RDD[Tweet], testSet : RDD[Tweet]) : ExperimentalMetrics = {
    featureGen.train(trainingSet)
    val trainingSetFeatures = featureGen.generateFeatures(trainingSet)
    classifier.train(trainingSetFeatures)

    val testSetFeatures = featureGen.generateFeatures(testSet)
    val testSetFeatureVectors = testSetFeatures.map(lp => lp.features)
    val testSetLabels = testSetFeatures.map(lp => lp.label)
    val testSetClassifierLabels = classifier.predict(testSetFeatureVectors)
    MetricsCalculator.GenerateClassifierMetrics(testSetLabels.zip(testSetClassifierLabels))
  }

}
