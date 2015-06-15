package io.muvr.analytics.labelling

import io.muvr.analytics.labelling.ExerciseLabeling.{Split, SubSequence, TimeSeries}

object ExerciseLabeling extends {

  /**
   * Time series abstraction
   * @tparam T
   */
  type TimeSeries[T] = Seq[T]

  /**
   * Subsequence in a time series
   * @tparam T
   */
  /*type SubSequence[T] = Seq[T]*/

  /**
   * Abstraction of approach that splits multiple time series representing data from sensors
   * to subsequences where each subsequence contains exactly one repetition of given exercise
   * Padding before and after the periodic movement is discarded
   */
  type Split[T] = Seq[TimeSeries[T]] => Seq[SubSequence[T]]


  case class SubSequence[T](data: Seq[Seq[T]])

  /**
   * Subsequence representing single periodic repeated movement (single repetition of an exercise)
   * @param label Label
   * @param data Data
   */
  case class LabeledSubSequence[T](label: String, data: SubSequence[T])

  /**
   * Returns labeled data that can be directly used for training of a machine learning model
   * @param fusedSensorData Data from sensors
   * @param label Correct label for given exercise
   * @param split Abstracted function that extracts features (single repetitions) from the whole set
   * @return Labeled data
   */
  def label[T](fusedSensorData: Seq[TimeSeries[T]], label: String, split: Split[T]): Seq[LabeledSubSequence[T]] = {
    val splits = split(fusedSensorData)

    Seq(LabeledSubSequence(label, SubSequence(Seq(Seq()))))
  }
}


