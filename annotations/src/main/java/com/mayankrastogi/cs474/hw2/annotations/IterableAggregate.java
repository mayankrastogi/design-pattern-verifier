package com.mayankrastogi.cs474.hw2.annotations;

import java.lang.annotation.*;

/**
 * Denotes an aggregate class that provides an iterator to access its elements sequentially.
 * <p>
 * A class annotated with @{@link IterableAggregate} must provide at least one factory method annotated
 * with @{@link IteratorFactory}, whose return type matches the class specified in the {@link IterableAggregate#value()}
 * of @{@link IterableAggregate} annotation.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@Documented
public @interface IterableAggregate {
    /**
     * @return The {@code Class} of the iterator object exposed by this aggregate class.
     */
    Class<?> value();

    /**
     * Denotes a factory method that creates a new instance of an iterator for accessing elements of the parent iterable
     * aggregate.
     * <p>
     * The return type of this method should match the class specified in the {@link IterableAggregate#value()} of its
     * enclosing aggregate class's @{@link IterableAggregate} annotation.
     */
    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.METHOD)
    @Documented
    @interface IteratorFactory {
    }
}