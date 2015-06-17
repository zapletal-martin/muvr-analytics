package io.muvr.analytics.labelling.jmotif;

import java.util.*;

public class SAXFactory {

    /**
     * Extracts sub-series from series.
     *
     * @param data The series.
     * @param start The start position.
     * @param end The end position
     * @return sub-series from start to end.
     */
    public static double[] getSubSeries(double[] data, int start, int end) {
        double[] vals = new double[end - start];
        for (int i = 0; i < end - start; i++) {
            vals[i] = data[start + i];
        }
        return vals;
    }

    /**
     * Convert real-valued series into symbolic representation.
     *
     * @param vals Real valued timeseries.
     * @param windowSize The PAA window size.
     * @param cuts The cut values array used for SAX transform.
     * @return The symbolic representation of the given real time-series.
     * @throws TSException If error occurs.
     */
    public static char[] getSaxVals(double[] vals, int windowSize, double[] cuts) throws TSException {
        char[] saxVals;
        if (windowSize == cuts.length + 1) {
            saxVals = TSUtils.ts2String(TSUtils.zNormalize(vals), cuts);
        }
        else {
            saxVals = TSUtils.ts2String(TSUtils.zNormalize(TSUtils.paa(vals, cuts.length + 1)), cuts);
        }
        return saxVals;
    }

    /**
     * Get N top motifs from trie.
     *
     * @param trie The trie.
     * @param maxMotifsNum The number of motifs to report.
     * @return The motifs collection.
     * @throws TrieException If error occurs.
     */
    private static MotifRecords getMotifs(SAXTrie trie, int maxMotifsNum) throws TrieException {

        MotifRecords res = new MotifRecords(maxMotifsNum);

        ArrayList<SAXTrieHitEntry> frequencies = trie.getFrequencies();

        Collections.sort(frequencies);

        // all sorted - from one end we have unique words - those discords
        // from the other end - we have motifs - the most frequent entries
        //
        // what I'll do here - is to populate non-trivial frequent entries into
        // the resulting container
        //

        // picking those non-trivial patterns this method job
        // non-trivial here means the one which are not the same letters
        //

        Set<SAXTrieHitEntry> seen = new TreeSet<SAXTrieHitEntry>();

        int counter = 0;
        // iterating backward - collection is sorted
        for (int i = frequencies.size() - 1; i >= 0; i--) {
            SAXTrieHitEntry entry = frequencies.get(i);
            if (entry.isTrivial(2) || seen.contains(entry) || (2 > entry.getFrequency())) {
                if ((2 > entry.getFrequency())) {
                    break;
                }
                continue;
            }
            else {
                counter += 1;
                res.add(new MotifRecord(entry.getStr(), trie.getOccurences(entry.getStr())));
                seen.add(entry);
                if (counter > maxMotifsNum) {
                    break;
                }
            }
        }
        return res;
    }

    public static MotifRecords series2Motifs(
            double[] series, int windowSize, int alphabetSize,
            int motifsNumToReport, SlidingWindowMarkerAlgorithm markerAlgorithm) throws TrieException, TSException {
        // init the SAX structures
        //

        SAXTrie trie = new SAXTrie(series.length - windowSize, alphabetSize);

        StringBuilder sb = new StringBuilder();
        sb.append("data size: ").append(series.length);

        double max = TSUtils.max(series);
        sb.append("; max: ").append(max);

        double min = TSUtils.min(series);
        sb.append("; min: ").append(min);

        double mean = TSUtils.mean(series);
        sb.append("; mean: ").append(mean);

        int nans = TSUtils.countNaN(series);
        sb.append("; NaNs: ").append(nans);

        Alphabet normalA = new NormalAlphabet();

        Date start = new Date();
        // build the trie
        //
        int currPosition = 0;
        while ((currPosition + windowSize) < series.length) {
            // get the window SAX representation
            double[] subSeries = getSubSeries(series, currPosition, currPosition + windowSize);
            char[] saxVals = getSaxVals(subSeries, windowSize, normalA.getCuts(alphabetSize));
            // add result to the structure
            trie.put(String.valueOf(saxVals), currPosition);
            // increment the position
            currPosition++;
        }
        Date end = new Date();

        start = new Date();
        MotifRecords motifs = getMotifs(trie, motifsNumToReport);
        end = new Date();

        return motifs;
    }
}
