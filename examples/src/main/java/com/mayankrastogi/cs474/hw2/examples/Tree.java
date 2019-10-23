package com.mayankrastogi.cs474.hw2.examples;

import com.mayankrastogi.cs474.hw2.annotations.IterableAggregate;
import com.mayankrastogi.cs474.hw2.annotations.Iterator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@IterableAggregate(Tree.TreeIterator.class)
public class Tree<T> {

    private Node<T> root;

    public Tree(Node<T> root) {
        this.root = root;
    }

    @IterableAggregate.IteratorFactory
    public TreeIterator dfsIterator() {
        return new TreeIterator(true);
    }

    @IterableAggregate.IteratorFactory
    public TreeIterator bfsIterator() {
        return new TreeIterator(false);
    }

    @Override
    public String toString() {
        return "Tree(root: " + root + ")";
    }

    @Iterator(Node.class)
    public class TreeIterator {
        private boolean dfs;
        private Node<T> currentNode;
        private ArrayDeque<Node<T>> deque;

        private TreeIterator(boolean dfs) {
            this.dfs = dfs;
            deque = new ArrayDeque<>();
            deque.add(root);
        }

        @Iterator.CurrentItem
        public Node<T> currentNode() {
            return currentNode;
        }

        @Iterator.IsDone
        public boolean isDone() {
            return deque.isEmpty();
        }

        @Iterator.NextItem
        public Node<T> next() {
            if (isDone()) throw new NoSuchElementException("All nodes in the tree have been traversed.");

            currentNode = deque.pop();
            if (dfs) {
                var childrenIterator = currentNode.children.listIterator(currentNode.children.size());
                while (childrenIterator.hasPrevious()) {
                    deque.push(childrenIterator.previous());
                }
            } else {
                deque.addAll(currentNode.children);
            }
            return currentNode();
        }
    }
}

class Node<T> {
    T data;
    List<Node<T>> children;

    public Node(T data) {
        this.data = data;
        this.children = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Node(data: " + data + ", children: " + children + ")";
    }
}
