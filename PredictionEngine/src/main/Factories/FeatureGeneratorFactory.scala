package main.Factories

import main.Factories.FeatureGeneratorType.FeatureGeneratorType
import main.Implementations.FeatureGeneratorImpl.WordVectorGenerator
import main.Interfaces.IFeatureGenerator

/**
  * Created by Eric on 2/1/2017.
  */
object FeatureGeneratorFactory {
  def getFeatureGenerator(featureGeneratorType: FeatureGeneratorType) : IFeatureGenerator ={
    featureGeneratorType match {
      case FeatureGeneratorType.Word2Vec => new WordVectorGenerator
    }
  }

}

object FeatureGeneratorType extends Enumeration{
  type FeatureGeneratorType = Value
  val Word2Vec = Value
}
