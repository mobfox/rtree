package com.mobfox.rtree.model;

import com.mobfox.rtree.entity.Sample;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An interior node of an {@link  com.mobfox.rtree.model.RTree  RTree}.
 * Responsible for splitting the data according to some criterion such that the leaf nodes can fit simpler models to the
 * data.
 */
public class RTreeNode implements RTree {
    private final String splitFeature;
    private final Map<String, RTree> branches;
    private final RTree backupBranch;

    /**
     * @param splitFeature the feature by whose value this node splits
     * @param branches a mapping for each feature value to one sub-tree
     * @param backupBranch a branch for unknown feature values or feature values with insufficient training data
     */
    public RTreeNode(String splitFeature, Map<String, RTree> branches, RTree backupBranch) {
        this.splitFeature = splitFeature;
        this.branches = branches;
        this.backupBranch = backupBranch;
    }

    public double predict(final Sample sample) {
        final String featureValue = sample.featureValue(splitFeature);
        final RTree branch = branches.getOrDefault(featureValue, backupBranch);
        return branch.predict(sample);
    }

    @Override
    public Stats stats() {
        final Map<String, Integer> personalSplitCounts =
                branches.keySet().stream().collect(Collectors.toMap(val -> val, val -> 1));
        final Stats personalStats = new Stats(0, 1, personalSplitCounts);

        return Stream.concat(branches.values().stream(), Stream.of(backupBranch))
                .map(RTree::stats)
                .reduce(personalStats, Stats::merge);
    }
}
