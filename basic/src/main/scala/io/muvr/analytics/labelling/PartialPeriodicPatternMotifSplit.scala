package io.muvr.analytics.labelling

import io.muvr.analytics.labelling.ExerciseLabeling.SubSequence
import io.muvr.analytics.labelling.ExerciseLabeling.TimeSeries

object PartialPeriodicPatternMotifSplit {
  def split[T](input: Seq[TimeSeries[T]]): Seq[SubSequence[T]] = {
    Seq(SubSequence(Seq(Seq())))
  }
}
