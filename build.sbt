lazy val common = project
  .settings(
    Settings.commonSettings ++ Seq(
      name := "common"
    )
  )

lazy val PredictionEngine = project.dependsOn(common, PricingData)
  .settings(
    Settings.commonSettings ++ Dependencies.spark ++ Seq(
      name := "PredictionEngine"
    )
  )

lazy val PricingData = project.dependsOn(common)
  .settings(
    Settings.commonSettings ++ Dependencies.spark ++ Seq(
      name := "PricingData",
      libraryDependencies += "com.typesafe.play" %% "play-ws" % "2.4.10"
    )
  )

lazy val VirtualPortfolio = project.dependsOn(common, PricingData)
  .settings(
    Settings.commonSettings ++ Seq(
      name := "VirtualPortfolio"
    )
  )

lazy val TradingSimulation = project.dependsOn(common, VirtualPortfolio)
  .settings(
    Settings.commonSettings ++ Seq(
      name := "TradingSimulation"
    )
  )

lazy val root = (project in file("."))
  .aggregate(
    common,
    PredictionEngine,
    PricingData,
    VirtualPortfolio,
    TradingSimulation
  )
  .settings(Sync.task)
retrieveManaged := true

// Exclude Emacs autosave files.
excludeFilter in unmanagedSources := ".#*"

