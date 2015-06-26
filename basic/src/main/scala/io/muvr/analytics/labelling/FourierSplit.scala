package io.muvr.analytics.labelling

import breeze.linalg.DenseVector
import io.muvr.analytics.labelling.SubSequence._

object FourierSplit {
  def split(
      window: Int,
      windowSizeStep: Int = 10,
      windowMoveStep: Int = 30): Split[Double] = {
    split(_, window, windowSizeStep, windowMoveStep)
  }

  def split(
      input: Seq[TimeSeries[Double]],
      window: Int,
      windowSizeStep: Int,
      windowMoveStep: Int): Seq[SubSequence[Double]] = {

    for(i <- 0 to input.size - 1) {
      val vector = DenseVector(input(i).toArray)
      val fft = breeze.signal.fourierTr(vector)

      println("fft")
      println(fft)
    }

    Seq()
  }
}
