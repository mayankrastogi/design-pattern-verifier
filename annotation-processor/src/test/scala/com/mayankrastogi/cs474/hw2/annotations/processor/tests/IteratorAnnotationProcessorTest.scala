package com.mayankrastogi.cs474.hw2.annotations.processor.tests

import com.google.testing.compile.CompilationSubject.assertThat
import com.google.testing.compile.Compiler.javac
import com.google.testing.compile.{Compilation, JavaFileObjects}
import com.mayankrastogi.cs474.hw2.annotations.processor.IteratorAnnotationProcessor
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatestplus.junit.JUnitRunner

/**
 * Tests that the [[IteratorAnnotationProcessor]] is able to catch issues mentioned in this test suite.
 */
@RunWith(classOf[JUnitRunner])
class IteratorAnnotationProcessorTest extends FunSuite {

  test("IteratorAnnotationProcessor should succeed without warnings on valid usage.") {
    assertThat(compile("ValidIterator.java")).succeededWithoutWarnings()
  }

  test("@Iterator must not be applicable on an interface.") {
    assertThat(compile("IteratorAppliedOnInterface.java")).failed()
  }

  test("@Iterator must contain only one method annotated with @CurrentItem.") {
    assertThat(compile("IteratorWithNoCurrentItemMethod.java")).failed()
    assertThat(compile("IteratorWithTwoCurrentItemMethod.java")).failed()
  }

  test("@Iterator must contain only one method annotated with @IsDone.") {
    assertThat(compile("IteratorWithNoIsDoneMethod.java")).failed()
    assertThat(compile("IteratorWithTwoIsDoneMethod.java")).failed()
  }

  test("@Iterator must contain only one method annotated with @NextItem.") {
    assertThat(compile("IteratorWithNoNextItemMethod.java")).failed()
    assertThat(compile("IteratorWithTwoNextItemMethod.java")).failed()
  }

  test("@CurrentItem must be enclosed within a class annotated with @Iterator.") {
    assertThat(compile("CurrentItemNotInIterator.java")).failed()
  }

  test("Return type of method annotated with @CurrentItem must match the value property of its parent " +
    "class's @Iterator annotation.") {
    assertThat(compile("CurrentItemReturnTypeDifferentFromIteratorAnnotationValue.java")).failed()
  }

  test("Method annotated with @CurrentItem must not take any parameters.") {
    assertThat(compile("CurrentItemWithParameters.java")).failed()
  }

  test("@IsDone must be enclosed within a class annotated with @Iterator") {
    assertThat(compile("IsDoneNotInIterator.java")).failed()
  }

  test("Return type of method annotated with @IsDone must be boolean.") {
    assertThat(compile("IsDoneReturnTypeNotBoolean.java")).failed()
  }

  test("Method annotated with @IsDone must not take any parameters.") {
    assertThat(compile("IsDoneWithParameters.java")).failed()
  }

  test("Method annotated with @IsDone must generate a warning if it is private") {
    assertThat(compile("IsDonePrivate.java")).hadWarningCount(1)
  }

  test("@NextItem must be enclosed within a class annotated with @Iterator") {
    assertThat(compile("NextItemNotInIterator.java")).failed()
  }

  test("Return type of method annotated with @NextItem must match the value property of its parent " +
    "class's @Iterator annotation.") {
    assertThat(compile("NextItemReturnTypeDifferentFromIteratorAnnotationValue.java")).failed()
  }

  test("Method annotated with @NextItem must not take any parameters.") {
    assertThat(compile("NextItemWithParameters.java")).failed()
  }

  test("Method annotated with @NextItem must generate a warning if it is private") {
    assertThat(compile("NextItemPrivate.java")).hadWarningCount(1)
  }

  /**
   * Compiles the specified Java source file, present in the `src/test/resources` directory, and runs the
   * [[IteratorAnnotationProcessor]] during the compilation.
   *
   * @param fileName The Java source file to compile. The file should be present in the `src/test/resources` directory.
   * @return The result of the compilation.
   */
  private def compile(fileName: String): Compilation = {
    javac
      .withProcessors(new IteratorAnnotationProcessor)
      .compile(JavaFileObjects.forResource(fileName))
  }
}
