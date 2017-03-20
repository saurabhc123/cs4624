package cs4624.microblog

import cs4624.microblog.sentiment.{Bearish, Bullish, SentimentAnalysisModel}
import cs4624.microblog.sources.{CsvMicroblogDataSource, SparkHBaseMicroblogDataSource}
import cs4624.microblog.sources.SparkHBaseMicroblogDataSource.Default

/**
  * Created by joeywatts on 3/1/17.
  */
object Test extends App {

  import cs4624.common.spark.SparkContextManager._

  val dataSource = new SparkHBaseMicroblogDataSource(Default)

  //val csvDataSource = new CsvMicroblogDataSource("../../2014_stocktwits_data_11firms.csv")
  //val posts = csvDataSource.query()
  //dataSource.write(posts)

  val posts = dataSource.queryRDD()
  //println("Post count: " + posts.count())
  val postsWithSentiment = posts.filter(_.sentiment.isDefined).cache()
  val bearishTrainingData = postsWithSentiment.filter(_.sentiment == Some(Bearish)).sample(withReplacement = false, fraction = 0.5)
  val bullishTrainingData = postsWithSentiment.filter(_.sentiment == Some(Bullish)).sample(withReplacement = false, fraction = 0.5)
  val trainingData = bearishTrainingData union bullishTrainingData
  val testingData = postsWithSentiment subtract trainingData
  val model = SentimentAnalysisModel(trainingData)
  model.save("../cs4624_sentiment_analysis_model")
  val metrics = model.evaluate(testingData)
  println(metrics.describe)
}
