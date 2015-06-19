package io.muvr.analytics.labelling

import akka.analytics.cassandra
import io.muvr.analytics.basic.SparkConfiguration._
import org.apache.spark.SparkContext

object LabellingMain extends App {
  /**
   * Main method for the labelling task.
   * Reads all gathered session data and creates labeled samples from them.
   *
   * The task is trivially parallelizable.
   * Each session can be processed on different partition.
   *
   * @param args Input arguments.
   */
  override def main(args: Array[String]) {

    def transform(i: Seq[Seq[Int]]): Seq[Seq[Int]] = Seq(i.map(_(0)), i.map(_(1)), i.map(_(2)))

    val sc = new SparkContext(sparkConf)

    val input = sc.textFile("file:////Users/martinzapletal/Sandbox/lift/muvr-analytics/basic/src/main/resources/1.csv").map { l =>
      val split = l.split(",")
      Array(split(5).toInt, split(6).toInt, split(7).toInt)
    }.collect()

    val transposed = input.transpose

    //transposed.foreach(x => println(x.mkString(",")))

    import Distance.doubleDistance
    ExerciseLabelling.label[Double](transposed.toSeq.map(_.toSeq.map(_.toDouble)), "Bicep", PartialPeriodicPatternMotifSplit.split(_, Distance.Euclidean(_, _), 100, 10))
  }
}
