lazy val PredictionEngine = project
lazy val PricingData = project
lazy val root = (project in file("."))
.aggregate(
    PredictionEngine,
    PricingData
)
