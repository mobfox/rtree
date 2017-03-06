package com.mobfox.rtree.function;

import com.mobfox.rtree.entity.Sample;
import com.mobfox.rtree.entity.TrainingSample;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * A function to fit a prediction model to a list of samples.
 * Used to fit {@link  com.mobfox.rtree.model.RTreeLeaf  RTree leaves}
 */
@FunctionalInterface
public interface LeafFitter {
    /**
     * @param samples the samples to which the Leaf predictor should be fitted
     * @return a fitted prediction function
     */
    Function<Sample, Double> fit(Set<String> features, List<TrainingSample> samples);
}
