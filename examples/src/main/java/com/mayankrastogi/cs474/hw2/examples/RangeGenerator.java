package com.mayankrastogi.cs474.hw2.examples;

import com.mayankrastogi.cs474.hw2.annotations.Iterator;

@Iterator(int.class)
public class RangeGenerator {
    private final int maxValue;

    private int currentValue;

    public RangeGenerator(int from, int to) {
        this.maxValue = to;
        this.currentValue = from - 1;
    }

    @Iterator.CurrentItem
    public int currentValue() {
        return currentValue;
    }

    @Iterator.IsDone
    public boolean isDone() {
        return currentValue == maxValue;
    }

    @Iterator.NextItem
    public int next() {
        return ++currentValue;
    }
}
