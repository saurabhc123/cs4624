lazy val common = project
lazy val PredictionEngine = project.dependsOn(common)
lazy val PricingData = project.dependsOn(common)
lazy val VirtualPortfolio = project.dependsOn(common, PricingData)
lazy val TradingSimulation = project.dependsOn(common, VirtualPortfolio)
lazy val root = (project in file("."))
  .aggregate(
    common,
    PredictionEngine,
    PricingData,
    VirtualPortfolio,
    TradingSimulation
  )
retrieveManaged := true

// Exclude Emacs autosave files.
excludeFilter in unmanagedSources := ".#*"

import sbt.complete.Parsers.spaceDelimited
lazy val sync = inputKey[Unit]("Sync the source code to a remote server")
sync := {
  val params = spaceDelimited("<arg>").parsed
  val property = System.getProperty("sync.remote")
  val remote = params.headOption.orElse(Option(property))
  remote match {
    case Some(r) => Seq("rsync", "-avz", "--delete", "--filter=:- .gitignore", "--exclude", ".git/", "./", r) !
    case None => println("Please specify the remote path as a parameter or set the 'sync.remote' system property. Example:\n\tuser@host:mypath/")
  }
}
