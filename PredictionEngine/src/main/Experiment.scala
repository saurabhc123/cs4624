package main

import main.DataTypes.Tweet
import main.Factories.ClassifierType.ClassifierType
import main.Factories.FeatureGeneratorType.FeatureGeneratorType

/**
  * Created by ericrw96 on 2/2/17.
  */
class Experiment(private val featureGeneratorType: FeatureGeneratorType, private val classifierType: ClassifierType) {
  private val orchestrator = new Orchestrator(featureGeneratorType, classifierType)

  private val sc = SparkContextManager.getContext

  private val exampleTrainSet = sc.parallelize(Seq(Tweet("a", "hello world", Some(0.0)), Tweet("b", "goodbye everyone", Some(1.0)), Tweet("a2", "hello world", Some(0.0)), Tweet("ab", "hello world", Some(1.0)), Tweet("abc", "hello world", Some(0.0)), Tweet("abd", "hello world", Some(0.0))))

  private val exampleTestSet = sc.parallelize(Seq(Tweet("aa", "hello everyone", Some(0.0)), Tweet("bb", "goodbye world", Some(1.0))))

  private val metrics = orchestrator.performExperiment(exampleTrainSet, exampleTestSet)

  println(ExperimentalMetrics.header())
  println(metrics.toString)
}
