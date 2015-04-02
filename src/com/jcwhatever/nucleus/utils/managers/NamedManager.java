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
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * An abstract manager for named objects.
 *
 * <p>Does not include a public {@link #add} method. You can make the protected
 * {@link #add} method public, or make your own method. Be sure to
 * call the protected {@link #add} method from your implementation
 * to add a new item.</p>
 */
public abstract class NamedManager<T extends INamed> implements INamedManager<T> {

    protected Map<String, T> _map = new HashMap<>(15);

    @Override
    public boolean contains(String name) {
        PreCon.notNull(name);

        return _map.containsKey(getName(name));
    }

    @Override
    @Nullable
    public T get(String name) {
        PreCon.notNull(name);

        return _map.get(getName(name));
    }

    @Override
    public Collection<T> getAll() {
        return Collections.unmodifiableCollection(_map.values());
    }

    @Override
    public boolean remove(String name) {
        PreCon.notNull(name);

        T item = _map.remove(getName(name));
        if (item == null)
            return false;

        onRemove(item);

        return true;
    }

    /**
     * Invoked when an item is removed.
     *
     * @param removed  The removed item.
     */
    protected void onRemove(T removed) {}

    /**
     * Invoked when an item is added.
     *
     * @param added  The added item.
     */
    protected boolean onAdd(T added) { return true; }

    /**
     * Add a new item to the manager.
     *
     * @param item  The item to add.
     */
    protected boolean add(T item) {
        _map.put(getName(item), item);
        return onAdd(item);
    }

    /**
     * Get the correct name to use as a map key.
     *
     * @param name  The name to check.
     */
    protected String getName(String name) {
        return name;
    }

    /**
     * Get the name to use as a map key for an item.
     *
     * @param item  The item to check.
     */
    protected String getName(T item) {
        return item.getName();
    }
}
