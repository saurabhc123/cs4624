lazy val pricingData = (project in file(".")).
  settings(
    name := "PricingData",
    scalaVersion := "2.10.4",
    libraryDependencies ++=Seq(
      "org.scalactic" %% "scalactic" % "3.0.1",
      "org.scalatest" %% "scalatest" % "3.0.1" % "test"
    )
  )
