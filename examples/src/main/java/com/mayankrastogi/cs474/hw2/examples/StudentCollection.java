package com.mayankrastogi.cs474.hw2.examples;

import com.mayankrastogi.cs474.hw2.annotations.IterableAggregate;
import com.mayankrastogi.cs474.hw2.annotations.Iterator;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * The degree a student is pursuing.
 */
enum Degree {
    Bachelors,
    Masters,
    PhD
}

/**
 * An iterable collection of {@link Student} objects.
 * <p>
 * Along with the @{@link IterableAggregate} annotation, this class also implements the {@link Iterable} interface,
 * which allows the {@link Student}s in this collection to be iterated using Java's "enhanced for-loop".
 */
@IterableAggregate(StudentCollection.StudentCollectionIterator.class)
public class StudentCollection implements Iterable<Student> {

    private ArrayList<Student> studentsList = new ArrayList<>();

    /**
     * Add a {@link Student} to the collection.
     *
     * @param student The student to be added.
     */
    public void addStudent(Student student) {
        studentsList.add(student);
    }

    /**
     * Remove a {@link Student} from the collection.
     *
     * @param student The student to be removed.
     */
    public void removeStudent(Student student) {
        studentsList.remove(student);
    }

    /**
     * Creates an iterator that can be used to iterate through the students in this collection.
     * <p>
     * Notice that the return type of this method is {@link java.util.Iterator} instead of
     * {@link StudentCollectionIterator} and the annotation processor still treats it as valid. This is because the
     * annotation processor checks if the class specified on @{@link IterableAggregate} is "assignable" to and not "same
     * as" the return type of the @{@link com.mayankrastogi.cs474.hw2.annotations.IterableAggregate.IteratorFactory}.
     *
     * @return The iterator.
     */
    @Override
    @IterableAggregate.IteratorFactory
    public java.util.Iterator<Student> iterator() {
        return new StudentCollectionIterator();
    }

    /**
     * An iterator for iterating {@link Student} objects in {@link StudentCollection}.
     * <p>
     * Along with the @{@link Iterator} annotation, this class also implements the {@link java.util.Iterator} interface,
     * which allows the it to be iterated using Java's "enhanced for-loop".
     */
    @Iterator(Student.class)
    public class StudentCollectionIterator implements java.util.Iterator<Student> {

        private int currentIndex;

        private StudentCollectionIterator() {
            currentIndex = -1;
        }

        /**
         * The current {@link Student} object in the current state of iteration.
         *
         * @return The current {@link Student} object; {@code null} if this method is called before a call to
         * {@link #hasNext()} has been made.
         */
        @Iterator.CurrentItem
        public Student current() {
            return currentIndex < 0 ? null : studentsList.get(currentIndex);
        }

        /**
         * Tells whether more {@link Student} objects are available for iteration.
         *
         * @return {@code true} if more students are available, {@code false} otherwise.
         */
        @Override
        @Iterator.IsDone
        public boolean hasNext() {
            return currentIndex < studentsList.size() - 1;
        }

        /**
         * The next {@link Student} object in the current state of iteration.
         * <p>
         * This method throws a {@link NoSuchElementException} if the iterator has finished iterating all the students
         * in the collection.
         *
         * @return The next {@link Student} object.
         */
        @Override
        @Iterator.NextItem
        public Student next() {
            if (hasNext()) {
                currentIndex++;
                return current();
            } else {
                throw new NoSuchElementException("StudentCollectionIterator has finished iterating over all the" +
                        " students in the collection");
            }
        }
    }
}

/**
 * Models a student in the {@link StudentCollection}.
 */
class Student {

    private String name;
    private int age;
    private Degree degree;
    private float gpa;

    public Student(String name, int age, Degree degree, float gpa) {
        this.name = name;
        this.age = age;
        this.degree = degree;
        this.gpa = gpa;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public Degree getDegree() {
        return degree;
    }

    public float getGPA() {
        return gpa;
    }
}