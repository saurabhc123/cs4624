package cs4624

import java.time.{Instant, LocalDate, ZoneOffset}


/**
  * A singleton class that will serve as the entry point
  * Created by Eric on 2/1/2017.
  */
object PredictionEngine extends App{
  // this is the entry point for our application
  //println("Hello world")
  //val experiment = new Experiment(FeatureGeneratorType.Word2Vec, ClassifierType.LogisticRegression)
  //StockTweetWriterTest.doTest()
  //StockDecisions.makeRun()
  //StockActions.makeRun()
  val startTime = LocalDate.of(2014, 1, 1).atTime(12, 0).toInstant(ZoneOffset.UTC)
  val endTime = LocalDate.of(2014, 12, 31).atStartOfDay().toInstant(ZoneOffset.UTC)
  //StockOrchestrator.Orchestrate(startTime, endTime, Judge.confirmationTimeWindow, Judge.confirmationTimeWindow)
}
