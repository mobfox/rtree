package com.mobfox.rtree.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Stats {
    public static double median(final List<Double> values) {
        final List<Double> sortableValues = new ArrayList<>(values);
        Collections.sort(sortableValues);

        if (sortableValues.size() == 0) {
            return Double.NaN;
        } else if (sortableValues.size() % 2 == 1) {
            return sortableValues.get((sortableValues.size() - 1) / 2);
        } else {
            final double v1 = sortableValues.get(sortableValues.size() / 2 - 1);
            final double v2 = sortableValues.get(sortableValues.size() / 2);
            return (v1 + v2) / 2;
        }
    }
}
