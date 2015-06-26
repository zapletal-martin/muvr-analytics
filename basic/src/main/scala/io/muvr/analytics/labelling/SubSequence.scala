package io.muvr.analytics.labelling

import io.muvr.analytics.labelling.Distance._

object SubSequence {

  /**
   * Time series abstraction.
   * @tparam T
   */
  type TimeSeries[T] = Seq[T]

  /**
   * Subsequence in a time series.
   * @tparam T
   */
  /*type SubSequence[T] = Seq[T]*/

  /**
   * Abstraction of approach that splits multiple time series representing data from sensors.
   * to subsequences where each subsequence contains exactly one repetition of given exercise.
   * Padding before and after the periodic movement is discarded.
   */
  type Split[T] = Seq[TimeSeries[T]] => Seq[SubSequence[T]]
  //type Split[T] = (Seq[TimeSeries[T]], TimeSeriesDistance[T]) => Seq[SubSequence[T]]

  case class SubSequence[T](data: Seq[Seq[T]])

  /**
   * Subsequence representing single periodic repeated movement (single repetition of an exercise).
   * @param label Label.
   * @param data Data.
   */
  case class LabeledSubSequence[T](label: String, data: SubSequence[T])
}
