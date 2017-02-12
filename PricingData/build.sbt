lazy val PricingData = (project in file(".")).
  settings(
    name := "PricingData",
    scalaVersion := "2.10.4",
    resolvers ++= Seq(
      "Hadoop Releases" at "https://repository.cloudera.com/content/repositories/releases/"
    ),
    libraryDependencies ++= Seq(
      "com.github.nscala-time" %% "nscala-time" % "2.16.0",
      "com.typesafe.play" %% "play-ws" % "2.4.10",
      "com.google.guava" % "guava" % "15.0",
      "org.apache.hadoop" % "hadoop-common" % "2.6.0",
      "org.apache.hadoop" % "hadoop-mapred" % "0.22.0",
      "org.apache.hbase" % "hbase-common" % "1.0.0",
      "org.apache.hbase" % "hbase-client" % "1.0.0"
    ),
    dependencyOverrides += "com.google.guava" % "guava" % "15.0"
)
