lazy val PricingData = (project in file(".")).
  settings(
    name := "PricingData",
    scalaVersion := "2.10.4",
    libraryDependencies ++= Seq(
      "com.github.nscala-time" %% "nscala-time" % "2.16.0",
      "com.typesafe.play" %% "play-ws" % "2.4.10"
    )
  )
