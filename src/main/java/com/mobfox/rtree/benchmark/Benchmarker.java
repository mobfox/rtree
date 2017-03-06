package com.mobfox.rtree.benchmark;

import com.mobfox.rtree.entity.TrainingSample;
import com.mobfox.rtree.model.RTree;
import com.mobfox.rtree.util.Stats;

import java.util.List;
import java.util.stream.Collectors;

public class Benchmarker {
    public static class Result {
        public final double residualMean;
        public final double residualMedian;
        public final double residualAbsMean;
        public final double residualAbsMedian;
        public final double residualSquareMean;
        public final double residualSquareMedian;
        public final double r2;

        public Result(double residualMean, double residualMedian, double residualAbsMean, double residualAbsMedian,
                      double residualSquareMean, double residualSquareMedian, double r2) {
            this.residualMean = residualMean;
            this.residualMedian = residualMedian;
            this.residualAbsMean = residualAbsMean;
            this.residualAbsMedian = residualAbsMedian;
            this.residualSquareMean = residualSquareMean;
            this.residualSquareMedian = residualSquareMedian;
            this.r2 = r2;
        }

        @Override
        public String toString() {
            return String.format("Result{residualMean=%.3f, residualMedian=%.3f, " +
                            "residualAbsMean=%.3f, residualAbsMedian=%.3f, residualSquareMean=%.3f, " +
                            "residualSquareMedian=%.3f, r2=%.3f}",
                    residualMean, residualMedian, residualAbsMean, residualAbsMedian, residualSquareMean,
                    residualSquareMedian, r2);
        }
    }

    public static Result benchmark(final RTree model, final List<TrainingSample> samples) {
        final List<Double> actualValues = samples.parallelStream()
                .map(sample -> sample.value)
                .collect(Collectors.toList());

        final List<Double> predictedValues = samples.parallelStream()
                .map(model::predict)
                .collect(Collectors.toList());

        final List<Double> residualValues = samples.parallelStream()
                .map(sample -> model.predict(sample) - sample.value)
                .collect(Collectors.toList());

        final List<Double> absresiduals = residualValues.parallelStream()
                .map(Math::abs)
                .collect(Collectors.toList());

        final List<Double> squareresiduals = residualValues.parallelStream()
                .map(residual -> residual*residual)
                .collect(Collectors.toList());

        return new Result(
                residualValues.stream().mapToDouble(d -> d).sum() / residualValues.size(),
                Stats.median(residualValues),
                absresiduals.stream().mapToDouble(d -> d).sum() / residualValues.size(),
                Stats.median(absresiduals),
                squareresiduals.stream().mapToDouble(d -> d).sum() / residualValues.size(),
                Stats.median(squareresiduals),
                calculateR2(predictedValues, residualValues)
        );
    }

    private static double calculateR2(final List<Double> predictedValues, final List<Double> residualValues) {
        final double ess = predictedValues.parallelStream()
                .mapToDouble(val -> val * val)
                .sum();
        final double rss = residualValues.parallelStream()
                .mapToDouble(val -> val * val)
                .sum();
        final double tss = ess + rss;
        return ess / tss;
    }
}
