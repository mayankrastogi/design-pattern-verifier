package com.mayankrastogi.cs474.hw2.annotations.processor.tests;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import com.mayankrastogi.cs474.hw2.annotations.processor.IterableAggregateAnnotationProcessor;
import org.junit.Test;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

public class IterableAggregateAnnotationProcessorTest {

    @Test
    public void IterableAggregate_SucceedsOnValidUsage() {
        assertThat(compile("ValidIterableAggregate.java")).succeededWithoutWarnings();
    }

    @Test
    public void IterableAggregate_CannotBeAppliedOnInterface() {
        assertThat(compile("IterableAggregateAppliedOnInterface.java")).failed();
    }

    @Test
    public void IterableAggregate_AnnotationValueMustBeAnIterator() {
        assertThat(compile("IterableAggregateAnnotationValueNotAnIterator.java")).failed();
    }

    @Test
    public void IterableAggregate_MustContainAtLeastOneIteratorFactoryMethod() {
        assertThat(compile("IterableAggregateWithNoIteratorFactoryMethod.java")).failed();
        assertThat(compile("IterableAggregateWithTwoIteratorFactoryMethod.java")).succeededWithoutWarnings();
    }

    @Test
    public void IteratorFactory_MustBeEnclosedWithinIterableAggregate() {
        assertThat(compile("IteratorFactoryNotInIterableAggregate.java")).failed();
    }

    @Test
    public void IteratorFactory_ReturnTypeMustMatchIterableAggregateAnnotationValue() {
        assertThat(compile("IteratorFactoryReturnTypeDifferentFromIterableAggregateAnnotationValue.java")).failed();
    }

    private Compilation compile(String fileName) {
        return javac()
                .withProcessors(new IterableAggregateAnnotationProcessor())
                .compile(JavaFileObjects.forResource(fileName));
    }
}
