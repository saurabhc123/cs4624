package main

import ClassifierImpl.LogisticRegressionClassifier
import main.ClassifierType.ClassifierType

/**
  * Created by ericrw96 on 2/2/17.
  */
object ClassifierFactory {
  def getClassifier(classifierType: ClassifierType): IClassifier = {
    import main.ClassifierType._
     classifierType match {
       case LogisticRegression => new LogisticRegressionClassifier
       case SVM => throw new NotImplementedError("The is no SVM as of now")
     }
  }

}

object ClassifierType extends Enumeration{
  type ClassifierType = Value
  val LogisticRegression, SVM = Value
}
