package main

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by ericrw96 on 2/2/17.
  */
object SparkContextManager {
  private val conf = new SparkConf().setAppName("PredictionEngine")
  private val sc = new SparkContext(conf)

  def getContext :SparkContext = {
     sc
  }

}
