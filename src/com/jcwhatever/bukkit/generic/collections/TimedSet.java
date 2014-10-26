/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * A hash set where each item has its own lifespan. When an items lifespan ends,
 * it is removed from the hash set.
 *
 * <p>If a duplicate item is added, the items lifespan is reset, in addition to normal
 * hash set operations.</p>
  */
public class TimedSet<T> extends HashSet<T> {

	private static final long serialVersionUID = -5446406109300331584L;
	
	private final int _defaultTime;
	private final TimedSet<T> _instance;
	private Map<T, BukkitTask> _tasks;
    private List<LifespanEndAction<T>> _onLifespanEnd = new ArrayList<>(5);
    private List<CollectionEmptyAction<TimedSet<T>>> _onEmpty = new ArrayList<>(5);

    /**
     * Constructor. Default lifespan is 1 second.
     */
	public TimedSet() {
		super();
		_defaultTime = 20;
		_instance = this;
        _tasks = new HashMap<T, BukkitTask>(10);
	}

    /**
     * Constructor. Default lifespan is 1 second.
     *
     * @param size  The initial capacity.
     */
	public TimedSet(int size) {
		super(size);
		_defaultTime = 20;
		_instance = this;
        _tasks = new HashMap<T, BukkitTask>(30);
	}

    /**
     * Constructor.
     * @param size             The initial capacity.
     * @param defaultLifespan  The default lifespan in ticks.
     */
    public TimedSet(int size, int defaultLifespan) {
        super(size);
        PreCon.positiveNumber(defaultLifespan);

        _defaultTime = defaultLifespan;
        _instance = this;
        _tasks = new HashMap<T, BukkitTask>(30);
    }

    /**
     * Add an item to the hash set with the specified lifespan.
     *
     * <p>If the item is already present, the lifespan is reset
     * using the new lifespan value.</p>
     *
     * @param item      The item to add.
     * @param lifespan  The lifespan of the item in ticks.
     */
	public boolean add(final T item, int lifespan) {
        PreCon.notNull(item);
        PreCon.positiveNumber(lifespan);

		scheduleRemoval(item, lifespan);

		return super.add(item);
	}

    /**
     * Add an item to the hash set using the default lifespan.
     *
     * <p>If the item is already present, the lifespan is reset
     * using the default lifespan value.</p>
     *
     * @param item  The item to add.
     */
	@Override
	public boolean add(T item) {
        PreCon.notNull(item);

		return add(item, _defaultTime);
	}

    /**
     * Add items from a collection using the specified lifespan.
     *
     * <p>Any item that is already present will have its lifespan reset
     * using the specified lifespan value.</p>
     *
     * @param collection  The collection to add.
     * @param lifespan    The lifespan of the item in ticks.
     */
	public boolean addAll(Collection<? extends T> collection, int lifespan) {
        PreCon.notNull(collection);
        PreCon.positiveNumber(lifespan);

		for (T item : collection) {
			scheduleRemoval(item, lifespan);
		}
		return super.addAll(collection);
	}

    /**
     * Add items from a collection using the default lifespan.
     *
     * <p>Any item that is already present will have its lifespan reset
     * using the default lifespan value.</p>
     *
     * @param collection  The collection to add.
     */
	@Override
	public boolean addAll(Collection<? extends T> collection) {
        PreCon.notNull(collection);

		return addAll(collection, _defaultTime);
	}

    /**
     * Remove all items.
     */
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
     * Remove an item.
     *
     * @param item  The item to remove.
     */
	@Override
	public boolean remove(Object item) {
        PreCon.notNull(item);

		BukkitTask task = _tasks.remove(item);
		if (task != null) {
			task.cancel();
		}

        if (super.remove(item)) {
            onEmpty();
            return true;
        }

        return false;
	}

    /**
     * Remove all items from the collection.
     *
     * @param collection  The items to remove.
     */
	@Override
	public boolean removeAll(Collection<?> collection) {
        PreCon.notNull(collection);

		for (Object item : collection) {
			BukkitTask task = _tasks.remove(item);
			if (task != null) {
				task.cancel();
			}
		}

		if (super.removeAll(collection)) {
            onEmpty();
            return true;
        }

        return false;
	}

    /**
     * Determine if the set contains the specified item and
     * reset the items lifespan.
     *
     * @param item       The item to check.
     * @param lifespan   The new lifespan of the item.
     */
	public boolean contains(T item, int lifespan) {
        PreCon.notNull(item);
        PreCon.positiveNumber(lifespan);

        scheduleRemoval(item, lifespan);
		
		return super.contains(item);
	}

    /**
     * Add a handler to be called whenever an items lifespan ends.
     *
     * @param action  The handler to call.
     */
	public void addOnLifetimeEnd(LifespanEndAction<T> action) {
        PreCon.notNull(action);

		_onLifespanEnd.add(action);
	}

    /**
     * Remove a handler.
     *
     * @param action  The handler to remove.
     */
    public void removeOnLifespanEnd(LifespanEndAction<T> action) {
        PreCon.notNull(action);

        _onLifespanEnd.remove(action);
    }

    /**
     * Add a handler to be called whenever the collection becomes empty.
     *
     * @param action  The handler to call
     */
    public void addOnCollectionEmpty(CollectionEmptyAction<TimedSet<T>> action) {
        PreCon.notNull(action);

        _onEmpty.add(action);
    }

    /**
     * Remove a handler.
     *
     * @param action  The handler to remove.
     */
    public void removeOnCollectionEmpty(CollectionEmptyAction<TimedSet<T>> action) {
        PreCon.notNull(action);

        _onEmpty.remove(action);
    }


	protected void scheduleRemoval(final T item, int lifespan) {
		if (lifespan < 1)
			return;

        BukkitTask task = _tasks.remove(item);
        if (task != null) {
            task.cancel();
        }

		task = Bukkit.getScheduler().runTaskLater(GenericsLib.getPlugin(), new Runnable() {

			@Override
			public void run() {
				BukkitTask task = _tasks.remove(item);
				if (task != null) {
					task.cancel();
				}
				
				if (_instance.remove(item)) {
                    onLifespanEnd(item);
				}

                onEmpty();
			}

		}, lifespan);

		_tasks.put(item, task);
	}

    private void onEmpty() {
        if (!isEmpty() || _onEmpty.isEmpty())
            return;

        for (CollectionEmptyAction<TimedSet<T>> action : _onEmpty) {
            action.onEmpty(this);
        }
    }

    private void onLifespanEnd(T item) {
        for (LifespanEndAction<T> action : _onLifespanEnd) {
            action.onEnd(item);
        }
    }

}
