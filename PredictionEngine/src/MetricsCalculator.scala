import org.apache.spark.mllib.evaluation.{MulticlassMetrics, MultilabelMetrics}
import org.apache.spark.mllib.linalg.Matrix
import org.apache.spark.rdd.RDD

/**
  * Created by Eric on 12/23/2016.
  */
class ExperimentalMetrics(multiclassMetrics: MulticlassMetrics, multilabelMetrics: MultilabelMetrics){
  var predictTime = 0.0

  val multiClassMetrics:MulticlassMetrics = multiclassMetrics
  val multiLabelMetrics:MultilabelMetrics = multilabelMetrics
  val weightedF1:Double = multiClassMetrics.weightedFMeasure
  val weightedPrecision:Double = multiClassMetrics.weightedPrecision
  val weightedRecall:Double = multiClassMetrics.weightedRecall
  val microF1:Double =  multiLabelMetrics.microF1Measure
  val macroF1:Double = multiClassMetrics.labels.map(lab => multiClassMetrics.fMeasure(lab)).sum/multiClassMetrics.labels.length
  val confusionMatrix:Matrix = multiClassMetrics.confusionMatrix

  var trainTime = 0.0



  override def toString: String = {
    s"$microF1,$macroF1,$trainTime,$weightedPrecision,$weightedRecall,$weightedF1,$predictTime"
  }
}
object ExperimentalMetrics{
  def header(): String = {
    val header = "Micro F1,Macro F1,Train Time(Seconds),Weighted Precision,Weighted Recall,Weighted F1,Prediction Time(Seconds)"
    header
  }
}
object MetricsCalculator {
  def GenerateClassifierMetrics(predictionAndLabels: RDD[(Double, Double)]) : ExperimentalMetrics = {
    val metrics = new MulticlassMetrics(predictionAndLabels)
    val otherMetrics = new MultilabelMetrics(predictionAndLabels.map(elem => (Array(elem._1), Array(elem._2))))
    new ExperimentalMetrics(metrics,otherMetrics)
  }

}