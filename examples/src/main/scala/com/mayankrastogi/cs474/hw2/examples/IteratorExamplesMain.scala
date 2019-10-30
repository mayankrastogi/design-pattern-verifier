package com.mayankrastogi.cs474.hw2.examples

import com.typesafe.scalalogging.LazyLogging

import scala.jdk.CollectionConverters._

object IteratorExamplesMain extends App with LazyLogging {

  println(
    """
      |=================================================================================================================
      |                 Design Pattern Verifier - Iterator Design Pattern Example Programs
      |=================================================================================================================
      |The iterator design pattern provides a way to access the elements of an aggregate object sequentially without
      |exposing its underlying representation.
      |
      |This application demonstrates 3 classes that use the iterator pattern and are annotated with different variations
      |of the @Iterator and @IterableAggregate (and their nested) annotations:
      |  1. StudentCollection: A class annotated with @IterableAggregate that implements `java.lang.Iterable`. It's
      |       iterator class is annotated with @Iterator and implements `java.util.Iterator`. The iterator returns a
      |       `Student` object during iteration.
      |  2. RangeGenerator: A class annotated with @Iterator that generates a primitive `int` value on each iteration
      |       within the range specified while constructing its object.
      |  3. Tree: A class annotated with @IterableAggregate that provides two methods annotated with @IteratorFactory.
      |       The two methods return instances of `Tree.TreeIterator` that allow the user to iterate the nodes of the
      |       tree in depth-first and breadth-first manners.
      |""".stripMargin)

  printExampleName("Student Collection Iteration Example")
  runStudentCollectionExample()

  printExampleName("RangeGenerator Iteration Example")
  runRangeExample()

  printExampleName("Tree Iteration Example")
  runTreeExample()

  private def printExampleName(exampleName: String): Unit = {
    println(
      s"""
         |---------------------------------------------------------------------------------------------------------------
         |$exampleName
         |---------------------------------------------------------------------------------------------------------------
         |""".stripMargin)
  }

  private def runStudentCollectionExample(): Unit = {
    logger.debug("Creating StudentCollection...")

    val students = new StudentCollection

    students.addStudent(new Student("Alex", 20, Degree.Bachelors, 3.2f))
    students.addStudent(new Student("Bob", 25, Degree.Masters, 3.6f))
    students.addStudent(new Student("Chuck", 28, Degree.PhD, 3.5f))

    logger.debug("Iterating StudentCollection...")

    // StudentCollection implements java.lang.Iterable => can be converted to Scala Iterable for scala-style iteration
    students.asScala.foreach { student =>
      println(
        s"""
           |Name:   ${student.getName}
           |Age:    ${student.getAge}
           |Degree: ${student.getDegree}
           |GPA:    ${student.getGPA}
           |""".stripMargin)
    }
    logger.debug("StudentCollection Iteration Example Finished.")
  }

  private def runRangeExample(): Unit = {
    val from = 1
    val to = 10

    logger.debug(String.format("Creating a RangeGenerator(from: %s, to: %s)...", from, to))
    val range = new RangeGenerator(from, to)

    logger.debug("Iterating RangeGenerator...")
    while (!range.isDone)
      println(range.next)

    logger.debug("RangeGenerator Iteration Example Finished.")
  }

  private def runTreeExample(): Unit = {
    println(
      """
        |Sample Tree:
        |-----------
        |
        |                                               1
        |                _______________________________|_______________________________
        |               |                               |                               |
        |               2                               3                               4
        |        _______|_______                 _______|_______                 _______|_______
        |       |               |               |               |               |               |
        |       5               6               7               8               9              10
        |""".stripMargin)

    logger.debug("Creating 10 nodes using the RangeGenerator...")

    val allNodes = (1 to 10).map(new Node[Integer](_))
    logger.debug("allNodes: " + allNodes)

    logger.debug("Creating Sample Tree...")

    allNodes(0).children = List(allNodes(1), allNodes(2), allNodes(3)).asJava
    allNodes(1).children = List(allNodes(4), allNodes(5)).asJava
    allNodes(2).children = List(allNodes(6), allNodes(7)).asJava
    allNodes(3).children = List(allNodes(8), allNodes(9)).asJava

    val tree = new Tree[Integer](allNodes(0))
    logger.debug("tree: " + tree)

    logger.debug("Iterating tree using `dfsIterator()`...")
    println("Depth-first tree traversal:")

    var iterator = tree.dfsIterator
    while (!iterator.isDone)
      print("  " + iterator.next.data)
    println()

    logger.debug("Iterating tree using `bfsIterator()`...")
    println("Breadth-first tree traversal:")

    iterator = tree.bfsIterator
    while (!iterator.isDone)
      print("  " + iterator.next.data)
    println()

    logger.debug("Tree Iteration Example Finished.")
  }
}
