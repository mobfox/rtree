package com.mobfox.rtree.function;

import com.mobfox.rtree.entity.TrainingSample;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A function to split a list of samples into sub-lists for some optimal criterion.
 * Used to build {@link  com.mobfox.rtree.model.RTreeNode  RTree nodes}
 */
@FunctionalInterface
public interface NodeSplitter {
    /**
     * @param features features available for splitting
     * @param samples samples to which the tree should be fitted
     * @return the best split found or an {@link  Optional#empty()} if there is no further split available
     */
    Optional<Result> split(Set<String> features, List<TrainingSample> samples);

    class Result {
        public final String splitFeature;
        public final Map<String, List<TrainingSample>> splitSamples;
        public final List<TrainingSample> backupSplitSamples;

        /**
         * @param splitFeature the feature at which this node splits
         * @param splitSamples a map that holds for each feature value the samples with this value
         * @param backupSplitSamples a list of samples which will be used to fit the backup branch
         */
        public Result(String splitFeature, Map<String, List<TrainingSample>> splitSamples,
                      List<TrainingSample> backupSplitSamples) {
            this.splitFeature = splitFeature;
            this.splitSamples = splitSamples;
            this.backupSplitSamples = backupSplitSamples;
        }
    }
}
