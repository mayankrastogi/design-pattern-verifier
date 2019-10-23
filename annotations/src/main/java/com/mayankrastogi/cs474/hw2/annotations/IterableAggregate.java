package com.mayankrastogi.cs474.hw2.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@Documented
public @interface IterableAggregate {
    Class<?> value();

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.METHOD)
    @Documented
    @interface IteratorFactory {
    }
}