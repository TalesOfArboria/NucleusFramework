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

import com.jcwhatever.nucleus.utils.PreCon;

import java.util.ArrayList;
import java.util.Collection;
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
    public int getLevel() {
        return _parent == null ? 0 : _parent.getLevel() + 1;
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
    public Collection<TreeNode<T>> getChildren() {
        return _children != null ? _children : new ArrayList<TreeNode<T>>(0);
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
            if (childNode.getValue().equals(child)) {
                iterator.remove();
                childNode._parent = null;
                return childNode;
            }
        }

        return null;
    }

    @Override
    public Iterator<TreeNode<T>> iterator() {
        return new Iter();
    }

    // Tree node iterator. Iterates all children and sub children from the top down,
    // does not repeat any element.
    private class Iter implements Iterator<TreeNode<T>> {

        LinkedList<StackNode<T>> recursionStack = new LinkedList<>();
        StackNode<T> nxt;
        StackNode<T> current;

        Iter() {
            nxt = new StackNode<>(TreeNode.this);
            recursionStack.addLast(nxt);
        }

        @Override
        public boolean hasNext() {
            return nxt != null;
        }

        @Override
        public TreeNode<T> next() {
            StackNode<T> result = current = nxt;
            setupNext();
            return result.node;
        }

        @Override
        public void remove() {

            if (current.node == TreeNode.this)
                throw new IllegalStateException("Cannot remove the top level node of the iterator.");

            StackNode<T> toRemove = current;

            assert toRemove.node.getParent() != null;

            if (toRemove.node.getParent().removeChild(toRemove.node.getValue()) == null)
                throw new RuntimeException("Failed to remove node.");

            if (recursionStack.isEmpty()) {
                nxt = null;
            } else {
                recursionStack.removeLast(); // remove current

                // set current
                current = recursionStack.peekLast();
                if (!current.node.isLeaf()) {
                    current.childIndex--;
                }

                setupNext();
            }
        }

        private void setupNext() {
            StackNode<T> nextParent = findNextParent(current);
            if (nextParent != null) {
                nxt = new StackNode<>(nextParent.node._children.get(nextParent.childIndex));
                recursionStack.addLast(nxt);
                nextParent.childIndex++;
            }
            else {
                nxt = null;
            }
        }

        @Nullable
        private StackNode<T> findNextParent(StackNode<T> current) {

            if (recursionStack.isEmpty())
                return null;

            StackNode<T> scanNode = current;

            while(scanNode != null && (scanNode.node.isLeaf() ||
                    scanNode.childIndex >= scanNode.node._children.size())) {

                recursionStack.removeLast();
                scanNode = recursionStack.peekLast();
            }

            return scanNode;
        }
    }

    // used to track recursion info
    private static class StackNode<T> {
        TreeNode<T> node;
        int childIndex = 0;

        StackNode(TreeNode<T> node) {
            this.node = node;
        }
    }
}
