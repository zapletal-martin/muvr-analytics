package io.muvr.analytics.labelling

import io.muvr.analytics.labelling.Distance.TimeSeriesDistance
import io.muvr.analytics.labelling.SubSequence.{Split, SubSequence, TimeSeries}

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
  case class Cluster(windowStart: Int, windowSize: Int, clusterStarts: Seq[Int], cummulativeDistance: Double)

  def split(
      timeSeriesDistance: TimeSeriesDistance[Double],
      threshold: Double,
      window: Int,
      windowSizeStep: Int = 10,
      windowMoveStep: Int = 30): Split[Double] =
    split(_,  timeSeriesDistance, threshold, window, windowSizeStep, windowMoveStep)

  def split(
      input: Seq[TimeSeries[Double]],
      timeSeriesDistance: TimeSeriesDistance[Double],
      threshold: Double,
      window: Int,
      windowSizeStep: Int,
      windowMoveStep: Int): Seq[SubSequence[Double]] = {

    var clusters: Seq[Seq[Cluster]]= Seq()

    // The algorithm often identifies the same cluster mutliple times.
    // E.g. 100 and 500, 110 and 510 etc. We want to skip those.
    val skip = 30

    for(ts <- 0 to input.size - 1) {
      var tsClusters: Seq[Cluster] = Seq()

      for (i <- 0 to input(ts).length - window by windowMoveStep) {
        var cluster = Cluster(i, window, Seq(), 0)

        for (j <- i + windowMoveStep to input(ts).length - window by windowMoveStep) {
          val first = input(ts).slice(i, i + window)
          val second = input(ts).slice(j, j + window)

          val distance = timeSeriesDistance(first, second)

          val lastCluster = if(cluster.clusterStarts.isEmpty) 0 else cluster.clusterStarts.last

          if (distance < threshold && lastCluster + skip <= j && i + skip <= j) {
            cluster = cluster.copy(clusterStarts = cluster.clusterStarts :+ j, cummulativeDistance = cluster.cummulativeDistance + distance)
          }
        }

        tsClusters = tsClusters :+ cluster
      }

      clusters = clusters :+ tsClusters
    }

    /*println("CLUSTERS")
    clusters.foreach{ ts =>
      println("TS")

      ts.foreach{ cl =>
        println(s"CLUSTER ${cl.windowStart} SIMILAR ${cl.clusterStarts.mkString(",")} WITH CUMMULATIVE DISTANCE ${cl.cummulativeDistance}")
      }
    }

    println()
    println()
    println()
    println("TOPS")*/

    val longest = clusters.map(_.map(_.clusterStarts.size).max)

    val topClusters = clusters.zip(longest).map(cs => cs._1.filter(_.clusterStarts.size == cs._2).sortBy(_.cummulativeDistance).head)

    for(i <- 0 to topClusters.size - 1) {
      println(s"TS $i")
      println(s"CLUSTER ${topClusters(i).windowStart} SIMILAR ${topClusters(i).clusterStarts.mkString(",")} WITH CUMMULATIVE DISTANCE ${topClusters(i).cummulativeDistance}")
      println(topClusters(i).windowStart)
      println(input(i).slice(topClusters(i).windowStart, topClusters(i).windowStart + topClusters(i).windowSize).mkString(","))
      topClusters(i).clusterStarts.foreach{ c =>
        println(c)
        println(input(i).slice(c, c + topClusters(i).windowSize).mkString(","))
      }
    }

    //TODO: Not just first.
    val numbers = topClusters(0).clusterStarts.map{ start =>
      SubSequence.SubSequence(Seq(input(0).slice(start, start + topClusters(0).windowSize)))
    }
/*
    println("--------------------------------------")
    println(input(0).size)
    println(input(0).mkString(","))
    println("--------------------------------------")*/

    /*println("SUBSEQUENCES 0")
    numbers.foreach{ subseq =>
      println(subseq.data(0).mkString(","))
    }*/
    //clusters.map(SubSequence.SubSequence(_))
    //Seq(SubSequence.SubSequence(Seq()))
    /*val motifs = JMotif.series2Mofifs(input(0), window)
    Seq(SubSequence.SubSequence(Seq(Seq())))*/

    numbers
  }
}
