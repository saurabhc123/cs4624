package main.Factories

import main.Factories.ClassifierType.ClassifierType
import main.Implementations.ClassifierImpl.LogisticRegressionClassifier
import main.Interfaces.IClassifier

/**
  * Created by ericrw96 on 2/2/17.
  */
object ClassifierFactory {
  def getClassifier(classifierType: ClassifierType): IClassifier = {
     classifierType match {
       case ClassifierType.LogisticRegression => new LogisticRegressionClassifier
       case ClassifierType.SVM => throw new NotImplementedError("The is no SVM as of now")
     }
  }

}

object ClassifierType extends Enumeration{
  type ClassifierType = Value
  val LogisticRegression, SVM = Value
}
