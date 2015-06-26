package io.muvr.analytics.labelling

import io.muvr.analytics.labelling.SubSequence.TimeSeries

object Distance {
  /**
   * Computes distance between two time series.
   * @tparam T
   */
  type TimeSeriesDistance[T] = (TimeSeries[T], TimeSeries[T]) => Double

  /**
   * Distance between two elements.
   * @tparam T
   */
  type Distance[T] = (T, T) => Double

  implicit def doubleDistance: Distance[Double] = (a, b) => a - b
  implicit def intDistance: Distance[Int] = (a, b) => a - b

  def Euclidean[T](x: TimeSeries[T], y: TimeSeries[T])(implicit distance: Distance[T]) =
    Math.sqrt(x.zip(y).map(z => Math.pow(distance(z._1, z._2), 2)).sum)
}
