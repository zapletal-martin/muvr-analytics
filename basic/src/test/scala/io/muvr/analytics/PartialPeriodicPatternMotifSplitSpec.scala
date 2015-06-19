package io.muvr.analytics

import io.muvr.analytics.labelling.{Distance, PartialPeriodicPatternMotifSplit}
import org.scalatest.{MustMatchers, FlatSpec}

import scala.io.Source

class PartialPeriodicPatternMotifSplitSpec extends FlatSpec with MustMatchers {

  "Subsequence split" should "return recurring subsequences" in {
    val sequence = Seq(
      Seq(0.1, 0.2, 0.3, 0.0, 0.2, 0.5, 0.1, 0.2, 0.3),
      Seq(0.2, 0.1, 0.0, 0.0, 0.0, 0.2, 0.1, 0.0, 0.3))

    val result = PartialPeriodicPatternMotifSplit.split(
      sequence,
      (x, y) => if(x.zip(y).map(i => i._1 - i._2).exists(_ != 0)) 1 else 0,
      0.01, 3, 1, 1)

    result.size must be(2)
    result(0).data(0) must be(Seq(0.1, 0.2, 0.3))
    result(1).data(0) must be(Seq(0.2, 0.1, 0.0))
  }

  "Large sequence" should "return recurring subsequences" in {
    val sequence = Source
      .fromFile("/Users/martinzapletal/Sandbox/lift/muvr-analytics/basic/src/test/resources/1.csv")
      .getLines()
      .map { l =>
        val splits  = l.split(",")
        List(splits(5).toDouble, splits(6).toDouble, splits(7).toDouble)
      }
      .toList
      .transpose

    import Distance.doubleDistance
    val result = PartialPeriodicPatternMotifSplit.split(
      sequence.toSeq.map(_.toSeq), Distance.Euclidean(_, _),
      950,
      100,
      10,
      10)

    result.size must be(3)
    result(0).data(0) must be(Seq(0.1, 0.2, 0.3))
    result(1).data(0) must be(Seq(0.2, 0.1, 0.0))
  }
}
