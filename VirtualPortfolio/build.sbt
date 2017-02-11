lazy val virtualPortfolio = (project in file(".")).
  settings(
    name := "VirtualPortfolio",
    scalaVersion := "2.10.4",
    libraryDependencies ++= Seq(
      "com.github.nscala-time" %% "nscala-time" % "2.16.0"
    )
  )
