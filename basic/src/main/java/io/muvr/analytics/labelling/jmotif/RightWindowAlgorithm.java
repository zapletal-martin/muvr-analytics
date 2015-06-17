package io.muvr.analytics.labelling.jmotif;

public class RightWindowAlgorithm implements SlidingWindowMarkerAlgorithm {

    @Override
    public void markVisited(VisitRegistry registry, int startPosition, int intervalLength) {
        // mark to the right
        for (int i = 0; i < intervalLength; i++) {
            registry.markVisited(startPosition + i);
        }
        // grow left
        // for (int i = 0; i < intervalLength; i++) {
        // registry.markVisited(startPosition - i);
        // }

    }
}

