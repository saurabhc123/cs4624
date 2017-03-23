package cs4624.microblog.contribution

import cs4624.microblog.MicroblogPost

/**
  * Created by joeywatts on 3/15/17.
  */
class MicroblogAuthorContributions(var meanOfPostScores: Double,
                                   var sumOfSquaredDifferencesFromMean: Double,
                                   var numberOfPosts: Int) {

  def varianceOfPostScores: Double = sumOfSquaredDifferencesFromMean / numberOfPosts
  def standardDeviationOfTweetScores: Double = Math.sqrt(varianceOfPostScores)
  def weight: Double = meanOfPostScores / standardDeviationOfTweetScores

  def transform(post: MicroblogPost, rawPostScore: Double): Unit = {
    val scoreDifferenceFromCurrentMean = rawPostScore - meanOfPostScores
    val nextMeanOfPostScores = (meanOfPostScores * numberOfPosts + scoreDifferenceFromCurrentMean) / (numberOfPosts + 1)
    val scoreDifferenceFromNextMean = rawPostScore - nextMeanOfPostScores
    this.meanOfPostScores = nextMeanOfPostScores
    this.sumOfSquaredDifferencesFromMean += scoreDifferenceFromCurrentMean * scoreDifferenceFromNextMean
    this.numberOfPosts += 1
  }
}
