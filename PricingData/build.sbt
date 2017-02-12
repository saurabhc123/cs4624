lazy val PricingData = (project in file(".")).
  settings(
    name := "PricingData",
    scalaVersion := "2.10.4",
    libraryDependencies ++= Seq(
      "com.github.nscala-time" %% "nscala-time" % "2.16.0",
      "com.typesafe.play" %% "play-ws" % "2.4.10"
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
