/*
 * This file is part of NucleusFramework for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.jcwhatever.nucleus.collections;

import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;

/**
 * An iterable tree node.
 *
 * <p>Recursively iterates starting from the node to all child nodes. Does not repeat
 * any element.</p>
 */
public class TreeNode<T> implements Iterable<TreeNode<T>> {

    private T _node;
    private TreeNode<T> _parent;
    private List<TreeNode<T>> _children;

    /**
     * Constructor.
     *
     * @param nodeValue  The node value.
     */
    public TreeNode(T nodeValue) {
        PreCon.notNull(nodeValue);

        _node = nodeValue;
    }

    /**
     * Determine if this the node is
     * the root node.
     */
    public boolean isRoot() {
        return _parent == null;
    }

    /**
     * Determine if the node is a leaf
     * node (has no children).
     */
    public boolean isLeaf() {
        return _children == null || _children.size() == 0;
    }

    /**
     * Get the level/depth of the node. The root
     * node is 0.
     */
    public int getDepth() {
        return _parent == null ? 0 : _parent.getDepth() + 1;
    }

    /**
     * Get the nodes value.
     */
    public T getValue() {
        return _node;
    }

    /**
     * Get the nodes parent.
     *
     * @return  Null if the node is a root node.
     */
    @Nullable
    public TreeNode<T> getParent() {
        return _parent;
    }

    /**
     * Get the nodes children.
     */
    public List<TreeNode<T>> getChildren() {
        return _children != null
                ? CollectionUtils.unmodifiableList(_children)
                : new ArrayList<TreeNode<T>>(0);
    }

    /**
     * Get the number of direct children
     * of the node.
     */
    public int totalChildren() {
        if (_children == null)
            return 0;

        return _children.size();
    }

    /**
     * Get the index of the direct child.
     *
     * @param childValue  The child value.
     *
     * @return  The list index or -1 if not found.
     */
    public int getChildIndex(T childValue) {
        if (_children == null)
            return -1;

        for (int i=0; i < _children.size(); i++) {
            if (childValue.equals(_children.get(i).getValue()))
                return i;
        }

        return -1;
    }

    /**
     * Get the index of the direct child node.
     *
     * @param childNode  The child node.
     *
     * @return  The list index or -1 if not found.
     */
    public int getChildIndex(TreeNode<T> childNode) {
        if (_children == null)
            return -1;

        for (int i=0; i < _children.size(); i++) {
            if (childNode.equals(_children.get(i)))
                return i;
        }

        return -1;
    }

    /**
     * Add a child node.
     *
     * @param childValue  The child value.
     *
     * @return  The child node.
     */
    public TreeNode<T> addChild(T childValue) {

        TreeNode<T> node = new TreeNode<>(childValue);
        node._parent = this;

        if (_children == null)
            _children = new ArrayList<TreeNode<T>>(5);

        _children.add(node);

        return node;
    }

    /**
     * Add a child node.
     *
     * @param childNode  The child node.
     *
     * @return  The child node.
     */
    public TreeNode<T> addChild(TreeNode<T> childNode) {

        childNode._parent = this;

        if (_children == null)
            _children = new ArrayList<TreeNode<T>>(5);

        _children.add(childNode);

        return childNode;
    }

    /**
     * Remove a child node.
     *
     * @param child  The child value.
     *
     * @return  The removed child node. Null if not found.
     */
    @Nullable
    public TreeNode<T> removeChild(T child) {

        if (_children == null)
            return null;

        Iterator<TreeNode<T>> iterator = _children.iterator();
        while (iterator.hasNext()) {
            TreeNode<T> childNode = iterator.next();
            if (child.equals(childNode.getValue())) {
                iterator.remove();
                childNode._parent = null;
                return childNode;
            }
        }

        return null;
    }

    /**
     * Remove a child node.
     *
     * @param childNode  The child node.
     *
     * @return  The removed child node. Null if not found.
     */
    @Nullable
    public TreeNode<T> removeChild(TreeNode<T> childNode) {

        if (_children == null)
            return null;

        Iterator<TreeNode<T>> iterator = _children.iterator();
        while (iterator.hasNext()) {
            TreeNode<T> node = iterator.next();
            if (childNode.equals(node)) {
                iterator.remove();
                node._parent = null;
                return node;
            }
        }

        return null;
    }

    @Override
    public Iterator<TreeNode<T>> iterator() {
        return new Iter();
    }

    @Override
    public int hashCode() {
        return _node.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof TreeNode) {
            return _node.equals(((TreeNode) object)._node);
        }
        return _node.equals(object);
    }

    // Tree node iterator. Iterates all children and sub children from the top down,
    // does not repeat any element.
    private class Iter implements Iterator<TreeNode<T>> {

        LinkedList<StackIterator> recursionStack = new LinkedList<>();
        StackIterator currentIterator;
        TreeNode<T> currentNode;
        boolean iterSelf;

        Iter() {
            if (!isLeaf()) {
                currentIterator = new StackIterator(_children.iterator());
                recursionStack.push(currentIterator);
            }
        }

        @Override
        public boolean hasNext() {

            if (!iterSelf)
                return true;

            if (recursionStack.isEmpty())
                return false;

            currentIterator = recursionStack.peek();

            boolean hasNext = currentIterator.hasNext();

            while (!hasNext) {
                recursionStack.pop();

                currentIterator = recursionStack.peek();
                if (currentIterator == null)
                    return false;

                hasNext = currentIterator.hasNext();
            }

            return true;
        }

        @Override
        public TreeNode<T> next() {

            if (iterSelf) {
                currentNode = currentIterator.next();

                if (!currentNode.isLeaf()) {
                    recursionStack.push(new StackIterator(currentNode._children.iterator()));
                }
            } else {
                iterSelf = true;
                currentNode = TreeNode.this;
            }

            return currentNode;
        }

        @Override
        public void remove() {

            currentIterator.remove();

            if (!currentIterator.hasNext()) {
                recursionStack.pop();
                currentIterator = recursionStack.peek();
            }
        }
    }

    // used to track recursion info
    private class StackIterator {
        Iterator<TreeNode<T>> childIterator;
        Boolean hasNext;

        StackIterator(Iterator<TreeNode<T>> iterator) {
            this.childIterator = iterator;
        }

        boolean hasNext() {
            if (hasNext == null) {
                hasNext = childIterator.hasNext();
            }
            return hasNext;
        }

        TreeNode<T> next() {
            hasNext = null;
            return childIterator.next();
        }

        void remove() {
            childIterator.remove();
        }
    }
}
