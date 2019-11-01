package com.mayankrastogi.cs474.hw2.annotations.processor.tests

import com.google.testing.compile.CompilationSubject.assertThat
import com.google.testing.compile.Compiler.javac
import com.google.testing.compile.{Compilation, JavaFileObjects}
import com.mayankrastogi.cs474.hw2.annotations.processor.IterableAggregateAnnotationProcessor
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class IterableAggregateAnnotationProcessorTest extends FunSuite {

  test("IterableAggregateAnnotationProcessor should succeed without warnings on valid usage.") {
    assertThat(compile("ValidIterableAggregate.java")).succeededWithoutWarnings()
  }

  test("@IterableAggregate must not be applicable on an interface.") {
    assertThat(compile("IterableAggregateAppliedOnInterface.java")).failed()
  }

  test("@IterableAggregate's value property must be a class annotated with @Iterator.") {
    assertThat(compile("IterableAggregateAnnotationValueNotAnIterator.java")).failed()
  }

  test("@IterableAggregate must contain at least one method annotated with @IteratorFactory.") {
    assertThat(compile("IterableAggregateWithNoIteratorFactoryMethod.java")).failed()
    assertThat(compile("IterableAggregateWithTwoIteratorFactoryMethod.java")).succeededWithoutWarnings()
  }

  test("@IteratorFactory must be enclosed within a class annotated with @IterableAggregate.") {
    assertThat(compile("IteratorFactoryNotInIterableAggregate.java")).failed()
  }

  test("Return type of a method annotated with @IteratorFactory must match the value property of its parent class's @IterableAggregate annotation.") {
    assertThat(compile("IteratorFactoryReturnTypeDifferentFromIterableAggregateAnnotationValue.java")).failed()
  }

  private def compile(fileName: String): Compilation = {
    javac
      .withProcessors(new IterableAggregateAnnotationProcessor)
      .compile(JavaFileObjects.forResource(fileName))
  }
}
