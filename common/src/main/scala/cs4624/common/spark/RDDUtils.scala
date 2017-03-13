package cs4624.common.spark

import org.apache.spark.rdd.RDD

/**
  * Created by joeywatts on 2/28/17.
  */
object RDDUtils {

  private class RDDIterable[A](rdd: RDD[A]) extends Iterable[A] {
    override def iterator = rdd.toLocalIterator
  }

  def iterableFromRDD[A](rdd: RDD[A]): Iterable[A] = new RDDIterable(rdd)
}
