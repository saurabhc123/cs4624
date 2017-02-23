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
        "org.apache.hbase" % "hbase" % "1.2.3",
        "org.apache.hbase" % "hbase-common" % "1.0.0-cdh5.5.1",
        "org.apache.hbase" % "hbase-client" % "1.0.0-cdh5.5.1",
        "org.apache.hbase" % "hbase-server" % "1.0.0-cdh5.5.1"
    )

)
