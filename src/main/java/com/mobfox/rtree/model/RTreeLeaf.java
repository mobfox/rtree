package com.mobfox.rtree.model;

import com.mobfox.rtree.entity.Sample;

import java.util.Collections;
import java.util.function.Function;

/**
 * A terminal node of an {@link  com.mobfox.rtree.model.RTree  RTree}.
 * Responsible for making the actual predictions.
 */
public class RTreeLeaf implements RTree {
    private final Function<Sample, Double> leafModel;

    /**
     * @param leafModel the fitted prediction function for this terminal leaf node
     */
    public RTreeLeaf(Function<Sample, Double> leafModel) {
        this.leafModel = leafModel;
    }

    public double predict(final Sample sample) {
        return leafModel.apply(sample);
    }

    @Override
    public Stats stats() {
        return new Stats(1, 0, Collections.emptyMap());
    }
}
