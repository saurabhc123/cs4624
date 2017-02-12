lazy val PredictionEngine = project
lazy val PricingData = project
lazy val VirtualPortfolio = project.dependsOn(PricingData)
lazy val root = (project in file("."))
.aggregate(
    PredictionEngine,
    PricingData,
    VirtualPortfolio
)
retrieveManaged := true
