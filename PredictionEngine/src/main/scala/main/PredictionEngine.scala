package main

import java.time.Instant

import main.Factories.{ClassifierType, FeatureGeneratorType}
import main.Hbase.StockTweetWriterTest

/**
  * A singleton class that will serve as the entry point
  * Created by Eric on 2/1/2017.
  */
object PredictionEngine extends App{
  // this is the entry point for our application
  //println("Hello world")
  //val experiment = new Experiment(FeatureGeneratorType.Word2Vec, ClassifierType.LogisticRegression)
  StockTweetWriterTest.doTest()
  //StockDecisions.makeRun()
  //StockActions.makeRun()
  //StockOrchestrator.Orchestrate(Instant.now(), Instant.now().plus(Judge.confirmationTimeWindow).plus(Judge.confirmationTimeWindow), Judge.confirmationTimeWindow, Judge.confirmationTimeWindow)
}
