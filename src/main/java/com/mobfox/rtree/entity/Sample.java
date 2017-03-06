package com.mobfox.rtree.entity;

import java.util.Map;

public class Sample {
    private final Map<String, String> featureValues;

    public Sample(Map<String, String> featureValues) {
        this.featureValues = featureValues;
    }

    public String featureValue(final String feature) {
        return featureValues.get(feature);
    }
}
