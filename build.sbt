import sbtsparksubmit.SparkSubmitPlugin.autoImport._

lazy val common = project
  .settings(
    Settings.commonSettings ++ Dependencies.spark ++ Seq(
      name := "common"
    )
  )

lazy val OpinionAggregation = project.dependsOn(common, PricingData)
  .settings(
    Settings.commonSettings ++ Dependencies.spark ++ Seq(
      name := "OpinionAggregation"
    ) ++ SparkSubmitSetting("runSpark", Seq("--class", "cs4624.microblog.Test"))
  )

lazy val PricingData = project.dependsOn(common)
  .settings(
    Settings.commonSettings ++ Dependencies.spark ++ Seq(
      name := "PricingData",
      libraryDependencies += "com.typesafe.play" %% "play-ws" % "2.4.10"
    )
  )

lazy val TradingSimulation = project.dependsOn(common, PricingData, OpinionAggregation)
  .settings(
    Settings.commonSettings ++ Dependencies.spark ++ Seq(
      name := "TradingSimulation"
    )
  )

lazy val root = (project in file("."))
  .aggregate(
    common,
    OpinionAggregation,
    PricingData,
    TradingSimulation
  )
  .settings(Sync.task)
retrieveManaged := true

// Exclude Emacs autosave files.
excludeFilter in unmanagedSources := ".#*"

