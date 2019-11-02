## CS 474 - Object Oriented Languages and Environments
## Homework 2 - Design Pattern Verifier (Iterator Pattern)

---

### Overview

The objective of this homework was to write an annotation processor that will verify whether a certain class, annotated with the packaged annotations, uses the said design pattern correctly.

My implementation consists of two annotation processors that verify the usage of the **Iterator** design pattern. Three example classes are provided that demonstrate the use of the iterator design pattern and also demonstrate the various cases that my annotation processor is able to handle. Furthermore, an exhaustive test suite is provided with the annotation processor to verify its correctness.

### Instructions

#### Prerequisites

- [Java 11](https://www.oracle.com/technetwork/java/javase/downloads/index.html) or above

#### Running the application

1. Clone or download this repository onto your system
2. Open the Command Prompt (if using Windows) or the Terminal (if using Linux/Mac) and browse to the project directory
3. Build the project using Gradle; this will run the test cases and the annotation processor on the provided examples
    
    ```
    gradlew clean build
    ```
    
4. If you want detailed debug messages to be printed during annotation processing, build the project using the following command

    ```
    gradlew clean build -PdebugAnnotationProcessor
    ``` 

5. Run the project to see the output from the examples program

    ```
    gradlew run
    ```

### Project Structure

This project makes use of Gradle's multi-project build and is divided into 3 sub-projects:

1. **`annotations`:** Contains all the annotations defined in the project. Tha annotations are written in **Java**.
2. **`annotation-processor`:** Contains all the annotation processors that verify correctness of classes annotated with the annotation from the `annotations` sub-project. Also contains **test cases** for testing the annotation processors themselves. The annotation processors are written in **Scala**.
3. **`examples`:** Contains 3 example classes that implement the **Iterator** design pattern and are annotated with annotations from the `annotations` project. A main program, written in **Scala**, demonstrates how these classes may be used. The example classes are written in **Java**.  

### What is the Iterator Design Pattern?

According to the **[Gang Of Four Design Pattern Book](https://w3sdesign.com/GoF_Design_Patterns_Reference0100.pdf)**, the iterator design pattern is a **behavioral** design pattern which *"provides a way to access the elements of an aggregate object sequentially without exposing its underlying representation"*.

There are two main participants in the implementation of the iterator design pattern:

1. **Aggregate:** An aggregate object which provides a way of creating an iterator.
2. **Iterator:** Provides a set of methods for accessing and traversing the elements of the aggregate. At minimum, an iterator should allow the consumer to *get the next element* and check the *availability of more elements*.

### The Annotations

This project provides two main annotations for the two main participants of the iterator design pattern - `@IterableAggregate` and `@Iterator`. Both of these annotations contain child annotations which can be used to annotate the methods in the classes annotated with the two main annotations. These annotations collectively  help implement the iterator design pattern.

All of these annotations have a retention policy of `SOURCE`, meaning they are only available during compile-time annotation processing. 

#### The `@IterableAggregate` Annotation

The `@IterableAggregate` annotation can be used to annotate a class that represents an ***aggregate***. Although an `@IterableAggregate` can be applied on any `TYPE` declaration, the annotation processor restricts its usage to annotate only `class` declarations.

The `value()` of the `@IterableAggregate` annotation specifies the class of *iterator* object it exposes.

It provides a child annotation - **`@IteratorFactory`**, which can be used to annotate a factory method that creates a new instance of an iterator for accessing elements of the parent iterable aggregate.

#### The `@Iterator` Annotation

The `@Iterator` annotation can be used to annotate an iterator which provides a way to sequentially extract elements from an underlying aggregate class. Although an `@Iterator` can be applied on any `TYPE` declaration, the annotation processor restricts its usage to annotate only `class` declarations.

The `value()` of the `@Iterator` annotation specifies the class of the object that can be extracted from this iterator on each iteration. The `treatWarningsAsErrors()` property denotes whether cases, which usually generate a warning, should raise errors instead or not.

It provides three child annotations:

1. **`@CurrentItem`:** Can be used to annotate a method that returns the current item in the current state of iteration.
2. **`@IsDone`:** Can be used to annotate a method that tells the consumer whether the iterator has finished iterating through all its elements.
3. **`@NextItem`:** Can be used to annotate a method that returns the next item in the current state of iteration.

### The Annotation Processor

The project provides two concrete annotation processors for processing the two main participants of the iterator design pattern. These annotation processors operate on Java source files at compile-time to process elements annotated with the annotations mentioned above.

#### The `AbstractAnnotationProcessor` class

This is the *abstract base class* for the other two concrete annotation processors. It *provides helper methods and convenience methods* that can be used by child annotation processors for performing common tasks and easily print logs at the required level.

The `AbstractAnnotationProcessor` provides a way of switching **debugging mode** on and off. If the annotation processor is passed the option **`AnnotationProcessor.debug`**, irrespective of its value, debug messages are also logged during the annotation processing.

The gradle build task for the `examples` project has been configured to pass this option to the annotation processor if the project property `debugAnnotationProcessor` is specified.

```
gradlew clean build -PdebugAnnotationProcessor
```

#### The `IterableAggregateAnnotationProcessor` class

This concrete annotation processor verifies the correct usage of `@IterableAggregate` annotation and its child annotation - `@IteratorFactory`.

It enforces the following rules during the processing:

1. Only classes can be annotated with `@IterableAggregate`.
2. `@IterableAggregate`'s `value()` property must be a class annotated with `@Iterator`.
3. `@IterableAggregate` must contain at least one method annotated with `@IteratorFactory`.
4. `@IteratorFactory` must be enclosed within a class annotated with `@IterableAggregate`.
5. The return type of a method annotated with `@IteratorFactory` must match the `value()` property of its parent class's `@IterableAggregate` annotation.

The **test suite** `IterableAggregateAnnotationProcessorTest` tests that the `IterableAggregateAnnotationProcessor` is able to catch violations of the above rules. Furthermore, it validates that the annotation processor doesn't produce any errors for a valid usage of the `@IterableAggregate` annotation. For each test case, a Java source file, present in the `src/test/resources` directory, is compiled and run through the `IterableAggregateAnnotationProcessor` during the compilation. **[Google Compile Testing](https://github.com/google/compile-testing)** library is used to *perform the compilation and perform assertions* based on the compilation results.

#### The `IteratorAnnotationProcessor` class

This concrete annotation processor verifies the correct usage of `@Iterator` annotation and its child annotations - `@CurrentItem`, `@IsDone`, and `@NextItem`.

It enforces the following rules during the processing:

1. Only classes can be annotated with `@Iterator`.
2. `@Iterator` can have only one method annotated with `@CurrentItem`.
3. `@Iterator` can have only one method annotated with `@IsDone`.
4. `@Iterator` can have only one method annotated with `@NextItem`.
5. `@CurrentItem` must be enclosed within a class annotated with `@Iterator`.
6. The return type of a method annotated with `@CurrentItem` must match the `value()` property of its parent class's `@Iterator` annotation.
7. A method annotated with `@CurrentItem` must not take any parameters.
8. `@IsDone` must be enclosed within a class annotated with `@Iterator`.
9. The return type of a method annotated with `@IsDone` must be a `boolean`.
10. A method annotated with `@IsDone` must not take any parameters.
11. A method annotated with `@IsDone` must generate a warning if it is `private`.
12. `@NextItem` must be enclosed within a class annotated with `@Iterator`.
13. The return type of a method annotated with `@NextItem` must match the `value()` property of its parent class's `@Iterator` annotation.
14. A method annotated with `@NextItem` must not take any parameters.
15. A method annotated with `@NextItem` must generate a warning if it is `private`.

The **test suite** `IteratorAnnotationProcessorTest` tests that the `IteratorAnnotationProcessor` is able to catch violations of the above rules. Furthermore, it validates that the annotation processor doesn't produce any errors for a valid usage of the `@Iterator` annotation. For each test case, a Java source file, present in the `src/test/resources` directory, is compiled and run through the `IteratorAnnotationProcessor` during the compilation. **[Google Compile Testing](https://github.com/google/compile-testing)** library is used to *perform the compilation and perform assertions* based on the compilation results.

### The Example Implementations

This project provides 3 classes that demonstrate the use of the iterator pattern and are annotated with different variations of the `@Iterator` and `@IterableAggregate` (and their nested) annotations:

1. **`StudentCollection`:** A class annotated with `@IterableAggregate` that implements `java.lang.Iterable`. It's iterator class is annotated with `@Iterator` and implements `java.util.Iterator`. This allows the `Student`s in this collection to be iterated using Java's *"enhanced for-loop"*. The iterator returns a `Student` object during iteration.
2. **`RangeGenerator`:** A class annotated with `@Iterator` that generates a primitive `int` value on each iteration within the range specified while constructing its object.
3. **`Tree`:** A class annotated with `@IterableAggregate` that provides **two methods** annotated with `@IteratorFactory`. The two methods return instances of `Tree.TreeIterator` that allow the user to iterate the nodes of the tree in **depth-first** and **breadth-first** orders.

### Output ###

#### Result of `gradlew clean build`

```text

> Task :annotation-processor:compileScala
Pruning sources from previous analysis, due to incompatible CompileSetup.

> Task :annotation-processor:compileTestScala
Pruning sources from previous analysis, due to incompatible CompileSetup.

> Task :annotation-processor:test

com.mayankrastogi.cs474.hw2.annotations.processor.tests.IterableAggregateAnnotationProcessorTest

  Test IterableAggregateAnnotationProcessor should succeed without warnings on valid usage. PASSED
  Test @IterableAggregate must not be applicable on an interface. PASSED
  Test @IterableAggregate's value property must be a class annotated with @Iterator. PASSED
  Test @IterableAggregate must contain at least one method annotated with @IteratorFactory. PASSED
  Test @IteratorFactory must be enclosed within a class annotated with @IterableAggregate. PASSED
  Test Return type of a method annotated with @IteratorFactory must match the value property of its parent class's @IterableAggregate annotation. PASSED

com.mayankrastogi.cs474.hw2.annotations.processor.tests.IteratorAnnotationProcessorTest

  Test IteratorAnnotationProcessor should succeed without warnings on valid usage. PASSED
  Test @Iterator must not be applicable on an interface. PASSED
  Test @Iterator must contain only one method annotated with @CurrentItem. PASSED
  Test @Iterator must contain only one method annotated with @IsDone. PASSED
  Test @Iterator must contain only one method annotated with @NextItem. PASSED
  Test @CurrentItem must be enclosed within a class annotated with @Iterator. PASSED
  Test Return type of method annotated with @CurrentItem must match the value property of its parent class's @Iterator annotation. PASSED
  Test Method annotated with @CurrentItem must not take any parameters. PASSED
  Test @IsDone must be enclosed within a class annotated with @Iterator PASSED
  Test Return type of method annotated with @IsDone must be boolean. PASSED
  Test Method annotated with @IsDone must not take any parameters. PASSED
  Test Method annotated with @IsDone must generate a warning if it is private PASSED
  Test @NextItem must be enclosed within a class annotated with @Iterator PASSED
  Test Return type of method annotated with @NextItem must match the value property of its parent class's @Iterator annotation. PASSED
  Test Method annotated with @NextItem must not take any parameters. PASSED
  Test Method annotated with @NextItem must generate a warning if it is private PASSED

SUCCESS: Executed 22 tests in 4.3s


> Task :examples:compileJava
Note: Processing elements annotated with @com.mayankrastogi.cs474.hw2.annotations.IterableAggregate
Note: Processing elements annotated with @com.mayankrastogi.cs474.hw2.annotations.IterableAggregate.IteratorFactory
Note: Processing elements annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator
Note: Processing elements annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.CurrentItem
Note: Processing elements annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.IsDone
Note: Processing elements annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.NextItem
Note: Processing elements annotated with @com.mayankrastogi.cs474.hw2.annotations.IterableAggregate
Note: Processing elements annotated with @com.mayankrastogi.cs474.hw2.annotations.IterableAggregate.IteratorFactory
Note: Processing elements annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator
Note: Processing elements annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.CurrentItem
Note: Processing elements annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.IsDone
Note: Processing elements annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.NextItem

> Task :examples:compileScala
Pruning sources from previous analysis, due to incompatible CompileSetup.

BUILD SUCCESSFUL in 13s
17 actionable tasks: 17 executed

```

#### Result of `gradlew clean build -PdebugAnnotationProcessor` (Debug mode ON)

```text

> Configure project :examples
Debugging mode for annotation processor has been switched on.

> Task :annotation-processor:compileScala
Pruning sources from previous analysis, due to incompatible CompileSetup.

> Task :annotation-processor:compileTestScala
Pruning sources from previous analysis, due to incompatible CompileSetup.

> Task :annotation-processor:test

com.mayankrastogi.cs474.hw2.annotations.processor.tests.IterableAggregateAnnotationProcessorTest

  Test IterableAggregateAnnotationProcessor should succeed without warnings on valid usage. PASSED
  Test @IterableAggregate must not be applicable on an interface. PASSED
  Test @IterableAggregate's value property must be a class annotated with @Iterator. PASSED
  Test @IterableAggregate must contain at least one method annotated with @IteratorFactory. PASSED
  Test @IteratorFactory must be enclosed within a class annotated with @IterableAggregate. PASSED
  Test Return type of a method annotated with @IteratorFactory must match the value property of its parent class's @IterableAggregate annotation. PASSED

com.mayankrastogi.cs474.hw2.annotations.processor.tests.IteratorAnnotationProcessorTest

  Test IteratorAnnotationProcessor should succeed without warnings on valid usage. PASSED
  Test @Iterator must not be applicable on an interface. PASSED
  Test @Iterator must contain only one method annotated with @CurrentItem. PASSED
  Test @Iterator must contain only one method annotated with @IsDone. PASSED
  Test @Iterator must contain only one method annotated with @NextItem. PASSED
  Test @CurrentItem must be enclosed within a class annotated with @Iterator. PASSED
  Test Return type of method annotated with @CurrentItem must match the value property of its parent class's @Iterator annotation. PASSED
  Test Method annotated with @CurrentItem must not take any parameters. PASSED
  Test @IsDone must be enclosed within a class annotated with @Iterator PASSED
  Test Return type of method annotated with @IsDone must be boolean. PASSED
  Test Method annotated with @IsDone must not take any parameters. PASSED
  Test Method annotated with @IsDone must generate a warning if it is private PASSED
  Test @NextItem must be enclosed within a class annotated with @Iterator PASSED
  Test Return type of method annotated with @NextItem must match the value property of its parent class's @Iterator annotation. PASSED
  Test Method annotated with @NextItem must not take any parameters. PASSED
  Test Method annotated with @NextItem must generate a warning if it is private PASSED

SUCCESS: Executed 22 tests in 4.5s


> Task :examples:compileJava
Note: [DEBUG]process(annotations: [com.mayankrastogi.cs474.hw2.annotations.IterableAggregate, com.mayankrastogi.cs474.hw2.annotations.IterableAggregate.IteratorFactory], roundEnv: [errorRaised=false, rootElements=[com.mayankrastogi.cs474.hw2.examples.RangeGenerator, com.mayankrastogi.cs474.hw2.examples.Degree, com.mayankrastogi.cs474.hw2.examples.StudentCollection, com.mayankrastogi.cs474.hw2.examples.Student, com.mayankrastogi.cs474.hw2.examples.Tree, com.mayankrastogi.cs474.hw2.examples.Node], processingOver=false])
Note: [DEBUG]iterableAggregates: Set(com.mayankrastogi.cs474.hw2.examples.StudentCollection, com.mayankrastogi.cs474.hw2.examples.Tree)
Note: [DEBUG]iteratorFactories: Set(iterator(), dfsIterator(), bfsIterator())
Note: Processing elements annotated with @com.mayankrastogi.cs474.hw2.annotations.IterableAggregate
examples\src\main\java\com\mayankrastogi\cs474\hw2\examples\StudentCollection.java:25: Note: [DEBUG]Processing iterableAggregateElement
public class StudentCollection implements Iterable<Student> {
       ^
Note: [DEBUG]assertIterableAggregateElementIsAppliedOnClass...
Note: [DEBUG]assertIterableAggregateElementIsAppliedOnClass: true
Note: [DEBUG]assertIterableAggregateAnnotationValueIsAnnotatedWithIterator...
Note: [DEBUG]assertIterableAggregateAnnotationValueIsAnnotatedWithIterator: true
Note: [DEBUG]assertIterableAggregateElementContainsAtLeastOneIteratorFactory...
Note: [DEBUG]Found 1 element(s) annotated with @com.mayankrastogi.cs474.hw2.annotations.IterableAggregate.IteratorFactory within iterable aggregate
Note: [DEBUG]Processing successful: true
examples\src\main\java\com\mayankrastogi\cs474\hw2\examples\Tree.java:20: Note: [DEBUG]Processing iterableAggregateElement
public class Tree<T> {
       ^
Note: [DEBUG]assertIterableAggregateElementIsAppliedOnClass...
Note: [DEBUG]assertIterableAggregateElementIsAppliedOnClass: true
Note: [DEBUG]assertIterableAggregateAnnotationValueIsAnnotatedWithIterator...
Note: [DEBUG]assertIterableAggregateAnnotationValueIsAnnotatedWithIterator: true
Note: [DEBUG]assertIterableAggregateElementContainsAtLeastOneIteratorFactory...
Note: [DEBUG]Found 2 element(s) annotated with @com.mayankrastogi.cs474.hw2.annotations.IterableAggregate.IteratorFactory within iterable aggregate
Note: [DEBUG]Processing successful: true
Note: Processing elements annotated with @com.mayankrastogi.cs474.hw2.annotations.IterableAggregate.IteratorFactory
examples\src\main\java\com\mayankrastogi\cs474\hw2\examples\StudentCollection.java:59: Note: [DEBUG]Processing iteratorFactoryMethod
    public java.util.Iterator<Student> iterator() {
                                       ^
Note: [DEBUG]assertIteratorFactoryMethodIsEnclosedWithinIterableAggregate...
Note: [DEBUG]Iterable factory method is enclosed within an iterable aggregate
Note: [DEBUG]assertIteratorFactoryMethodReturnsTypeSpecifiedInIterableAggregateAnnotationValue...
Note: [DEBUG]iterableAggregateAnnotationValue: com.mayankrastogi.cs474.hw2.examples.StudentCollection.StudentCollectionIterator
Note: [DEBUG]The return type of the iterator factory method is assignable to the type specified as value on its enclosing @com.mayankrastogi.cs474.hw2.annotations.IterableAggregate
Note: [DEBUG]Processing successful: true
examples\src\main\java\com\mayankrastogi\cs474\hw2\examples\Tree.java:42: Note: [DEBUG]Processing iteratorFactoryMethod
    public TreeIterator dfsIterator() {
                        ^
Note: [DEBUG]assertIteratorFactoryMethodIsEnclosedWithinIterableAggregate...
Note: [DEBUG]Iterable factory method is enclosed within an iterable aggregate
Note: [DEBUG]assertIteratorFactoryMethodReturnsTypeSpecifiedInIterableAggregateAnnotationValue...
Note: [DEBUG]iterableAggregateAnnotationValue: com.mayankrastogi.cs474.hw2.examples.Tree.TreeIterator
Note: [DEBUG]The return type of the iterator factory method is assignable to the type specified as value on its enclosing @com.mayankrastogi.cs474.hw2.annotations.IterableAggregate
Note: [DEBUG]Processing successful: true
examples\src\main\java\com\mayankrastogi\cs474\hw2\examples\Tree.java:52: Note: [DEBUG]Processing iteratorFactoryMethod
    public TreeIterator bfsIterator() {
                        ^
Note: [DEBUG]assertIteratorFactoryMethodIsEnclosedWithinIterableAggregate...
Note: [DEBUG]Iterable factory method is enclosed within an iterable aggregate
Note: [DEBUG]assertIteratorFactoryMethodReturnsTypeSpecifiedInIterableAggregateAnnotationValue...
Note: [DEBUG]iterableAggregateAnnotationValue: com.mayankrastogi.cs474.hw2.examples.Tree.TreeIterator
Note: [DEBUG]The return type of the iterator factory method is assignable to the type specified as value on its enclosing @com.mayankrastogi.cs474.hw2.annotations.IterableAggregate
Note: [DEBUG]Processing successful: true
Note: [DEBUG]process(annotations: [com.mayankrastogi.cs474.hw2.annotations.Iterator.CurrentItem, com.mayankrastogi.cs474.hw2.annotations.Iterator, com.mayankrastogi.cs474.hw2.annotations.Iterator.NextItem, com.mayankrastogi.cs474.hw2.annotations.Iterator.IsDone], roundEnv: [errorRaised=false, rootElements=[com.mayankrastogi.cs474.hw2.examples.RangeGenerator, com.mayankrastogi.cs474.hw2.examples.Degree, com.mayankrastogi.cs474.hw2.examples.StudentCollection, com.mayankrastogi.cs474.hw2.examples.Student, com.mayankrastogi.cs474.hw2.examples.Tree, com.mayankrastogi.cs474.hw2.examples.Node], processingOver=false])
Note: [DEBUG]iterators: Set(com.mayankrastogi.cs474.hw2.examples.RangeGenerator, com.mayankrastogi.cs474.hw2.examples.StudentCollection.StudentCollectionIterator, com.mayankrastogi.cs474.hw2.examples.Tree.TreeIterator)
Note: [DEBUG]currentItems: Set(currentValue(), current(), currentNode())
Note: [DEBUG]isDones: Set(isDone(), hasNext(), isDone())
Note: [DEBUG]nextItems: Set(next(), next(), next())
Note: Processing elements annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator
examples\src\main\java\com\mayankrastogi\cs474\hw2\examples\RangeGenerator.java:11: Note: [DEBUG]Processing iteratorElement
public class RangeGenerator {
       ^
Note: [DEBUG]assertIteratorElementIsValid...
Note: [DEBUG]assertIteratorElementIsValid: true
Note: [DEBUG]assertIteratorElementContainsOnlyOneMethodAnnotatedWith...
Note: [DEBUG]Found 1 element(s) annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.CurrentItem within iterator
Note: [DEBUG]assertIteratorElementContainsOnlyOneMethodAnnotatedWith: true
Note: [DEBUG]assertIteratorElementContainsOnlyOneMethodAnnotatedWith...
Note: [DEBUG]Found 1 element(s) annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.IsDone within iterator
Note: [DEBUG]assertIteratorElementContainsOnlyOneMethodAnnotatedWith: true
Note: [DEBUG]assertIteratorElementContainsOnlyOneMethodAnnotatedWith...
Note: [DEBUG]Found 1 element(s) annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.NextItem within iterator
Note: [DEBUG]assertIteratorElementContainsOnlyOneMethodAnnotatedWith: true
Note: [DEBUG]Processing successful: true
examples\src\main\java\com\mayankrastogi\cs474\hw2\examples\StudentCollection.java:70: Note: [DEBUG]Processing iteratorElement
    public class StudentCollectionIterator implements java.util.Iterator<Student> {
           ^
Note: [DEBUG]assertIteratorElementIsValid...
Note: [DEBUG]assertIteratorElementIsValid: true
Note: [DEBUG]assertIteratorElementContainsOnlyOneMethodAnnotatedWith...
Note: [DEBUG]Found 1 element(s) annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.CurrentItem within iterator
Note: [DEBUG]assertIteratorElementContainsOnlyOneMethodAnnotatedWith: true
Note: [DEBUG]assertIteratorElementContainsOnlyOneMethodAnnotatedWith...
Note: [DEBUG]Found 1 element(s) annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.IsDone within iterator
Note: [DEBUG]assertIteratorElementContainsOnlyOneMethodAnnotatedWith: true
Note: [DEBUG]assertIteratorElementContainsOnlyOneMethodAnnotatedWith...
Note: [DEBUG]Found 1 element(s) annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.NextItem within iterator
Note: [DEBUG]assertIteratorElementContainsOnlyOneMethodAnnotatedWith: true
Note: [DEBUG]Processing successful: true
examples\src\main\java\com\mayankrastogi\cs474\hw2\examples\Tree.java:65: Note: [DEBUG]Processing iteratorElement
    public class TreeIterator {
           ^
Note: [DEBUG]assertIteratorElementIsValid...
Note: [DEBUG]assertIteratorElementIsValid: true
Note: [DEBUG]assertIteratorElementContainsOnlyOneMethodAnnotatedWith...
Note: [DEBUG]Found 1 element(s) annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.CurrentItem within iterator
Note: [DEBUG]assertIteratorElementContainsOnlyOneMethodAnnotatedWith: true
Note: [DEBUG]assertIteratorElementContainsOnlyOneMethodAnnotatedWith...
Note: [DEBUG]Found 1 element(s) annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.IsDone within iterator
Note: [DEBUG]assertIteratorElementContainsOnlyOneMethodAnnotatedWith: true
Note: [DEBUG]assertIteratorElementContainsOnlyOneMethodAnnotatedWith...
Note: [DEBUG]Found 1 element(s) annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.NextItem within iterator
Note: [DEBUG]assertIteratorElementContainsOnlyOneMethodAnnotatedWith: true
Note: [DEBUG]Processing successful: true
Note: Processing elements annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.CurrentItem
examples\src\main\java\com\mayankrastogi\cs474\hw2\examples\RangeGenerator.java:28: Note: [DEBUG]Processing currentItemMethod
    private int currentValue() {
                ^
Note: [DEBUG]assertMethodIsEnclosedWithinIterator...
Note: [DEBUG]CurrentItem is enclosed within an iterator
Note: [DEBUG]assertMethodReturnsTypeSpecifiedInIteratorAnnotationValue...
Note: [DEBUG]iteratorAnnotationValue: int
Note: [DEBUG]The return type of the method annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.CurrentItem is assignable to the type specified as value on its enclosing @com.mayankrastogi.cs474.hw2.annotations.Iterator
Note: [DEBUG]assertMethodTakesNoParameters...
Note: [DEBUG]assertMethodTakesNoParameters: true
Note: [DEBUG]Processing successful: true
examples\src\main\java\com\mayankrastogi\cs474\hw2\examples\StudentCollection.java:85: Note: [DEBUG]Processing currentItemMethod
        public Student current() {
                       ^
Note: [DEBUG]assertMethodIsEnclosedWithinIterator...
Note: [DEBUG]CurrentItem is enclosed within an iterator
Note: [DEBUG]assertMethodReturnsTypeSpecifiedInIteratorAnnotationValue...
Note: [DEBUG]iteratorAnnotationValue: com.mayankrastogi.cs474.hw2.examples.Student
Note: [DEBUG]The return type of the method annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.CurrentItem is assignable to the type specified as value on its enclosing @com.mayankrastogi.cs474.hw2.annotations.Iterator
Note: [DEBUG]assertMethodTakesNoParameters...
Note: [DEBUG]assertMethodTakesNoParameters: true
Note: [DEBUG]Processing successful: true
examples\src\main\java\com\mayankrastogi\cs474\hw2\examples\Tree.java:90: Note: [DEBUG]Processing currentItemMethod
        public Node<T> currentNode() {
                       ^
Note: [DEBUG]assertMethodIsEnclosedWithinIterator...
Note: [DEBUG]CurrentItem is enclosed within an iterator
Note: [DEBUG]assertMethodReturnsTypeSpecifiedInIteratorAnnotationValue...
Note: [DEBUG]iteratorAnnotationValue: com.mayankrastogi.cs474.hw2.examples.Node
Note: [DEBUG]The return type of the method annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.CurrentItem is assignable to the type specified as value on its enclosing @com.mayankrastogi.cs474.hw2.annotations.Iterator
Note: [DEBUG]assertMethodTakesNoParameters...
Note: [DEBUG]assertMethodTakesNoParameters: true
Note: [DEBUG]Processing successful: true
Note: Processing elements annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.IsDone
examples\src\main\java\com\mayankrastogi\cs474\hw2\examples\RangeGenerator.java:38: Note: [DEBUG]Processing isDoneMethod
    public boolean isDone() {
                   ^
Note: [DEBUG]assertMethodIsEnclosedWithinIterator...
Note: [DEBUG]IsDone is enclosed within an iterator
Note: [DEBUG]assertIsDoneMethodReturnsBooleanType...
Note: [DEBUG]assertIsDoneMethodReturnsBooleanType: true
Note: [DEBUG]assertMethodTakesNoParameters...
Note: [DEBUG]assertMethodTakesNoParameters: true
Note: [DEBUG]warnIfMethodIsPrivate...
Note: [DEBUG]shouldTreatWarningsAsErrors...
Note: [DEBUG]shouldTreatWarningsAsErrors: false
Note: [DEBUG]isPrivate: false
Note: [DEBUG]operationSuccessful: true
Note: [DEBUG]Processing successful: true
examples\src\main\java\com\mayankrastogi\cs474\hw2\examples\StudentCollection.java:96: Note: [DEBUG]Processing isDoneMethod
        public boolean hasNext() {
                       ^
Note: [DEBUG]assertMethodIsEnclosedWithinIterator...
Note: [DEBUG]IsDone is enclosed within an iterator
Note: [DEBUG]assertIsDoneMethodReturnsBooleanType...
Note: [DEBUG]assertIsDoneMethodReturnsBooleanType: true
Note: [DEBUG]assertMethodTakesNoParameters...
Note: [DEBUG]assertMethodTakesNoParameters: true
Note: [DEBUG]warnIfMethodIsPrivate...
Note: [DEBUG]shouldTreatWarningsAsErrors...
Note: [DEBUG]shouldTreatWarningsAsErrors: false
Note: [DEBUG]isPrivate: false
Note: [DEBUG]operationSuccessful: true
Note: [DEBUG]Processing successful: true
examples\src\main\java\com\mayankrastogi\cs474\hw2\examples\Tree.java:100: Note: [DEBUG]Processing isDoneMethod
        public boolean isDone() {
                       ^
Note: [DEBUG]assertMethodIsEnclosedWithinIterator...
Note: [DEBUG]IsDone is enclosed within an iterator
Note: [DEBUG]assertIsDoneMethodReturnsBooleanType...
Note: [DEBUG]assertIsDoneMethodReturnsBooleanType: true
Note: [DEBUG]assertMethodTakesNoParameters...
Note: [DEBUG]assertMethodTakesNoParameters: true
Note: [DEBUG]warnIfMethodIsPrivate...
Note: [DEBUG]shouldTreatWarningsAsErrors...
Note: [DEBUG]shouldTreatWarningsAsErrors: false
Note: [DEBUG]isPrivate: false
Note: [DEBUG]operationSuccessful: true
Note: [DEBUG]Processing successful: true
Note: Processing elements annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.NextItem
examples\src\main\java\com\mayankrastogi\cs474\hw2\examples\RangeGenerator.java:50: Note: [DEBUG]Processing nextItemMethod
    public int next() {
               ^
Note: [DEBUG]assertMethodIsEnclosedWithinIterator...
Note: [DEBUG]NextItem is enclosed within an iterator
Note: [DEBUG]assertMethodReturnsTypeSpecifiedInIteratorAnnotationValue...
Note: [DEBUG]iteratorAnnotationValue: int
Note: [DEBUG]The return type of the method annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.NextItem is assignable to the type specified as value on its enclosing @com.mayankrastogi.cs474.hw2.annotations.Iterator
Note: [DEBUG]assertMethodTakesNoParameters...
Note: [DEBUG]assertMethodTakesNoParameters: true
Note: [DEBUG]warnIfMethodIsPrivate...
Note: [DEBUG]shouldTreatWarningsAsErrors...
Note: [DEBUG]shouldTreatWarningsAsErrors: false
Note: [DEBUG]isPrivate: false
Note: [DEBUG]operationSuccessful: true
Note: [DEBUG]Processing successful: true
examples\src\main\java\com\mayankrastogi\cs474\hw2\examples\StudentCollection.java:110: Note: [DEBUG]Processing nextItemMethod
        public Student next() {
                       ^
Note: [DEBUG]assertMethodIsEnclosedWithinIterator...
Note: [DEBUG]NextItem is enclosed within an iterator
Note: [DEBUG]assertMethodReturnsTypeSpecifiedInIteratorAnnotationValue...
Note: [DEBUG]iteratorAnnotationValue: com.mayankrastogi.cs474.hw2.examples.Student
Note: [DEBUG]The return type of the method annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.NextItem is assignable to the type specified as value on its enclosing @com.mayankrastogi.cs474.hw2.annotations.Iterator
Note: [DEBUG]assertMethodTakesNoParameters...
Note: [DEBUG]assertMethodTakesNoParameters: true
Note: [DEBUG]warnIfMethodIsPrivate...
Note: [DEBUG]shouldTreatWarningsAsErrors...
Note: [DEBUG]shouldTreatWarningsAsErrors: false
Note: [DEBUG]isPrivate: false
Note: [DEBUG]operationSuccessful: true
Note: [DEBUG]Processing successful: true
examples\src\main\java\com\mayankrastogi\cs474\hw2\examples\Tree.java:113: Note: [DEBUG]Processing nextItemMethod
        public Node<T> next() {
                       ^
Note: [DEBUG]assertMethodIsEnclosedWithinIterator...
Note: [DEBUG]NextItem is enclosed within an iterator
Note: [DEBUG]assertMethodReturnsTypeSpecifiedInIteratorAnnotationValue...
Note: [DEBUG]iteratorAnnotationValue: com.mayankrastogi.cs474.hw2.examples.Node
Note: [DEBUG]The return type of the method annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.NextItem is assignable to the type specified as value on its enclosing @com.mayankrastogi.cs474.hw2.annotations.Iterator
Note: [DEBUG]assertMethodTakesNoParameters...
Note: [DEBUG]assertMethodTakesNoParameters: true
Note: [DEBUG]warnIfMethodIsPrivate...
Note: [DEBUG]shouldTreatWarningsAsErrors...
Note: [DEBUG]shouldTreatWarningsAsErrors: false
Note: [DEBUG]isPrivate: false
Note: [DEBUG]operationSuccessful: true
Note: [DEBUG]Processing successful: true
Note: [DEBUG]process(annotations: [], roundEnv: [errorRaised=false, rootElements=[], processingOver=true])
Note: [DEBUG]iterableAggregates: Set()
Note: [DEBUG]iteratorFactories: Set()
Note: Processing elements annotated with @com.mayankrastogi.cs474.hw2.annotations.IterableAggregate
Note: Processing elements annotated with @com.mayankrastogi.cs474.hw2.annotations.IterableAggregate.IteratorFactory
Note: [DEBUG]process(annotations: [], roundEnv: [errorRaised=false, rootElements=[], processingOver=true])
Note: [DEBUG]iterators: Set()
Note: [DEBUG]currentItems: Set()
Note: [DEBUG]isDones: Set()
Note: [DEBUG]nextItems: Set()
Note: Processing elements annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator
Note: Processing elements annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.CurrentItem
Note: Processing elements annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.IsDone
Note: Processing elements annotated with @com.mayankrastogi.cs474.hw2.annotations.Iterator.NextItem

> Task :examples:compileScala
Pruning sources from previous analysis, due to incompatible CompileSetup.

BUILD SUCCESSFUL in 13s
17 actionable tasks: 17 executed

```

#### Result of `gradlew run`

```text

> Task :examples:run

=================================================================================================================
                 Design Pattern Verifier - Iterator Design Pattern Example Programs
=================================================================================================================
The iterator design pattern provides a way to access the elements of an aggregate object sequentially without
exposing its underlying representation.

This application demonstrates 3 classes that use the iterator pattern and are annotated with different variations
of the @Iterator and @IterableAggregate (and their nested) annotations:

  1. StudentCollection: A class annotated with @IterableAggregate that implements `java.lang.Iterable`. It's
       iterator class is annotated with @Iterator and implements `java.util.Iterator`. The iterator returns a
       `Student` object during iteration.
  2. RangeGenerator: A class annotated with @Iterator that generates a primitive `int` value on each iteration
       within the range specified while constructing its object.
  3. Tree: A class annotated with @IterableAggregate that provides two methods annotated with @IteratorFactory.
       The two methods return instances of `Tree.TreeIterator` that allow the user to iterate the nodes of the
       tree in depth-first and breadth-first orders.


-----------------------------------------------------------------------------------------------------------------
Student Collection Iteration Example
-----------------------------------------------------------------------------------------------------------------

23:13:35.800 [main] DEBUG com.mayankrastogi.cs474.hw2.examples.IteratorExamplesMain$ - Creating StudentCollection...
23:13:35.803 [main] DEBUG com.mayankrastogi.cs474.hw2.examples.IteratorExamplesMain$ - Iterating StudentCollection...

Name:   Alex
Age:    20
Degree: Bachelors
GPA:    3.2


Name:   Bob
Age:    25
Degree: Masters
GPA:    3.6


Name:   Chuck
Age:    28
Degree: PhD
GPA:    3.5

23:13:35.815 [main] DEBUG com.mayankrastogi.cs474.hw2.examples.IteratorExamplesMain$ - StudentCollection Iteration Example Finished.

-----------------------------------------------------------------------------------------------------------------
RangeGenerator Iteration Example
-----------------------------------------------------------------------------------------------------------------

23:13:35.823 [main] DEBUG com.mayankrastogi.cs474.hw2.examples.IteratorExamplesMain$ - Creating a RangeGenerator(from: 1, to: 10)...
23:13:35.823 [main] DEBUG com.mayankrastogi.cs474.hw2.examples.IteratorExamplesMain$ - Iterating RangeGenerator...
1
2
3
4
5
6
7
8
9
10
23:13:35.823 [main] DEBUG com.mayankrastogi.cs474.hw2.examples.IteratorExamplesMain$ - RangeGenerator Iteration Example Finished.

-----------------------------------------------------------------------------------------------------------------
Tree Iteration Example
-----------------------------------------------------------------------------------------------------------------


Sample Tree:
-----------

                                               1
                _______________________________|_______________________________
               |                               |                               |
               2                               3                               4
        _______|_______                 _______|_______                 _______|_______
       |               |               |               |               |               |
       5               6               7               8               9              10

23:13:35.824 [main] DEBUG com.mayankrastogi.cs474.hw2.examples.IteratorExamplesMain$ - Creating 10 nodes...
23:13:35.828 [main] DEBUG com.mayankrastogi.cs474.hw2.examples.IteratorExamplesMain$ - allNodes: Vector(Node(data: 1, children: []), Node(data: 2, children: []), Node(data: 3, children: []), Node(data: 4, children: []), Node(data: 5, children: []), Node(data: 6, children: []), Node(data: 7, children: []), Node(data: 8, children: []), Node(data: 9, children: []), Node(data: 10, children: []))
23:13:35.828 [main] DEBUG com.mayankrastogi.cs474.hw2.examples.IteratorExamplesMain$ - Creating Sample Tree...
23:13:35.841 [main] DEBUG com.mayankrastogi.cs474.hw2.examples.IteratorExamplesMain$ - tree: Tree(root: Node(data: 1, children: [Node(data: 2, children: [Node(data: 5, children: []), Node(data: 6, children: [])]), Node(data: 3, children: [Node(data: 7, children: []), Node(data: 8, children: [])]), Node(data: 4, children: [Node(data: 9, children: []), Node(data: 10, children: [])])]))
23:13:35.841 [main] DEBUG com.mayankrastogi.cs474.hw2.examples.IteratorExamplesMain$ - Iterating tree using `dfsIterator()`...
Depth-first tree traversal:
  1  2  5  6  3  7  8  4  9  10
23:13:35.842 [main] DEBUG com.mayankrastogi.cs474.hw2.examples.IteratorExamplesMain$ - Iterating tree using `bfsIterator()`...
Breadth-first tree traversal:
  1  2  3  4  5  6  7  8  9  10
23:13:35.843 [main] DEBUG com.mayankrastogi.cs474.hw2.examples.IteratorExamplesMain$ - Tree Iteration Example Finished.

BUILD SUCCESSFUL in 1s
8 actionable tasks: 1 executed, 7 up-to-date

```