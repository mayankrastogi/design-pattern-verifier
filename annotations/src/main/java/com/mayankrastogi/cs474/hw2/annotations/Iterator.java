package com.mayankrastogi.cs474.hw2.annotations;

import java.lang.annotation.*;

/**
 * Denotes an iterator which provides a way to sequentially extract elements from an underlying aggregate class.
 * <p>
 * A class annotated with {@link Iterator} must have one and only one method each annotated
 * with @{@link CurrentItem}, @{@link IsDone}, and @{@link NextItem}. The return type of the methods annotated
 * with @{@link CurrentItem} and @{@link NextItem} must match the class specified in the {@link Iterator#value()}
 * of @{@link Iterator} annotation.
 * <p>
 * If methods annotated with @{@link IsDone} or @{@link NextItem} are {@code private}, a warning is issued by the
 * annotation processor, since this may make this iterator useless for its consumers. Instead of issuing a warning, an
 * error can be forced to be raised in this situation by setting the {@link Iterator#treatWarningsAsErrors()} to
 * {@code true}.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@Documented
public @interface Iterator {
    /**
     * @return The {@code Class} of the object that can be extracted from this iterator on each iteration.
     */
    Class<?> value();

    /**
     * @return Force errors to be raised instead of warnings if methods with @{@link IsDone} or @{@link NextItem} are
     * {@code private}.
     */
    boolean treatWarningsAsErrors() default false;

    /**
     * Denotes that this method returns the current item in the current state of iteration.
     * <p>
     * This method should not take any parameters and its return type must match the class specified in the
     * {@link Iterator#value()} of @{@link Iterator} annotation.
     * <p>
     * Ideally, this method should return some default value or throw an error if this method is invoked before a call
     * to the method with @{@link NextItem} has been made on a newly instantiated iterator. This behavior, however, is
     * not enforced at compile-time during annotation processing.
     */
    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.METHOD)
    @Documented
    @interface CurrentItem {
    }

    /**
     * Denotes that this method tells the consumer whether the iterator has finished iterating through all its elements.
     * <p>
     * This method should not take any parameters and its return type must be {@code boolean}. If this method is marked
     * {@code private}, a warning is generated during annotation processing. If {@link Iterator#treatWarningsAsErrors()}
     * is set to {@code true} on the enclosing @{@link Iterator} annotated class, an error is raised instead of a
     * warning.
     */
    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.METHOD)
    @Documented
    @interface IsDone {
    }

    /**
     * Denotes that this method returns the next item in the current state of iteration.
     * <p>
     * This method should not take any parameters and its return type must match the class specified in the
     * {@link Iterator#value()} of @{@link Iterator} annotation. If this method is marked {@code private}, a warning is
     * generated during annotation processing. If {@link Iterator#treatWarningsAsErrors()} is set to {@code true} on
     * the enclosing @{@link Iterator} annotated class, an error is raised instead of a warning.
     * <p>
     * Ideally, this method should throw a {@link java.util.NoSuchElementException} if the iterator has finished
     * iterating all the elements and there are no more elements remaining. This behavior, however, is not enforced at
     * compile-time during annotation processing.
     */
    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.METHOD)
    @Documented
    @interface NextItem {
    }
}
