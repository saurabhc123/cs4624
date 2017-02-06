package org.apache.spark.mllib.linalg

/**
  * A wrapper file that allows us to use from breeze
  * Created by Eric on 2/2/2017.
  */
object VectorPub {

  implicit class VectorPublications(val vector : Vector) extends AnyVal {
    def toBreeze : breeze.linalg.Vector[scala.Double] = vector.toBreeze

  }

  implicit class BreezeVectorPublications(val breezeVector : breeze.linalg.Vector[Double]) extends AnyVal {
    def fromBreeze : Vector = Vectors.fromBreeze(breezeVector)
  }
}
