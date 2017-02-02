/**
  * This class will serve as the main way that data is transferred between each stage
  * Created by Eric on 2/1/2017.
  */
case class Tweet(identifier: String, text:String, label: Option[Double]) {}
