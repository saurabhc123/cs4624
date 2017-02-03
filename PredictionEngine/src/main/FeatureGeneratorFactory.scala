package main

import FeatureGeneratorImpl.WordVectorGenerator

/**
  * Created by Eric on 2/1/2017.
  */
object FeatureGeneratorFactory {
  def getGenerator : FeatureGenerator ={
    new WordVectorGenerator()
  }

}
