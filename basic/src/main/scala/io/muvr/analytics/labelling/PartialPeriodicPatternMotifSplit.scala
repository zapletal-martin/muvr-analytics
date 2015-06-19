package io.muvr.analytics.labelling

import io.muvr.analytics.labelling.Distance.TimeSeriesDistance
import io.muvr.analytics.labelling.SubSequence.{SubSequence, TimeSeries}

/*object JMotif {
  def series2Mofifs(input: TimeSeries[Double], window: Int): Seq[Int] = {
    val motifs = SAXFactory
      .series2Motifs(input.toArray, window, 9, 1, new LargeWindowAlgorithm)
      .getTopHits(10)

    for(i <- 0 to motifs.size() - 1) {
      println(s"MOTIF: $i")
      println(s"POSITIONS: ${motifs.get(i).getPositions.mkString(",")}")
      println(s"PAYLOAD: ${motifs.get(i).getPayload}")
    }

    motifs.get(0).getPositions
  }
}*/

object PartialPeriodicPatternMotifSplit {
  def split(
      input: Seq[TimeSeries[Double]],
      timeSeriesDistance: TimeSeriesDistance[Double],
      threshold: Double,
      window: Int,
      windowSizeStep: Int = 10,
      windowMoveStep: Int = 30): Seq[SubSequence[Double]] = {

    var clusters: Seq[Seq[Seq[Double]]]= Seq()
    var best: Map[Int, (Double, Int)] = Map()

    for(ts <- 0 to input.size - 1) {
      var tsclusters: Seq[Seq[Double]] = Seq()

      for (i <- 0 to input(ts).length - window by windowMoveStep) {
        for (j <- i + windowMoveStep to input(ts).length - window by windowMoveStep) {
          val first = input(ts).slice(i, i + window)
          val second = input(ts).slice(j, j + window)

          if (timeSeriesDistance(first, second) < threshold) {
            tsclusters = tsclusters :+ first :+ second
          }
        }
      }

      clusters = clusters :+ tsclusters
    }

    println("CLUSTERS")
    println(clusters.map(_.mkString(",")).mkString("\r\n"))

    clusters.map(SubSequence.SubSequence(_))

    /*val motifs = JMotif.series2Mofifs(input(0), window)
    Seq(SubSequence.SubSequence(Seq(Seq())))*/
  }
}
