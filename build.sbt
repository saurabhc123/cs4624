lazy val PricingData = project
lazy val PredictionEngine = project.dependsOn(PricingData)
lazy val VirtualPortfolio = project.dependsOn(PricingData)
lazy val root = (project in file("."))
.aggregate(
    PricingData,
    PredictionEngine,
    VirtualPortfolio
).dependsOn(PricingData,PredictionEngine, VirtualPortfolio)
retrieveManaged := true
