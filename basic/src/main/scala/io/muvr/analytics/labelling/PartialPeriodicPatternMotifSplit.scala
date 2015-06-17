package io.muvr.analytics.labelling

import io.muvr.analytics.labelling.Distance.TimeSeriesDistance
import io.muvr.analytics.labelling.SubSequence.{SubSequence, TimeSeries}
import io.muvr.analytics.labelling.jmotif.RightWindowAlgorithm

object JMotif {
  def series2Mofifs(input: TimeSeries[Double]): Seq[Int] = {
    val motifs = io.muvr.analytics.labelling.jmotif.SAXFactory.series2Motifs(input.toArray, 10, 6, 10, new RightWindowAlgorithm)
    motifs.getTopHits(10).get(0).getPositions
  }
}

object PartialPeriodicPatternMotifSplit {
  def split(input: Seq[TimeSeries[Double]], timeSeriesDistance: TimeSeriesDistance[Double]): Seq[SubSequence[Double]] = {
    //assert(1 == 1)

    val subSequenceLength = 100
    val treshold = 15

    val motifs = JMotif.series2Mofifs(input(0))

    println(motifs)

    Seq(SubSequence.SubSequence(Seq(Seq())))
  }
}
