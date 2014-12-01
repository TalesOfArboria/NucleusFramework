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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An array list where each item has an individual lifespan that when reached, causes the item
 * to be removed.
 *
 * <p>The items lifespan cannot be reset except by removing it.</p>
 *
 * <p>Items can be added using the default lifespan time or a lifespan can be specified per item.</p>
 */
public class TimedArrayList<E> extends ArrayList<E>
        implements ITimedList<E>, ITimedCallbacks<E, TimedArrayList<E>> {

    private static final long serialVersionUID = 6625205971219435341L;

    private final int _defaultTime;
    private final TimedArrayList<E> _instance;
    private Map<Object, BukkitTask> _tasks;
    private List<LifespanEndAction<E>> _onLifespanEnd = new ArrayList<>(5);
    private List<CollectionEmptyAction<TimedArrayList<E>>> _onEmpty = new ArrayList<>(5);

    /**
     * Constructor. Default item lifespan is 1 second.
     */
    public TimedArrayList() {
        super(20);
        _defaultTime = 20;
        _instance = this;
        _tasks = new HashMap<>(20);
    }

    /**
     * Constructor. Default item lifespan is 1 second.
     *
     * @param size  The initial capacity of the list.
     */
    public TimedArrayList(int size) {
        super(size);
        _defaultTime = 20;
        _instance = this;
        _tasks = new HashMap<>(size);
    }

    /**
     * Constructor. Specify default item lifespan.
     *
     * @param size         The initial capacity of the list.
     * @param defaultTime  The default lifespan of items.
     */
    public TimedArrayList(int size, int defaultTime) {
        super(size);
        PreCon.positiveNumber(defaultTime);

        _defaultTime = defaultTime;
        _instance = this;
        _tasks = new HashMap<>(size);
    }

    /**
     * Add an item to the list and specify its lifetime in ticks.
     *
     * @param item      The item to add.
     * @param lifespan  The amount of time in ticks it will stay in the list.
     */
    @Override
    public boolean add(final E item, int lifespan) {
        PreCon.notNull(item);
        PreCon.positiveNumber(lifespan);

        scheduleRemoval(item, lifespan);

        return super.add(item);
    }


    /**
     * Add an item to the list using the default lifetime.
     *
     * @param item  The item to add.
     */
    @Override
    public boolean add(E item) {
        return add(item, _defaultTime);
    }

    /**
     * Insert an item into the list at the specified index
     * and specify its lifetime in ticks.
     *
     * @param index     The index position to insert at.
     * @param item      The item to insert.
     * @param lifespan  The amount of time in ticks the item will stay in the list.
     */
    @Override
    public void add(int index, E item, int lifespan) {
        PreCon.positiveNumber(index);
        PreCon.notNull(item);
        PreCon.positiveNumber(lifespan);

        scheduleRemoval(item, lifespan);

        super.add(index, item);
    }

    /**
     * Insert an item into the list at the specified index
     * and specify its lifetime in ticks.
     *
     * @param index  The index position to insert at.
     * @param item   The item to insert.
     */
    @Override
    public void add(int index, E item) {
        PreCon.positiveNumber(index);
        PreCon.notNull(item);

        add(index, item, _defaultTime);
    }

    /**
     * Add a collection to the list and specify the lifetime in ticks.
     *
     * @param collection  The collection to add.
     * @param lifespan    The amount of time in ticks it will stay in the list.
     */
    @Override
    public boolean addAll(Collection<? extends E> collection, int lifespan) {
        PreCon.notNull(collection);
        PreCon.positiveNumber(lifespan);

        for (E item : collection) {
            scheduleRemoval(item, lifespan);
        }
        return super.addAll(collection);
    }

    /**
     * Add a collection to the list using the default lifespan.
     *
     * @param collection  The collection to add.
     */
    @Override
    public boolean addAll(Collection<? extends E> collection) {
        PreCon.notNull(collection);

        return addAll(collection, _defaultTime);
    }

    /**
     * Insert a collection into the list at the specified index
     * and specify the lifetime in ticks.
     *
     * @param index       The index position to insert at.
     * @param collection  The collection to add.
     * @param lifespan    The amount of time in ticks it will stay in the list.
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> collection, int lifespan) {
        PreCon.positiveNumber(index);
        PreCon.notNull(collection);
        PreCon.positiveNumber(lifespan);

        for (E item : collection) {
            scheduleRemoval(item, lifespan);
        }
        return super.addAll(index, collection);
    }

    /**
     * Insert a collection into the list at the specified index
     * using the default lifetime.
     *
     * @param index       The index position to insert at.
     * @param collection  The collection to add.
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> collection) {
        PreCon.positiveNumber(index);
        PreCon.notNull(collection);

        return addAll(index, collection, _defaultTime);
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

    @Override
    public boolean remove(Object item) {
        PreCon.notNull(item);

        BukkitTask task = _tasks.remove(item);

        if (task != null)
            task.cancel();

        onEmpty();

        return super.remove(item);
    }

    @Override
    public E remove(int index) {
        PreCon.positiveNumber(index);

        E item = super.remove(index);
        BukkitTask task = _tasks.remove(item);

        if (task != null)
            task.cancel();

        onEmpty();

        return item;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        PreCon.notNull(collection);

        for (Object item : collection) {
            BukkitTask task = _tasks.remove(item);
            if (task != null)
                task.cancel();
        }

        if (super.removeAll(collection)) {
            onEmpty();
            return true;
        }

        return false;
    }


    /**
     * Add a handler to be called whenever an items lifespan ends.
     *
     * @param callback  The handler to call.
     */
    @Override
    public void addOnLifespanEnd(LifespanEndAction<E> callback) {
        PreCon.notNull(callback);

        _onLifespanEnd.add(callback);
    }

    /**
     * Remove a handler.
     *
     * @param callback  The handler to remove.
     */
    @Override
    public void removeOnLifespanEnd(LifespanEndAction<E> callback) {
        PreCon.notNull(callback);

        _onLifespanEnd.remove(callback);
    }

    /**
     * Add a handler to be called whenever the collection becomes empty.
     *
     * @param callback  The handler to call
     */
    @Override
    public void addOnCollectionEmpty(CollectionEmptyAction<TimedArrayList<E>> callback) {
        PreCon.notNull(callback);

        _onEmpty.add(callback);
    }

    /**
     * Remove a handler.
     *
     * @param callback  The handler to remove.
     */
    @Override
    public void removeOnCollectionEmpty(CollectionEmptyAction<TimedArrayList<E>> callback) {
        PreCon.notNull(callback);

        _onEmpty.remove(callback);
    }

    private void onEmpty() {
        if (!isEmpty() || _onEmpty.isEmpty())
            return;

        for (CollectionEmptyAction<TimedArrayList<E>> action : _onEmpty) {
            action.onEmpty(this);
        }
    }

    private void onLifespanEnd(E item) {
        for (LifespanEndAction<E> action : _onLifespanEnd) {
            action.onEnd(item);
        }
    }

    protected void scheduleRemoval(final E item, int lifespan) {
        if (lifespan < 1)
            return;

        BukkitTask task = Bukkit.getScheduler().runTaskLater(GenericsLib.getLib(), new Runnable() {

            @Override
            public void run() {
                BukkitTask task = _tasks.remove(item);
                if (task != null)
                    task.cancel();

                if (_instance.remove(item)) {
                    onLifespanEnd(item);
                }
            }

        }, lifespan);

        _tasks.put(item, task);
    }
}
