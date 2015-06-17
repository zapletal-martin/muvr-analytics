package io.muvr.analytics.labelling.jmotif;

import java.util.Arrays;

public class TSUtils {

    /** The latin alphabet, lower case letters a-z. */
    static final char[] ALPHABET = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };


    /**
     * Z-Normalize timeseries to the mean zero and standard deviation of one.
     *
     * @param series The timeseries.
     * @return Z-normalized time-series.
     * @throws TSException if error occurs.
     * @throws CloneNotSupportedException
     */
    public static Timeseries zNormalize(Timeseries series) throws TSException,
            CloneNotSupportedException {

        // resulting series
        //
        Timeseries res = series.clone();

        // here I will extract doubles and normailize those, joining together later
        //
        double seriesValues[] = new double[series.size()];
        int idx = 0;
        for (TPoint p : series) {
            seriesValues[idx] = p.value();
            idx++;
        }
        double[] normalValues = zNormalize(seriesValues);

        // get things together
        //
        idx = 0;
        for (TPoint p : res) {
            p.setValue(normalValues[idx]);
            idx++;
        }
        return res;
    }

    /**
     * Finds the maximal value in timeseries.
     *
     * @param series The timeseries.
     * @return The max value.
     */
    public static double max(Timeseries series) {
        if (countNaN(series) == series.size()) {
            return Double.NaN;
        }
        double[] values = series.values();
        double max = Double.MIN_VALUE;
        for (int i = 0; i < values.length; i++) {
            if (max < values[i]) {
                max = values[i];
            }
        }
        return max;
    }

    /**
     * Finds the maximal value in timeseries.
     *
     * @param series The timeseries.
     * @return The max value.
     */
    public static double max(double[] series) {
        if (countNaN(series) == series.length) {
            return Double.NaN;
        }
        double max = Double.MIN_VALUE;
        for (int i = 0; i < series.length; i++) {
            if (max < series[i]) {
                max = series[i];
            }
        }
        return max;
    }

    /**
     * Finds the minimal value in timeseries.
     *
     * @param series The timeseries.
     * @return The min value.
     */
    public static double min(Timeseries series) {
        if (countNaN(series) == series.size()) {
            return Double.NaN;
        }
        double[] values = series.values();
        double min = Double.MAX_VALUE;
        for (int i = 0; i < values.length; i++) {
            if (min > values[i]) {
                min = values[i];
            }
        }
        return min;
    }

    /**
     * Finds the minimal value in timeseries.
     *
     * @param series The timeseries.
     * @return The min value.
     */
    public static double min(double[] series) {
        if (countNaN(series) == series.length) {
            return Double.NaN;
        }
        double min = Double.MAX_VALUE;
        for (int i = 0; i < series.length; i++) {
            if (min > series[i]) {
                min = series[i];
            }
        }
        return min;
    }

    /**
     * Converts the vector into one-row matrix.
     *
     * @param vector The vector.
     * @return The matrix.
     */
    public static double[][] asMatrix(double[] vector) {
        double[][] res = new double[1][vector.length];
        for (int i = 0; i < vector.length; i++) {
            res[0][i] = vector[i];
        }
        return res;
    }

    /**
     * Approximate the timeseries using PAA. If the timeseries has some NaN's they are handled as
     * follows: 1) if all values of the piece are NaNs - the piece is approximated as NaN, 2) if there
     * are some (more or equal one) values happened to be in the piece - algorithm will handle it as
     * usual - getting the mean.
     *
     * @param ts The timeseries to approximate.
     * @param paaSize The desired length of approximated timeseries.
     * @return PAA-approximated timeseries.
     * @throws TSException if error occurs.
     * @throws CloneNotSupportedException if error occurs.
     */
    public static Timeseries paa(Timeseries ts, int paaSize) throws TSException,
            CloneNotSupportedException {
        // fix the length
        int len = ts.size();
        // check for the trivial case
        if (len == paaSize) {
            return ts.clone();
        }
        else {
            // get values and timestamps
            double[][] vals = ts.valuesAsMatrix();
            long[] tStamps = ts.tstamps();
            // work out PAA by reshaping arrays
            double[][] res;
            if (len % paaSize == 0) {
                res = MatrixFactory.reshape(vals, len / paaSize, paaSize);
            }
            else {
                double[][] tmp = new double[paaSize][len];
                // System.out.println(Matrix.toString(tmp));
                for (int i = 0; i < paaSize; i++) {
                    for (int j = 0; j < len; j++) {
                        tmp[i][j] = vals[0][j];
                    }
                }
                // System.out.println(Matrix.toString(tmp));
                double[][] expandedSS = MatrixFactory.reshape(tmp, 1, len * paaSize);
                // System.out.println(Matrix.toString(expandedSS));
                res = MatrixFactory.reshape(expandedSS, len, paaSize);
                // System.out.println(Matrix.toString(res));
            }
            //
            // now, here is a new trick comes in game - because we have so many
            // "lost" values
            // PAA game rules will change - we will omit NaN values and put NaNs
            // back to PAA series
            //
            //
            // this is the old line of code here:
            // double[] newVals = MatrixFactory.colMeans(res);
            //
            // i will need to test this though
            //
            //
            double[] newVals = MatrixFactory.colMeans(res);

            // work out timestamps
            long start = tStamps[0];
            long interval = tStamps[len - 1] - start;
            double increment = Long.valueOf(interval).doubleValue() / Long.valueOf(paaSize).doubleValue();
            long[] newTstamps = new long[paaSize];
            for (int i = 0; i < paaSize; i++) {
                newTstamps[i] = start + Double.valueOf(increment / 2.0D + i * increment).longValue();
            }
            return new Timeseries(newVals, newTstamps);
        }
    }

    /**
     * Approximate the timeseries using PAA. If the timeseries has some NaN's they are handled as
     * follows: 1) if all values of the piece are NaNs - the piece is approximated as NaN, 2) if there
     * are some (more or equal one) values happened to be in the piece - algorithm will handle it as
     * usual - getting the mean.
     *
     * @param ts The timeseries to approximate.
     * @param paaSize The desired length of approximated timeseries.
     * @return PAA-approximated timeseries.
     * @throws TSException if error occurs.
     */
    public static double[] paa(double[] ts, int paaSize) throws TSException {
        // fix the length
        int len = ts.length;
        // check for the trivial case
        if (len == paaSize) {
            return Arrays.copyOf(ts, ts.length);
        }
        else {
            // get values and timestamps
            double[][] vals = asMatrix(ts);
            // work out PAA by reshaping arrays
            double[][] res;
            if (len % paaSize == 0) {
                res = MatrixFactory.reshape(vals, len / paaSize, paaSize);
            }
            else {
                double[][] tmp = new double[paaSize][len];
                for (int i = 0; i < paaSize; i++) {
                    for (int j = 0; j < len; j++) {
                        tmp[i][j] = vals[0][j];
                    }
                }
                double[][] expandedSS = MatrixFactory.reshape(tmp, 1, len * paaSize);
                res = MatrixFactory.reshape(expandedSS, len, paaSize);
            }
            double[] newVals = MatrixFactory.colMeans(res);

            return newVals;
        }

    }

    /**
     * Computes the mean value of timeseries.
     *
     * @param series The timeseries.
     * @return The mean value.
     */
    public static double mean(Timeseries series) {
        double res = 0D;
        int count = 0;
        for (TPoint tp : series) {
            if (Double.isNaN(tp.value()) || Double.isInfinite(tp.value())) {
                continue;
            }
            else {
                res += tp.value();
                count += 1;
            }
        }
        if (count > 0) {
            return res / ((Integer) count).doubleValue();
        }
        return Double.NaN;
    }

    /**
     * Computes the mean value of timeseries.
     *
     * @param series The timeseries.
     * @return The mean value.
     */
    public static double mean(double[] series) {
        double res = 0D;
        int count = 0;
        for (double tp : series) {
            if (Double.isNaN(tp) || Double.isInfinite(tp)) {
                continue;
            }
            else {
                res += tp;
                count += 1;
            }
        }
        if (count > 0) {
            return res / ((Integer) count).doubleValue();
        }
        return Double.NaN;
    }

    /**
     * Computes the standard deviation of timeseries.
     *
     * @param series The timeseries.
     * @return the standard deviation.
     */
    public static double stDev(Timeseries series) {
        double num0 = 0D;
        double sum = 0D;
        int count = 0;
        for (TPoint tp : series) {
            if (Double.isNaN(tp.value()) || Double.isInfinite(tp.value())) {
                continue;
            }
            else {
                num0 = num0 + tp.value() * tp.value();
                sum = sum + tp.value();
                count += 1;
            }
        }
        if (count > 0) {
            double len = ((Integer) count).doubleValue();
            return Math.sqrt((len * num0 - sum * sum) / (len * (len - 1)));
        }
        return Double.NaN;
    }

    /**
     * Computes the standard deviation of timeseries.
     *
     * @param series The timeseries.
     * @return the standard deviation.
     */
    public static double stDev(double[] series) {
        double num0 = 0D;
        double sum = 0D;
        int count = 0;
        for (double tp : series) {
            if (Double.isNaN(tp) || Double.isInfinite(tp)) {
                continue;
            }
            else {
                num0 = num0 + tp * tp;
                sum = sum + tp;
                count += 1;
            }
        }
        if (count > 0) {
            double len = ((Integer) count).doubleValue();
            return Math.sqrt((len * num0 - sum * sum) / (len * (len - 1)));
        }
        return Double.NaN;
    }

    /**
     * Counts the number of NaNs' in the timeseries.
     *
     * @param series The timeseries.
     * @return The count of NaN values.
     */
    public static int countNaN(double[] series) {
        int res = 0;
        for (double d : series) {
            if (Double.isInfinite(d) || Double.isNaN(d)) {
                res += 1;
            }
        }
        return res;
    }

    /**
     * Counts the number of NaNs' in the timeseries.
     *
     * @param series The timeseries.
     * @return The count of NaN values.
     */
    private static int countNaN(Timeseries series) {
        int res = 0;
        for (TPoint tp : series) {
            if (Double.isInfinite(tp.value()) || Double.isNaN(tp.value())) {
                res += 1;
            }
        }
        return res;
    }

    /**
     * Z-Normalize timeseries to the mean zero and standard deviation of one.
     *
     * @param series The timeseries.
     * @return Z-normalized time-series.
     * @throws TSException if error occurs.
     */
    public static double[] zNormalize(double[] series) throws TSException {

        // this is the resulting normalization
        //
        double[] res = new double[series.length];

        // get mean and sdev, NaN's will be handled
        //
        double mean = mean(series);
        double sd = stDev(series);

        // check if we hit special case, where something got NaN
        //
        if (Double.isInfinite(mean) || Double.isNaN(mean) || Double.isInfinite(sd) || Double.isNaN(sd)) {

            // case[1] single value within the timeseries, normalize this value to 1.0 - magic number
            //
            int nanNum = countNaN(series);
            if ((series.length - nanNum) == 1) {
                for (int i = 0; i < res.length; i++) {
                    if (Double.isInfinite(series[i]) || Double.isNaN(series[i])) {
                        res[i] = Double.NaN;
                    }
                    else {
                        res[i] = 1.0D;
                    }
                }
            }

            // case[2] all values are NaN's
            //
            else if (series.length == nanNum) {
                for (int i = 0; i < res.length; i++) {
                    res[i] = Double.NaN;
                }
            }
        }

        // another special case, where SD happens to be close to a zero, i.e. they all are the same for
        // example
        //
        else if (sd <= 0.001D) {

            // here I assign another magic value - 0.001D which makes to middle band of the normal
            // Alphabet
            //
            for (int i = 0; i < res.length; i++) {
                if (Double.isInfinite(series[i]) || Double.isNaN(series[i])) {
                    res[i] = series[i];
                }
                else {
                    res[i] = 0.1D;
                }
            }
        }

        // normal case, everything seems to be fine
        //
        else {
            // sd and mean here, - go-go-go
            for (int i = 0; i < res.length; i++) {
                res[i] = (series[i] - mean) / sd;
            }
        }
        return res;

    }

    /**
     * Z-Normalize timeseries to the mean zero and standard deviation of one.
     *
     * @param series The timeseries.
     * @param mean The mean values.
     * @param sd The standard deviation.
     * @return Z-normalized time-series.
     * @throws TSException if error occurs.
     */
    public static double[] zNormalize(double[] series, double mean, double sd) throws TSException {

        // this is the resulting normalization
        //
        double[] res = new double[series.length];

        // check if we hit special case, where something got NaN
        //
        if (Double.isInfinite(mean) || Double.isNaN(mean) || Double.isInfinite(sd) || Double.isNaN(sd)) {

            // case[1] single value within the timeseries, normalize this value to 1.0 - magic number
            //
            int nanNum = countNaN(series);
            if ((series.length - nanNum) == 1) {
                for (int i = 0; i < res.length; i++) {
                    if (Double.isInfinite(series[i]) || Double.isNaN(series[i])) {
                        res[i] = Double.NaN;
                    }
                    else {
                        res[i] = 1.0D;
                    }
                }
            }

            // case[2] all values are NaN's
            //
            else if (series.length == nanNum) {
                for (int i = 0; i < res.length; i++) {
                    res[i] = Double.NaN;
                }
            }
        }

        // another special case, where SD happens to be close to a zero, i.e. they all are the same for
        // example
        //
        else if (sd <= 0.001D) {

            // here I assign another magic value - 0.001D which makes to middle band of the normal
            // Alphabet
            //
            for (int i = 0; i < res.length; i++) {
                if (Double.isInfinite(series[i]) || Double.isNaN(series[i])) {
                    res[i] = series[i];
                }
                else {
                    res[i] = 0.1D;
                }
            }
        }

        // normal case, everything seems to be fine
        //
        else {
            // sd and mean here, - go-go-go
            for (int i = 0; i < res.length; i++) {
                res[i] = (series[i] - mean) / sd;
            }
        }
        return res;

    }


    /**
     * Get mapping of a number to char.
     *
     * @param value the value to map.
     * @param cuts the array of intervals.
     * @return character corresponding to numeric value.
     */
    public static char num2char(double value, double[] cuts) {
        int count = 0;
        while ((count < cuts.length) && (cuts[count] <= value)) {
            count++;
        }
        return ALPHABET[count];
    }

    /**
     * Converts the timeseries into string using given cuts intervals. Useful for not-normal
     * distribution cuts.
     *
     * @param vals The timeseries.
     * @param cuts The cut intervals.
     * @return The timeseries SAX representation.
     */
    public static char[] ts2String(double[] vals, double[] cuts) {
        char[] res = new char[vals.length];
        for (int i = 0; i < vals.length; i++) {
            res[i] = num2char(vals[i], cuts);
        }
        return res;
    }
}
