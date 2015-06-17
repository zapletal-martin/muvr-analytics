package io.muvr.analytics.labelling

import io.muvr.analytics.labelling.Distance._
import io.muvr.analytics.labelling.SubSequence._


object ExerciseLabelling extends {

  /**
   * Returns labeled data that can be directly used for training of a machine learning model.
   * @param fusedSensorData Data from sensors.
   * @param label Correct label for given exercise.
   * @param split Abstracted function that extracts features (single repetitions) from the whole set.
   * @return Labeled data.
   */
  def label[T](fusedSensorData: Seq[TimeSeries[T]], label: String, split: PartialSplit[T]): Seq[LabeledSubSequence[T]] = {
    val splits = split(fusedSensorData)

    Seq(LabeledSubSequence(label, SubSequence.SubSequence(Seq(Seq()))))
  }
}


