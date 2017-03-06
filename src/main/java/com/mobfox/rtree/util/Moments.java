package com.mobfox.rtree.util;

public class Moments {
    public static Moments IDENTITY = Moments.of(0.0);

    private final long count;
    private final double sum;
    private final double squareSum;

    private Moments(long count, double sum, double squareSum) {
        this.count = count;
        this.sum = sum;
        this.squareSum = squareSum;
    }

    public static Moments of(final double d) {
        return new Moments(1, d, d*d);
    }

    public static Moments merge(final Moments m1, final Moments m2) {
        return new Moments(m1.count + m2.count, m1.sum + m2.sum, m1.squareSum + m2.squareSum);
    }

    public long count() {
        return count;
    }

    public double mean() {
        return sum / count;
    }

    public double var() {
        if (count < 2) {
            return Double.POSITIVE_INFINITY;
        } else {
            final double mean = sum / count;
            final double rawVar = squareSum / count - mean*mean;
            final double biasCompensationFactor = count / (count - 1.0);
            return biasCompensationFactor * rawVar;
        }
    }
}
