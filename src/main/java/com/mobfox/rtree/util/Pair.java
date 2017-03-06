package com.mobfox.rtree.util;

public class Pair<T, R> {
    public final T fst;
    public final R snd;

    private Pair(T fst, R snd) {
        this.fst = fst;
        this.snd = snd;
    }

    public static <T, R> Pair<T, R> of(T fst, R snd) {
        return new Pair<>(fst, snd);
    }
}
