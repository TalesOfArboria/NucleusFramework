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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

/**
 * An iterable tree node whose nodes have key values.
 *
 * <p>Recursively iterates starting from the node to all child nodes. Does not repeat
 * any element. Order of a nodes child nodes when iterated is not guaranteed. It is
 * guaranteed that the parent node will be iterated just before its child nodes are.</p>
 */
public class TreeEntryNode<K, V> implements Iterable<TreeEntryNode<K, V>> {

    private Entry<K, V> _node;
    private TreeEntryNode<K, V> _parent;
    private Map<K, TreeEntryNode<K, V>> _children;

    /**
     * Constructor.
     *
     * @param key    The node key
     * @param value  The node value.
     */
    public TreeEntryNode(K key, @Nullable V value) {
        PreCon.notNull(key);

        _node = new NodeEntry<>(key, value);
    }

    /**
     * Determine if the node is the root node.
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
     * Get the nodes key.
     */
    public K getKey() {
        return _node.getKey();
    }

    /**
     * Get the nodes value.
     */
    @Nullable
    public V getValue() {
        return _node.getValue();
    }

    /**
     * Get the nodes parent.
     *
     * @return  Null if the node is a root node.
     */
    @Nullable
    public TreeEntryNode<K, V> getParent() {
        return _parent;
    }

    /**
     * Get the nodes children.
     */
    public Collection<TreeEntryNode<K, V>> getChildren() {
        return _children != null
                ? Collections.unmodifiableCollection(_children.values())
                : new ArrayList<TreeEntryNode<K, V>>(0);
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
     * Determine if the node has a child
     * with the specified key.
     *
     * @param key  The key to check.
     */
    public boolean hasChild(K key) {
        return _children != null && _children.containsKey(key);
    }

    /**
     * Get a child node.
     *
     * @param key  The child node key.
     *
     * @return  The list index or -1 if not found.
     */
    @Nullable
    public TreeEntryNode<K, V> getChild(K key) {
        if (_children == null)
            return null;

        return _children.get(key);
    }

    /**
     * Add a child node.
     *
     * @param key    The child key.
     * @param value  The child value.
     *
     * @return  The created child node.
     */
    public TreeEntryNode<K, V> putChild(K key, V value) {

        TreeEntryNode<K, V> node = new TreeEntryNode<>(key, value);
        node._parent = this;

        if (_children == null)
            _children = new HashMap<>(7);

        _children.put(key, node);

        return node;
    }

    /**
     * Add a child node.
     *
     * @param childNode  The child node.
     *
     * @return  The previous child node of the same key.
     */
    @Nullable
    public TreeEntryNode<K, V> putChild(TreeEntryNode<K, V> childNode) {

        childNode._parent = this;

        if (_children == null)
            _children = new HashMap<>(7);

        return _children.put(childNode.getKey(), childNode);
    }

    /**
     * Remove a child node.
     *
     * @param key  The child key.
     *
     * @return  The removed child node. Null if not found.
     */
    @Nullable
    public TreeEntryNode<K, V> removeChild(K key) {
        if (_children == null)
            return null;

        return _children.remove(key);
    }

    /**
     * Remove a child node.
     *
     * @param childNode  The child node.
     *
     * @return  The removed child node. Null if not found.
     */
    @Nullable
    public TreeEntryNode<K, V> removeChild(TreeEntryNode<K, V> childNode) {
        PreCon.notNull(childNode);

        if (_children == null)
            return null;

        return _children.remove(childNode.getKey());
    }

    @Override
    public Iterator<TreeEntryNode<K, V>> iterator() {
        return new Iter();
    }

    private static class NodeEntry<K, V> implements Entry<K, V> {

        final K key;
        V value;

        NodeEntry(K key, @Nullable V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        @Nullable
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(@Nullable V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }

    // Tree node iterator. Iterates all children and sub children from the top down,
    // does not repeat any element. Order is not guaranteed.
    private class Iter implements Iterator<TreeEntryNode<K, V>> {

        LinkedList<StackIterator> recursionStack = new LinkedList<>();
        StackIterator current;

        Iter() {
            if (!isLeaf()) {
                current = new StackIterator(_children.values().iterator());
                recursionStack.push(current);
            }
        }

        @Override
        public boolean hasNext() {
            return !recursionStack.isEmpty() && recursionStack.peek().childIterator.hasNext();
        }

        @Override
        public TreeEntryNode<K, V> next() {

            current = recursionStack.peek();

            TreeEntryNode<K, V> currentNode = current.childIterator.next();

            if (!current.childIterator.hasNext())
                recursionStack.pop();

            if (!currentNode.isLeaf()) {
                recursionStack.push(new StackIterator(currentNode._children.values().iterator()));
            }

            return currentNode;
        }

        @Override
        public void remove() {

            current.childIterator.remove();

            if (!current.childIterator.hasNext()) {
                recursionStack.pop();
                current = recursionStack.peek();
            }
        }
    }

    // used to track recursion info
    private class StackIterator {
        Iterator<TreeEntryNode<K, V>> childIterator;

        StackIterator(Iterator<TreeEntryNode<K, V>> iterator) {
            this.childIterator = iterator;
        }
    }

}
