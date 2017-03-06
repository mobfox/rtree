package com.mobfox.rtree.function.impl;

import com.mobfox.rtree.entity.TrainingSample;
import com.mobfox.rtree.function.NodeSplitter;
import com.mobfox.rtree.util.Moments;
import com.mobfox.rtree.util.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mobfox.rtree.util.Moments.IDENTITY;

/**
 * A node splitter that tries to split by the feature value that reduces the interior variance of the buckets as much as
 * possible.
 */
public class VarianceReductionNodeSplitter implements NodeSplitter {
    private final int minBucketSize;
    private final double minVarianceReduction;
    private final double minVarianceBeforeSplit;

    /**
     * @param minBucketSize minimum number of samples per feature value to consider making a bucket for that feature
     *                      value
     * @param minVarianceReduction minimum relative variance reduction of the returned split (e.g. 0.1 means that the
     *                             split needs to reduce the variance by at least 10%)
     * @param minVarianceBeforeSplit minimum variance that the initial data needs to have in order to consider further
     *                               splitting the node, as when all samples have the same value there is no point in
     *                               creating further sub-trees
     */
    public VarianceReductionNodeSplitter(int minBucketSize, double minVarianceReduction,
                                         double minVarianceBeforeSplit) {
        this.minBucketSize = minBucketSize;
        this.minVarianceReduction = minVarianceReduction;
        this.minVarianceBeforeSplit = minVarianceBeforeSplit;
    }

    @Override
    public Optional<Result> split(final Set<String> features, final List<TrainingSample> samples) {
        final double totalVar = samples.parallelStream()
                .map(sample -> Moments.of(sample.value))
                .reduce(IDENTITY, Moments::merge)
                .var();

        if (totalVar < minVarianceBeforeSplit) {
            return Optional.empty();
        }

        final Optional<Pair<Result, Double>> bestSplit = features.parallelStream()
                .map(feature -> cleanSplit(feature, splitByFeature(feature, samples)))
                .filter(split -> split.splitSamples.size() > 0)
                .map(split -> Pair.of(split, varRed(totalVar, samples.size(), split)))
                .filter(split -> split.snd >= minVarianceReduction)
                .max(Comparator.comparingDouble(s -> s.snd));

        return bestSplit.map(split -> split.fst);
    }

    private Map<String, List<TrainingSample>> splitByFeature(final String feature, final List<TrainingSample> samples) {
        final Map<String, List<TrainingSample>> splitSamples = new ConcurrentHashMap<>();
        samples.forEach(sample -> {
            final String featureValue = sample.featureValue(feature);
            splitSamples.computeIfAbsent(featureValue, key -> new ArrayList<>());
            splitSamples.get(featureValue).add(sample);
        });
        return splitSamples;
    }

    private NodeSplitter.Result cleanSplit(final String feature, final Map<String, List<TrainingSample>> split) {
        final Map<String, List<TrainingSample>> cleanBranches = split.entrySet().parallelStream()
                .filter(entry -> entry.getValue().size() >= minBucketSize)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        final List<TrainingSample> backupBucket = split.entrySet().parallelStream()
                .filter(entry -> entry.getValue().size() < minBucketSize)
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toList());

        if (backupBucket.size() >= minBucketSize) {
            return new Result(feature, cleanBranches, backupBucket);
        } else {
            final List<TrainingSample> allSamples = split.values().parallelStream()
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            return new Result(feature, cleanBranches, allSamples);
        }
    }

    private double varRed(final double totalVar, final int nSamples, final Result split) {
        final double reducedVar = Stream.concat(split.splitSamples.values().stream(), Stream.of(split.backupSplitSamples))
                .map(bucket -> bucket.stream()
                        .map(sample -> Moments.of(sample.value))
                        .reduce(IDENTITY, Moments::merge)
                )
                .mapToDouble(moments -> moments.var() * moments.count() / nSamples)
                .sum();

        return (totalVar - reducedVar) / totalVar;
    }
}
