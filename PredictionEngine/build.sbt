lazy val PredictionEngine = (project in file("."))
.settings(
    name := "PredictionEngine",
    organization := "main",
    scalaVersion := "2.10.4",
    libraryDependencies ++= Seq(
        "org.apache.spark" %% "spark-core" % "1.5.0",
        "org.apache.spark" %% "spark-mllib" % "1.5.0"
    ),
    resolvers ++= Seq(
        "Cloudera repos" at "https://repository.cloudera.com/artifactory/cloudera-repos",
        "Cloudera releases" at "https://repository.cloudera.com/artifactory/libs-release"
    ),
  libraryDependencies ++= Seq(
      "com.google.guava" % "guava" % "15.0",
      "org.apache.hadoop" % "hadoop-common" % "2.6.0" excludeAll ExclusionRule(organization = "javax.servlet"),
      "org.apache.hadoop" % "hadoop-mapred" % "0.22.0",
      "org.apache.hbase" % "hbase-common" % "1.0.0",
      "org.apache.hbase" % "hbase-client" % "1.0.0"
    ),
  dependencyOverrides ++= Set(
    "com.google.guava" % "guava" % "15.0",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.4"
  )
).dependsOn(Project(id="PricingData", base=file("../PricingData")))
