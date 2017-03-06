package com.mobfox.rtree.entity;

import java.util.Map;

public class TrainingSample extends Sample {
    public final double value;

    public TrainingSample(Map<String, String> featureValues, double value) {
        super(featureValues);
        this.value = value;
    }
}
