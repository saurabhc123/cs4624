package cs4624.microblog.sentiment

import org.apache.spark.mllib.evaluation.{MulticlassMetrics, MultilabelMetrics}
import org.apache.spark.mllib.linalg.Matrix

/**
  * Created by joeywatts on 3/14/17.
  */
case class SentimentAnalysisMetrics(multiclassMetrics: MulticlassMetrics, multilabelMetrics: MultilabelMetrics) {
  val weightedF1: Double = multiclassMetrics.weightedFMeasure
  val weightedPrecision: Double = multiclassMetrics.weightedPrecision
  val weightedRecall: Double = multiclassMetrics.weightedRecall
  val microF1: Double =  multilabelMetrics.microF1Measure
  val macroF1: Double = multiclassMetrics.labels.map(lab => multiclassMetrics.fMeasure(lab)).sum/multiclassMetrics.labels.length
  val confusionMatrix: Matrix = multiclassMetrics.confusionMatrix

  def describe: String = s"MicroF1: $microF1, Macro F1: $macroF1, " +
    s"Weighted Precision: $weightedPrecision, Weighted Recall: $weightedRecall, Weighted F1: $weightedF1"
}
