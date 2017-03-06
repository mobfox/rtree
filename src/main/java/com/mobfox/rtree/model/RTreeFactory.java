package com.mobfox.rtree.model;

import com.mobfox.rtree.entity.TrainingSample;
import com.mobfox.rtree.function.LeafFitter;
import com.mobfox.rtree.function.NodeSplitter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RTreeFactory {
    private final NodeSplitter nodeSplitter;
    private final LeafFitter leafFitter;

    public RTreeFactory(NodeSplitter nodeSplitter, LeafFitter leafFitter) {
        this.nodeSplitter = nodeSplitter;
        this.leafFitter = leafFitter;
    }

    public RTree build(final Set<String> features, final List<TrainingSample> samples) {
        final Optional<NodeSplitter.Result> split = nodeSplitter.split(features, samples);
        if (split.isPresent()) {
            return buildNode(features, split.get());
        } else {
            return new RTreeLeaf(leafFitter.fit(features, samples));
        }
    }

    private RTree buildNode(Set<String> oldFeatures, NodeSplitter.Result split) {
        final Set<String> newFeatures = new HashSet<>(oldFeatures);
        newFeatures.remove(split.splitFeature);

        final Map<String, RTree> branches = new ConcurrentHashMap<>();
        split.splitSamples.entrySet().parallelStream()
                .forEach(entry -> branches.put(entry.getKey(), build(newFeatures, entry.getValue())));

        final RTree backupBranch = build(newFeatures, split.backupSplitSamples);

        return new RTreeNode(split.splitFeature, branches, backupBranch);
    }
}
