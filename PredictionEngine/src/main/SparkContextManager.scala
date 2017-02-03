package main

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by ericrw96 on 2/2/17.
  */
object SparkContextManager {

  // change once it is running on the cluster
  private val conf = new SparkConf().setMaster("local[*]").setAppName("PredictionEngine")
  private val sc = new SparkContext(conf)

  def getContext :SparkContext = {
     sc
  }

}
