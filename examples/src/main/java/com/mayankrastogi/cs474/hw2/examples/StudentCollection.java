package com.mayankrastogi.cs474.hw2.examples;

import com.mayankrastogi.cs474.hw2.annotations.IterableAggregate;
import com.mayankrastogi.cs474.hw2.annotations.Iterator;

import java.util.ArrayList;
import java.util.NoSuchElementException;

enum Degree {
    Bachelors,
    Masters,
    PhD
}

@IterableAggregate(StudentCollection.StudentCollectionIterator.class)
public class StudentCollection implements Iterable<Student> {

    private ArrayList<Student> studentsList = new ArrayList<>();

    public void addStudent(Student student) {
        studentsList.add(student);
    }

    public void removeStudent(Student student) {
        studentsList.remove(student);
    }

    @Override
    @IterableAggregate.IteratorFactory
    public java.util.Iterator<Student> iterator() {
        return new StudentCollectionIterator();
    }

    @Iterator(Student.class)
    public class StudentCollectionIterator implements java.util.Iterator<Student> {

        private int currentIndex;

        private StudentCollectionIterator() {
            currentIndex = -1;
        }

        @Iterator.CurrentItem
        public Student current() {
            return currentIndex < 0 ? null : studentsList.get(currentIndex);
        }

        @Override
        @Iterator.IsDone
        public boolean hasNext() {
            return currentIndex < studentsList.size() - 1;
        }

        @Override
        @Iterator.NextItem
        public Student next() {
            if (hasNext()) {
                currentIndex++;
                return current();
            } else {
                throw new NoSuchElementException("StudentCollectionIterator has finished iterating over all the students in the collection");
            }
        }
    }
}

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