package com.mayankrastogi.cs474.hw2.examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;

public class IteratorExamplesMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(IteratorExamplesMain.class);

    public static void main(String[] args) {
        System.out.println("\n" +
                "=======================================================================================================\n" +
                "                 Design Pattern Verifier - Iterator Design Pattern Example Programs\n" +
                "=======================================================================================================\n" +
                "The iterator design pattern \"provides a way to access the elements of an aggregate object sequentially\n" +
                "without exposing its underlying representation.\n" +
                "\n" +
                "This application demonstrates 3 classes that use the iterator pattern and are annotated with different\n" +
                "variations of the @Iterator and @IterableAggregate (and their nested) annotations:\n" +
                "\n" +
                "  1. StudentCollection: A class annotated with @IterableAggregate that implements `java.lang.Iterable`.\n" +
                "       It's iterator class is annotated with @Iterator and implements `java.util.Iterator`. The\n" +
                "       iterator returns a `Student` object during iteration.\n" +
                "  2. RangeGenerator: A class annotated with @Iterator that generates a primitive `int` value on each\n" +
                "       iteration within the range specified while constructing its object.\n" +
                "  3. Tree: A class annotated with @IterableAggregate that provides two methods annotated with\n" +
                "       @IteratorFactory. The two methods return instances of `Tree.TreeIterator` that allow the user to\n" +
                "       iterate the nodes of the tree in depth-first and breadth-first manners.\n" +
                "======================================================================================================="
        );

        printExampleName("Student Collection Iteration Example");
        runStudentCollectionExample();

        printExampleName("RangeGenerator Iteration Example");
        runRangeExample();

        printExampleName("Tree Iteration Example");
        runTreeExample();
    }

    private static void printExampleName(String exampleName) {
        System.out.println("\n\n" +
                "-------------------------------------------------------------------------------------------------------\n" +
                exampleName + "\n" +
                "-------------------------------------------------------------------------------------------------------\n" +
                "\n"
        );
    }

    private static void runStudentCollectionExample() {
        LOGGER.debug("Creating StudentCollection...");
        var students = new StudentCollection();

        students.addStudent(new Student("Alex", 20, Degree.Bachelors, 3.2f));
        students.addStudent(new Student("Bob", 25, Degree.Masters, 3.6f));
        students.addStudent(new Student("Chuck", 28, Degree.PhD, 3.5f));

        LOGGER.debug("Iterating StudentCollection...");
        for (var student : students) {
            System.out.println("Name: " + student.getName());
            System.out.println("Age: " + student.getAge());
            System.out.println("Degree: " + student.getDegree());
            System.out.println("GPA: " + student.getGPA());
            System.out.println();
        }
        LOGGER.debug("StudentCollection Iteration Example Finished.");
    }

    private static void runRangeExample() {
        int from = 1;
        int to = 10;

        LOGGER.debug(String.format("Creating a RangeGenerator(from: %s, to: %s)...", from, to));
        var range = new RangeGenerator(from, to);

        LOGGER.debug("Iterating RangeGenerator...");
        while (!range.isDone()) {
            System.out.println(range.next());
        }

        LOGGER.debug("RangeGenerator Iteration Example Finished.");
    }

    private static void runTreeExample() {
        System.out.println("" +
                "Sample Tree:\n" +
                "-----------\n" +
                "\n" +
                "                                               1\n" +
                "                _______________________________|_______________________________\n" +
                "               |                               |                               |\n" +
                "               2                               3                               4\n" +
                "        _______|_______                 _______|_______                 _______|_______\n" +
                "       |               |               |               |               |               |\n" +
                "       5               6               7               8               9              10\n" +
                "\n");

        LOGGER.debug("Creating 10 nodes using the RangeGenerator...");
        var allNodes = new ArrayList<Node<Integer>>();
        var range = new RangeGenerator(1, 10);
        while (!range.isDone()) {
            allNodes.add(new Node<>(range.next()));
        }
        LOGGER.debug("allNodes: " + allNodes);

        LOGGER.debug("Creating Sample Tree...");

        allNodes.get(0).children = Arrays.asList(allNodes.get(1), allNodes.get(2), allNodes.get(3));
        allNodes.get(1).children = Arrays.asList(allNodes.get(4), allNodes.get(5));
        allNodes.get(2).children = Arrays.asList(allNodes.get(6), allNodes.get(7));
        allNodes.get(3).children = Arrays.asList(allNodes.get(8), allNodes.get(9));

        var tree = new Tree<>(allNodes.get(0));
        LOGGER.debug("tree: " + tree);

        LOGGER.debug("Iterating tree using `dfsIterator()`...");

        System.out.println("Depth-first tree traversal:");
        var iterator = tree.dfsIterator();
        while (!iterator.isDone()) {
            System.out.print("  " + iterator.next().data);
        }
        System.out.println();

        LOGGER.debug("Iterating tree using `bfsIterator()`...");

        System.out.println("Breadth-first tree traversal:");
        iterator = tree.bfsIterator();
        while (!iterator.isDone()) {
            System.out.print("  " + iterator.next().data);
        }
        System.out.println();

        LOGGER.debug("Tree Iteration Example Finished.");
    }
}
