import sbt._
import Keys._

object Dependencies {

  // Spark with HBase connector.
  val spark = Seq(
    resolvers ++= Seq(
      "Cloudera repos" at "https://repository.cloudera.com/artifactory/cloudera-repos",
      "Cloudera releases" at "https://repository.cloudera.com/artifactory/libs-release"
    ),
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % "1.5.0",
      "org.apache.spark" %% "spark-mllib" % "1.5.0",
      "it.nerdammer.bigdata" % "spark-hbase-connector_2.10" % "1.0.3"
    ),
    dependencyOverrides ++= Set(
      "com.google.guava" % "guava" % "15.0",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.4"
    )
  )

}
