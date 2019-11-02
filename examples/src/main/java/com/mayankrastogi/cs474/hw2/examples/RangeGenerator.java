package com.mayankrastogi.cs474.hw2.examples;

import com.mayankrastogi.cs474.hw2.annotations.Iterator;

import java.util.NoSuchElementException;

/**
 * Creates an iterator which generates integers, in the specified range, upon iteration.
 */
@Iterator(int.class)
public class RangeGenerator {
    private final int maxValue;

    private int currentValue;

    /**
     * Creates an iterator which generates integers, in the specified range, upon iteration.
     *
     * @param from The first integer to generate (inclusive).
     * @param to   The last integer to generate (inclusive).
     */
    public RangeGenerator(int from, int to) {
        this.maxValue = to;
        this.currentValue = from - 1;
    }

    @Iterator.CurrentItem
    private int currentValue() {
        return currentValue;
    }

    /**
     * Tells whether all the integers in the range have been generated during the iteration.
     *
     * @return {@code true}} if all integers in the range have been generated, {@code false} otherwise.
     */
    @Iterator.IsDone
    public boolean isDone() {
        return currentValue == maxValue;
    }

    /**
     * Generates the next integer in the range.
     * <p>
     * A {@link NoSuchElementException} is thrown if all the integers in the range have already been generated.
     *
     * @return The next integer in the range.
     */
    @Iterator.NextItem
    public int next() {
        if (isDone()) throw new NoSuchElementException("All items have been generated in the specified range.");
        return ++currentValue;
    }
}
