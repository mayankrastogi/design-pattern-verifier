package com.mayankrastogi.cs474.hw2.annotations.processor.tests;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import com.mayankrastogi.cs474.hw2.annotations.processor.IteratorAnnotationProcessor;
import org.junit.Test;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

public class IteratorAnnotationProcessorTest {

    @Test
    public void Iterator_SucceedsOnValidUsage() {
        assertThat(compile("ValidIterator.java")).succeededWithoutWarnings();
    }

    @Test
    public void Iterator_CannotBeAppliedOnInterface() {
        assertThat(compile("IteratorAppliedOnInterface.java")).failed();
    }

    @Test
    public void Iterator_CanContainOnlyOneCurrentItemMethod() {
        assertThat(compile("IteratorWithNoCurrentItemMethod.java")).failed();
        assertThat(compile("IteratorWithTwoCurrentItemMethod.java")).failed();
    }

    @Test
    public void Iterator_CanContainOnlyOneIsDoneMethod() {
        assertThat(compile("IteratorWithNoIsDoneMethod.java")).failed();
        assertThat(compile("IteratorWithTwoIsDoneMethod.java")).failed();
    }

    @Test
    public void Iterator_CanContainOnlyOneNextItemMethod() {
        assertThat(compile("IteratorWithNoNextItemMethod.java")).failed();
        assertThat(compile("IteratorWithTwoNextItemMethod.java")).failed();
    }

    @Test
    public void CurrentItem_MustBeEnclosedWithinIterator() {
        assertThat(compile("CurrentItemNotInIterator.java")).failed();
    }

    @Test
    public void CurrentItem_ReturnTypeMustMatchIteratorAnnotationValue() {
        assertThat(compile("CurrentItemReturnTypeDifferentFromIteratorAnnotationValue.java")).failed();
    }

    @Test
    public void CurrentItem_CannotTakeAnyParameters() {
        assertThat(compile("CurrentItemWithParameters.java")).failed();
    }

    @Test
    public void IsDone_MustBeEnclosedWithinIterator() {
        assertThat(compile("IsDoneNotInIterator.java")).failed();
    }

    @Test
    public void IsDone_ReturnTypeMustBeBoolean() {
        assertThat(compile("IsDoneReturnTypeNotBoolean.java")).failed();
    }

    @Test
    public void IsDone_CannotTakeAnyParameters() {
        assertThat(compile("IsDoneWithParameters.java")).failed();
    }

    @Test
    public void IsDone_MustWarnIfPrivate() {
        assertThat(compile("IsDonePrivate.java")).hadWarningCount(1);
    }

    @Test
    public void NextItem_MustBeEnclosedWithinIterator() {
        assertThat(compile("NextItemNotInIterator.java")).failed();
    }

    @Test
    public void NextItem_ReturnTypeMustMatchIteratorAnnotationValue() {
        assertThat(compile("NextItemReturnTypeDifferentFromIteratorAnnotationValue.java")).failed();
    }

    @Test
    public void NextItem_CannotTakeAnyParameters() {
        assertThat(compile("NextItemWithParameters.java")).failed();
    }

    @Test
    public void NextItem_MustWarnIfPrivate() {
        assertThat(compile("NextItemPrivate.java")).hadWarningCount(1);
    }

    private Compilation compile(String fileName) {
        return javac()
                .withProcessors(new IteratorAnnotationProcessor())
                .compile(JavaFileObjects.forResource(fileName));
    }
}
