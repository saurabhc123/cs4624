package main

import FeatureGeneratorImpl.WordVectorGenerator
import main.FeatureGeneratorType.FeatureGeneratorType

/**
  * Created by Eric on 2/1/2017.
  */
object FeatureGeneratorFactory {
  def getFeatureGenerator(featureGeneratorType: FeatureGeneratorType) : IFeatureGenerator ={
    import main.FeatureGeneratorType._
    featureGeneratorType match {
      case Word2Vec => new WordVectorGenerator
    }
  }

}

object FeatureGeneratorType extends Enumeration{
  type FeatureGeneratorType = Value
  val Word2Vec = Value
}
