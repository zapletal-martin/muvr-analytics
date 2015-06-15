package io.muvr.analytics.labelling

import akka.analytics.cassandra
import io.muvr.analytics.basic.SparkConfiguration._
import org.apache.spark.SparkContext

class LabellingMain {
  def main(args: Array[String]) {

    def transform(i: Seq[Seq[Int]]): Seq[Seq[Int]] = Seq(i.map(_(0)), i.map(_(1)), i.map(_(2)))

    val sc = new SparkContext(sparkConf)

    val input = sc.textFile("test").map { l =>
      val split = l.split(",")
      Seq(split(0).toInt, split(1).toInt, split(2).toInt)
    }.collect()

    ExerciseLabeling.label[Int](input.toSeq, "Bicep", PartialPeriodicPatternMotifSplit.split)
  }
}
