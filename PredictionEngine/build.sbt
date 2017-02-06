lazy val root = (project in file("."))
.settings(
    name := "PredictionEngine",
    organization := "main",
    scalaVersion := "2.10.4",
    libraryDependencies ++= Seq(
        "org.apache.spark" %% "spark-core" % "1.5.0",
        "org.apache.spark" %% "spark-mllib" % "1.5.0"
    )
)
