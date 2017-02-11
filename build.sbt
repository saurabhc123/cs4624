lazy val predictionEngine = project
lazy val pricingData = project
lazy val virtualPortfolio = project.dependsOn(pricingData)
lazy val root = (project in file("."))
.aggregate(
    predictionEngine,
    pricingData,
    virtualPortfolio
)
