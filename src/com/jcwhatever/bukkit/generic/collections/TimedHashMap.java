/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.collections;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * An hash map where each key value has an individual lifespan that when reached, causes the item
 * to be removed.
 *
 * <p>The lifespan can only be reset by re-adding an item.</p>
 *
 * <p>Items can be added using the default lifespan time or a lifespan can be specified per item.</p>
 */
public class TimedHashMap<K, V> extends HashMap<K, V> implements ITimedMap<K, V>, ITimedCallbacks<K, TimedHashMap<K, V>> {

    private static final long serialVersionUID = 1945035628724130125L;

    private final int _defaultTime;
    private final TimedHashMap<K, V> _instance;
    private Map<Object, BukkitTask> _tasks;
    private List<LifespanEndAction<K>> _onLifespanEnd = new ArrayList<>(5);
    private List<CollectionEmptyAction<TimedHashMap<K, V>>> _onEmpty = new ArrayList<>(5);

    /**
     * Constructor. Default lifespan is 1 second.
     */
    public TimedHashMap() {
        super();
        _defaultTime = 20;
        _instance = this;
        _tasks = new HashMap<>(20);
    }

    /**
     * Constructor. Default lifespan is 1 second.
     *
     * @param size  The initial capacity of the map.
     */
    public TimedHashMap(int size) {
        super(size);
        _defaultTime = 20;
        _instance = this;
        _tasks = new HashMap<>(size);
    }

    /**
     * Constructor.
     *
     * @param size             The initial capacity of the map.
     * @param defaultLifespan  The default lifespan of items in ticks.
     */
    public TimedHashMap(int size, int defaultLifespan) {
        super();
        PreCon.positiveNumber(defaultLifespan);

        _defaultTime = defaultLifespan;
        _instance = this;
        _tasks = new HashMap<>(size);
    }

    @Override
    public void clear() {
        for (BukkitTask task : _tasks.values()) {
            task.cancel();
        }
        _tasks.clear();
        super.clear();

        onEmpty();
    }

    /**
     * Put an item into the map using the specified lifespan.
     *
     * @param key       The item key.
     * @param value     The item to add.
     * @param lifespanTicks  The items lifespan in ticks.
     */
    @Override
    public V put(final K key, final V value, int lifespanTicks) {
        PreCon.notNull(key);
        PreCon.notNull(value);
        PreCon.positiveNumber(lifespanTicks);

        if (lifespanTicks == 0)
            return value;

        if (lifespanTicks < 0) {
            return super.put(key, value);
        }

        BukkitTask current = _tasks.remove(key);
        if (current != null) {
            current.cancel();
        }

        BukkitTask task = Bukkit.getScheduler().runTaskLater(GenericsLib.getLib(), new Runnable() {

            @Override
            public void run() {
                _tasks.remove(value);
                _instance.remove(key);
                onLifespanEnd(key);
            }

        }, lifespanTicks);

        _tasks.put(key, task);
        return super.put(key, value);

    }

    /**
     * Put an item into the map using the default lifespan.
     *
     * @param key    The item key.
     * @param value  The item to add.
     */
    @Override
    public V put(K key, V value) {
        PreCon.notNull(key);
        PreCon.notNull(value);

        return put(key, value, _defaultTime);
    }

    /**
     * Put a map of items into the map using the specified lifespan.
     *
     * @param entries   The map to add.
     * @param lifespanTicks  The lifespan of the added items in ticks.
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> entries, int lifespanTicks) {
        PreCon.notNull(entries);
        PreCon.positiveNumber(lifespanTicks);

        for (Map.Entry<? extends K, ? extends V> entry : entries.entrySet()) {
            put(entry.getKey(), entry.getValue(), lifespanTicks);
        }

    }

    /**
     * Put a map of items into the map using the default lifespan.
     *
     * @param entries  The map to add.
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> entries) {
        PreCon.notNull(entries);

        putAll(entries, _defaultTime);
    }

    @Override
    @Nullable
    public V remove(Object key) {
        PreCon.notNull(key);

        V value = super.remove(key);
        if (value == null)
            return null;

        BukkitTask task = _tasks.remove(key);
        if (task != null)
            task.cancel();

        onEmpty();

        return value;
    }

    /**
     * Add a handler to be called whenever an items lifespan ends.
     *
     * @param callback  The handler to call.
     */
    @Override
    public void addOnLifespanEnd(LifespanEndAction<K> callback) {
        PreCon.notNull(callback);

        _onLifespanEnd.add(callback);
    }

    /**
     * Remove a handler.
     *
     * @param callback  The handler to remove.
     */
    @Override
    public void removeOnLifespanEnd(LifespanEndAction<K> callback) {
        PreCon.notNull(callback);

        _onLifespanEnd.remove(callback);
    }

    /**
     * Add a handler to be called whenever the collection becomes empty.
     *
     * @param callback  The handler to call
     */
    @Override
    public void addOnCollectionEmpty(CollectionEmptyAction<TimedHashMap<K, V>> callback) {
        PreCon.notNull(callback);

        _onEmpty.add(callback);
    }

    /**
     * Remove a handler.
     *
     * @param callback  The handler to remove.
     */
    @Override
    public void removeOnCollectionEmpty(CollectionEmptyAction<TimedHashMap<K, V>> callback) {
        PreCon.notNull(callback);

        _onEmpty.remove(callback);
    }

    private void onEmpty() {
        if (!isEmpty() || _onEmpty.isEmpty())
            return;

        for (CollectionEmptyAction<TimedHashMap<K, V>> action : _onEmpty) {
            action.onEmpty(this);
        }
    }

    private void onLifespanEnd(K key) {
        for (LifespanEndAction<K> action : _onLifespanEnd) {
            action.onEnd(key);
        }
    }
}
