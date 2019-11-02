package com.mayankrastogi.cs474.hw2.examples;

import com.mayankrastogi.cs474.hw2.annotations.IterableAggregate;
import com.mayankrastogi.cs474.hw2.annotations.Iterator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A generic class that holds a reference to a tree.
 * <p>
 * This class provides two iterator factory methods which can be used to iterate through the nodes in the tree either in
 * depth-first or breadth-first order.
 *
 * @param <T> The type of data stored in a node of this tree.
 */
@IterableAggregate(Tree.TreeIterator.class)
public class Tree<T> {

    private Node<T> root;

    /**
     * A generic class that holds a reference to a tree.
     * <p>
     * This class provides two iterator factory methods which can be used to iterate through the nodes in the tree
     * either in depth-first or breadth-first order.
     *
     * @param root The root {@link Node} of the tree.
     */
    public Tree(Node<T> root) {
        this.root = root;
    }

    /**
     * Creates a {@link TreeIterator} which can be used to iterate through the nodes in the tree in depth-first order.
     *
     * @return The iterator.
     */
    @IterableAggregate.IteratorFactory
    public TreeIterator dfsIterator() {
        return new TreeIterator(true);
    }

    /**
     * Creates a {@link TreeIterator} which can be used to iterate through the nodes in the tree in breadth-first order.
     *
     * @return The iterator.
     */
    @IterableAggregate.IteratorFactory
    public TreeIterator bfsIterator() {
        return new TreeIterator(false);
    }

    @Override
    public String toString() {
        return "Tree(root: " + root + ")";
    }

    /**
     * An iterator for accessing {@link Node}s of a {@link Tree} in either depth-first or breadth-first order.
     */
    @Iterator(Node.class)
    public class TreeIterator {
        private boolean dfs;
        private Node<T> currentNode;

        // A deque can be used as a queue as well as a stack
        private ArrayDeque<Node<T>> deque;

        /**
         * An iterator for accessing {@link Node}s of a {@link Tree} in either depth-first or breadth-first order.
         *
         * @param dfs {@code true} if depth-first order is desired, {@code false} for breadth-first order.
         */
        private TreeIterator(boolean dfs) {
            this.dfs = dfs;
            deque = new ArrayDeque<>();
            deque.add(root);
        }

        /**
         * The current {@link Node} object in the current state of iteration.
         *
         * @return The current {@link Node} in the iteration; {@code null} if the {@link #next()} method wasn't called
         * before calling this method.
         */
        @Iterator.CurrentItem
        public Node<T> currentNode() {
            return currentNode;
        }

        /**
         * Tells whether the iterator has finished iterating through all the nodes.
         *
         * @return {@code true} if all the nodes have been iterated, {@code false} otherwise.
         */
        @Iterator.IsDone
        public boolean isDone() {
            return deque.isEmpty();
        }

        /**
         * The next {@link Node} object in the current state of iteration.
         * <p>
         * A {@link NoSuchElementException} is thrown if this method is called after all the nodes in the tree have been
         * iterated.
         *
         * @return The next {@link Node} in the iteration.
         */
        @Iterator.NextItem
        public Node<T> next() {
            if (isDone()) throw new NoSuchElementException("All nodes in the tree have been traversed.");

            // Irrespective of the order, we always pop the first element in the deque
            currentNode = deque.pop();

            // For DFS, push children onto the stack (at the front of the deque)
            if (dfs) {
                var childrenIterator = currentNode.children.listIterator(currentNode.children.size());
                // We push the children onto the stack in reverse order so that the first child gets traversed first
                while (childrenIterator.hasPrevious()) {
                    deque.push(childrenIterator.previous());
                }
            }
            // For BFS, add children to the end of the deque
            else {
                deque.addAll(currentNode.children);
            }
            return currentNode();
        }
    }
}

/**
 * Models a node in the {@link Tree}.
 *
 * @param <T> The type of `data` to be stored in the node.
 */
class Node<T> {
    T data;
    List<Node<T>> children;

    public Node(T data) {
        this.data = data;
        this.children = new ArrayList<>();
    }

    @Override
    public String toString() {
        return String.format("Node(data: %s, children: %s)", data, children);
    }
}
