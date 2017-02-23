package main.DataTypes

import java.time.Instant

/**
 * Created by saur6410 on 2/12/17.
 */
class JudgeScore (val identifier: String , val rawPredictionScore : Double, val timestamp : Instant) extends java.io.Serializable{

}
