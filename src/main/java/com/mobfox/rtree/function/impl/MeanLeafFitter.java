package com.mobfox.rtree.function.impl;

import com.mobfox.rtree.entity.Sample;
import com.mobfox.rtree.entity.TrainingSample;
import com.mobfox.rtree.function.LeafFitter;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class MeanLeafFitter implements LeafFitter {
    @Override
    public Function<Sample, Double> fit(final Set<String> features, final List<TrainingSample> samples) {
        final double sum = samples.stream().mapToDouble(s -> s.value).sum();
        final double mean = sum / samples.size();
        return (s) -> mean;
    }
}
