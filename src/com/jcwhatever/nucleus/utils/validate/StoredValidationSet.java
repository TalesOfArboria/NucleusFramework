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

package com.jcwhatever.nucleus.utils.validate;

import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Used for validation using a set of stored elements.
 *
 * <p>Stores set to a data node.</p>
 */
public abstract class StoredValidationSet<E> extends ValidationSet<E> {

    protected IDataNode _dataNode;

    /**
     * Constructor.
     *
     * @param dataNode  The data node.
     */
    public StoredValidationSet(IDataNode dataNode) {
        super();
        PreCon.notNull(dataNode);

        _dataNode = dataNode;

        load();
    }

    @Override
    public void setPolicy(ValidationPolicy policy) {
        super.setPolicy(policy);

        _dataNode.set("policy", policy);
        _dataNode.saveAsync(null);
    }

    @Override
    public boolean add(E element) {
        PreCon.notNull(element);

        String name = getElementNodeName(element);
        if (name == null)
            return false;

        if (super.add(element)) {
            saveElement(element, _dataNode.getNode("elements." + name));
            return true;
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        PreCon.notNull(collection);

        boolean isChanged = false;

        for (E element : collection) {
            String name = getElementNodeName(element);
            if (name == null)
                continue;

            if (super.add(element)) {
                saveElement(element, _dataNode.getNode("elements." + name));
                isChanged = true;
            }
        }
        return isChanged;
    }

    @Override
    public boolean remove(Object element) {
        PreCon.notNull(element);

        String name = getElementNodeName(element);
        if (name == null)
            return false;

        if (super.remove(element)) {
            IDataNode dataNode = _dataNode.getNode("elements." + name);
            dataNode.remove();
            dataNode.saveAsync(null);
            return true;
        }

        return false;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        PreCon.notNull(collection);

        boolean isChanged = false;

        for (Object obj : collection) {

            String name = getElementNodeName(obj);
            if (name == null)
                continue;

            if (super.remove(obj)) {
                IDataNode dataNode = _dataNode.getNode("elements." + name);
                dataNode.remove();
                dataNode.saveAsync(null);
                isChanged = true;
            }
        }

        return isChanged;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        PreCon.notNull(collection);

        Set<E> removed = new HashSet<>(this);
        //noinspection SuspiciousMethodCalls
        removed.removeAll(collection);

        boolean isChanged = false;

        for (E element : removed) {

            String name = getElementNodeName(element);
            if (name == null)
                continue;

            if (super.remove(element)) {
                IDataNode dataNode = _dataNode.getNode("elements." + name);
                dataNode.remove();
                dataNode.saveAsync(null);
                isChanged = true;
            }
        }

        return isChanged;
    }

    /**
     * Called to get the name of the node to store an element in.
     *
     * @param element  The element.
     *
     * @return  The node name or null to reject.
     */
    @Nullable
    protected abstract String getElementNodeName(Object element);

    /**
     * Called to save an element.
     *
     * @param element   The element to save.
     * @param dataNode  The elements data node.
     */
    protected abstract void saveElement(E element, IDataNode dataNode);

    /**
     * Called to load an element from the data node.
     *
     * @param dataNode  The data node.
     *
     * @return  The element or null to reject.
     */
    protected abstract E loadElement(IDataNode dataNode);

    // initial load of settings
    protected void load() {
        _policy = _dataNode.getEnum("policy", _policy, ValidationPolicy.class);

        clear();
        IDataNode eNode = _dataNode.getNode("elements");

        for (IDataNode node : eNode) {
            E element = loadElement(node);
            if (element == null)
                continue;

            add(element);
        }
    }
}
