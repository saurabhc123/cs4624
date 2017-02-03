package main

import main.ClassifierType.ClassifierType
import main.FeatureGeneratorType.FeatureGeneratorType

/**
  * Created by ericrw96 on 2/2/17.
  */
class Experiment(private val featureGeneratorType: FeatureGeneratorType, private val classifierType: ClassifierType) {
  private val orchestrator = new Orchestrator(featureGeneratorType, classifierType)

  private val sc = SparkContextManager.getContext

  private val exampleTrainSet = sc.parallelize(Seq(Tweet("a", "b o b", Some(0.0)), Tweet("b", "a a b o b", Some(1.0))))

  private val exampleTestSet = sc.parallelize(Seq(Tweet("aa", "bb oo bb b o b", Some(0.0)), Tweet("bb", "aa aa bb oo bb a a b o b", Some(1.0))))

  private val metrics = orchestrator.performExperiment(exampleTrainSet, exampleTestSet)

  println(ExperimentalMetrics.header())
  println(metrics.toString)
}
