package com.mobfox.rtree.model;

import com.mobfox.rtree.entity.Sample;
import com.mobfox.rtree.entity.TrainingSample;
import com.mobfox.rtree.function.LeafFitter;
import com.mobfox.rtree.function.NodeSplitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * This class represents a fully fitted regression tree.
 * The two implementing classes of this interface are:
 * @see RTreeNode
 * @see RTreeLeaf
 */
public interface RTree {
    double predict(Sample sample);

    Stats stats();

    /**
     * @param nodeSplitter a function that splits the samples to create an {@link  com.mobfox.rtree.model.RTreeNode  RTreeNode}
     * @param leafFitter a function that fits a prediction model to the leaves of the tree
     * @param features a list with the features for which all samples have a corresponding value
     * @param samples the samples to which to fit the tree
     * @return a fitted model
     */
    static RTree build(final NodeSplitter nodeSplitter, final LeafFitter leafFitter, final Set<String> features,
                       final List<TrainingSample> samples) {
        final RTreeFactory factory = new RTreeFactory(nodeSplitter, leafFitter);
        return factory.build(features, samples);
    }

    class Stats {
        public final int nLeaves;
        public final int nNodes;
        public final Map<String, Integer> splitCounts;

        /* package */ Stats(int nLeaves, int nNodes, Map<String, Integer> splitCounts) {
            this.nLeaves = nLeaves;
            this.nNodes = nNodes;
            this.splitCounts = splitCounts;
        }

        /* package */ static Stats merge(Stats s1, Stats s2) {
            final Map<String, Integer> splitCounts = new HashMap<>();
            Stream.concat(s1.splitCounts.entrySet().stream(), s2.splitCounts.entrySet().stream())
                    .forEach(entry -> splitCounts.put(entry.getKey(),
                            splitCounts.getOrDefault(entry.getKey(), 0) + entry.getValue()));
            return new Stats(s1.nLeaves + s2.nLeaves, s1.nNodes + s2.nNodes, splitCounts);
        }

        @Override
        public String toString() {
            return "Stats{" +
                    "nLeaves=" + nLeaves +
                    ", nNodes=" + nNodes +
                    '}';
        }
    }
}
