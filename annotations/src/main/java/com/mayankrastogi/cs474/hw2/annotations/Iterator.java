package com.mayankrastogi.cs474.hw2.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@Documented
public @interface Iterator {
    Class<?> value();

    boolean treatWarningsAsErrors() default false;

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.METHOD)
    @Documented
    @interface CurrentItem {
    }

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.METHOD)
    @Documented
    @interface NextItem {
    }

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.METHOD)
    @Documented
    @interface IsDone {
    }
}
