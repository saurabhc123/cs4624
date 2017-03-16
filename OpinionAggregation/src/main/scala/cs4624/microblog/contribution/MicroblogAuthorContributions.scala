package cs4624.microblog.contribution

import cs4624.microblog.MicroblogPost

/**
  * Created by joeywatts on 3/15/17.
  */
case class MicroblogAuthorContributions(meanOfPostScores: Double,
                                        sumOfSquaredDifferencesFromMean: Double,
                                        numberOfPosts: Int) {

  val varianceOfPostScores: Double = sumOfSquaredDifferencesFromMean / numberOfPosts
  val standardDeviationOfTweetScores: Double = Math.sqrt(varianceOfPostScores)
  val weight: Double = meanOfPostScores / standardDeviationOfTweetScores

  def transform(post: MicroblogPost, rawPostScore: Double): MicroblogAuthorContributions = {
    val scoreDifferenceFromCurrentMean = rawPostScore - meanOfPostScores
    val nextMeanOfPostScores = meanOfPostScores + scoreDifferenceFromCurrentMean / (numberOfPosts + 1)
    val scoreDifferenceFromNextMean = rawPostScore - nextMeanOfPostScores
    this.copy(
      meanOfPostScores = nextMeanOfPostScores,
      sumOfSquaredDifferencesFromMean = sumOfSquaredDifferencesFromMean + scoreDifferenceFromCurrentMean * scoreDifferenceFromNextMean,
      numberOfPosts = numberOfPosts + 1
    )
  }
}
