lazy val predictionEngine = project
lazy val pricingData = project
lazy val root = (project in file("."))
.aggregate(
    predictionEngine,
    pricingData
)
