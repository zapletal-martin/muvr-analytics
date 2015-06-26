package io.muvr.analytics

import io.muvr.analytics.labelling.{FourierSplit, PartialPeriodicPatternMotifSplit}
import org.scalatest._

class FourierSplitSpec extends FlatSpec with MustMatchers {
  "Subsequence split" should "return recurring subsequences" in {
    val sequence = Seq(
      Seq(0.1, 0.2, 0.3, 0.0, 0.2, 0.5, 0.1, 0.2, 0.3),
      Seq(0.2, 0.1, 0.0, 0.0, 0.0, 0.2, 0.1, 0.0, 0.3))

    val result = FourierSplit.split(sequence, 6, 1, 1)

    result.size must be(2)
    result(0).data(0) must be(Seq(0.1, 0.2, 0.3))
    result(1).data(0) must be(Seq(0.2, 0.1, 0.0))
  }
}
