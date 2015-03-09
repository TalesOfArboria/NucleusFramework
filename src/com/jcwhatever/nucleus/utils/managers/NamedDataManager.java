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

package com.jcwhatever.nucleus.utils.managers;

import com.jcwhatever.nucleus.mixins.INamed;
import com.jcwhatever.nucleus.storage.IDataNode;

import javax.annotation.Nullable;

/**
 * An abstract manager for named objects that are stored to
 * an {@link IDataNode}.
 *
 * <p>Does not include a public {@link #add} method. You can make the protected
 * {@link #add} method public, or make your own method. Be sure to
 * call the protected {@link #add} method from your implementation
 * to add a new item.</p>
 */
public abstract class NamedDataManager<T extends INamed>  extends NamedManager<T> {

    protected final IDataNode _dataNode;

    /**
     * Constructor.
     *
     * @param dataNode  The data node.
     * @param loadData  True to load data from the data node during the constructor.
     */
    protected NamedDataManager(@Nullable IDataNode dataNode, boolean loadData) {

        _dataNode = dataNode;

        if (loadData)
            load();
    }

    /**
     * Invoked to load an object from the data node.
     *
     * @param name      The name of the object.
     * @param itemNode  The data node it is stored on.
     *
     * @return  The instantiated object.
     */
    @Nullable
    protected abstract T load(String name, IDataNode itemNode);

    /**
     * Invoked to save an object to its data node.
     *
     * @param item      The object to save.
     * @param itemNode  The data node to save its properties to.
     */
    protected abstract void save(T item, IDataNode itemNode);

    @Override
    protected boolean onAdd(T added) {
        if (_dataNode != null) {
            IDataNode dataNode = getNode(added.getName());
            save(added, dataNode);
            dataNode.save();
        }
        return true;
    }

    @Override
    protected void onRemove(T removed) {
        if (_dataNode != null) {
            IDataNode dataNode = getNode(removed.getName());
            dataNode.remove();
            dataNode.save();
        }
    }

    /**
     * Invoked after settings are loaded.
     */
    protected void onLoad() {}

    /**
     * Invoked to get the data node for an item.
     *
     * @param name  The name of the item.
     */
    protected IDataNode getNode(String name) {
        assert _dataNode != null;
        return _dataNode.getNode(name);
    }

    /**
     * Invoked to load managed objects from the data node.
     */
    protected void load() {
        if (_dataNode == null)
            return;

        _map.clear();

        for (IDataNode node : _dataNode) {
            T item = load(node.getName(), node);
            if (item == null)
                continue;

            _map.put(getName(item), item);
            onAdd(item);
        }

        onLoad();
    }
}
