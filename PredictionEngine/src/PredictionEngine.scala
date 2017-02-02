import org.apache.spark.SparkConf
/**
  * A singleton class that will serve as the entry point
  * Created by Eric on 2/1/2017.
  */
object PredictionEngine extends App{
  // this is the entry point for our application
  println("Hello world")
  val gen = FeatureGeneratorFactory.getGenerator
  println(gen.getClass)
}
