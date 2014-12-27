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


package com.jcwhatever.nucleus.collections.timed;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.CollectionEmptyAction;
import com.jcwhatever.nucleus.scheduler.ScheduledTask;
import com.jcwhatever.nucleus.utils.DateUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

/**
 * An hash map where each key value has an individual lifespan that when reached, causes the item
 * to be removed.
 *
 * <p>The lifespan can only be reset by re-adding an item.</p>
 *
 * <p>Items can be added using the default lifespan time or a lifespan can be specified per item.</p>
 */
public class TimedHashMap<K, V> implements Map<K, V>, ITimedMap<K, V>, ITimedCallbacks<K, TimedHashMap<K, V>> {

    private static Map<TimedHashMap, Void> _instances = new WeakHashMap<>(10);
    private static ScheduledTask _janitor;

    private final Map<K, V> _map;
    private final Map<K, Date> _expireMap;
    private final int _timeFactor;
    private final int _defaultTime;
    private transient final Object _sync = new Object();

    private transient List<LifespanEndAction<K>> _onLifespanEnd = new ArrayList<>(5);
    private transient List<CollectionEmptyAction<TimedHashMap<K, V>>> _onEmpty = new ArrayList<>(5);


    /**
     * Constructor. Default lifespan is 20 ticks.
     */
    public TimedHashMap() {
        this(10, 20, TimeScale.TICKS);
    }

    /**
     * Constructor. Default lifespan is 20 ticks.
     *
     * @param size  The initial capacity of the map.
     */
    public TimedHashMap(int size) {
        this(size, 20, TimeScale.TICKS);
    }

    /**
     * Constructor. Time scale is in ticks.
     *
     * @param size             The initial capacity of the map.
     * @param defaultLifespan  The default lifespan of items in ticks.
     */
    public TimedHashMap(int size, int defaultLifespan) {
        this(size, defaultLifespan, TimeScale.TICKS);
    }

    /**
     * Constructor.
     *
     * @param size             The initial capacity of the map.
     * @param defaultLifespan  The default lifespan of items in ticks.
     * @param timeScale        The lifespan time scale.
     */
    public TimedHashMap(int size, int defaultLifespan, TimeScale timeScale) {
        super();
        PreCon.positiveNumber(defaultLifespan);

        _defaultTime = defaultLifespan;
        _map = new HashMap<>(size);
        _expireMap = new HashMap<>(size);
        _instances.put(this, null);

        _timeFactor = timeScale.getTimeFactor();

        if (_janitor == null) {
            _janitor = Scheduler.runTaskRepeatAsync(Nucleus.getPlugin(), 1, 20, new Runnable() {
                @Override
                public void run() {

                    List<TimedHashMap> maps = new ArrayList<TimedHashMap>(_instances.keySet());

                    for (TimedHashMap map : maps) {
                        synchronized (map._sync) {
                            map.cleanup();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void clear() {
        _map.clear();
        _expireMap.clear();

        onEmpty();
    }

    @Override
    public Set<K> keySet() {
        return _map.keySet();
    }

    @Override
    public Collection<V> values() {
        return _map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return _map.entrySet();
    }

    /**
     * Put an item into the map using the specified lifespan.
     *
     * @param key       The item key.
     * @param value     The item to add.
     * @param lifespan  The items lifespan.
     */
    @Override
    @Nullable
    public boolean put(final K key, final V value, int lifespan) {
        PreCon.notNull(key);
        PreCon.notNull(value);
        PreCon.positiveNumber(lifespan);

        if (lifespan == 0)
            return false;

        synchronized (_sync) {

            V previous = _map.put(key, value);

            if (lifespan > 0) {
                _expireMap.put(key, getExpires(lifespan));
            }

            return previous != null;
        }
    }

    @Override
    public int size() {
        synchronized (_sync) {
            cleanup();
            return _map.size();
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (_sync) {
            cleanup();
            return _map.isEmpty();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        PreCon.notNull(key);

        synchronized (_sync) {
            return !isExpired(key, true) &&
                    _map.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        PreCon.notNull(value);

        synchronized (_sync) {
            cleanup();
            return _map.containsValue(value);
        }
    }

    @Override
    @Nullable
    public V get(Object key) {
        PreCon.notNull(key);

        synchronized (_sync) {
            if (isExpired(key, true)) {
                return null;
            }
            return _map.get(key);
        }
    }

    /**
     * Put an item into the map using the default lifespan.
     *
     * @param key    The item key.
     * @param value  The item to add.
     */
    @Override
    @Nullable
    public V put(K key, V value) {
        PreCon.notNull(key);
        PreCon.notNull(value);

        V previous = get(key);

        if (put(key, value, _defaultTime)) {
            return previous;
        }

        return null;
    }

    /**
     * Put a map of items into the map using the specified lifespan.
     *
     * @param entries   The map to add.
     * @param lifespan  The lifespan of the added items.
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> entries, int lifespan) {
        PreCon.notNull(entries);
        PreCon.positiveNumber(lifespan);

        synchronized (_sync) {

            for (Map.Entry<? extends K, ? extends V> entry : entries.entrySet()) {
                put(entry.getKey(), entry.getValue(), lifespan);
            }
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

        V value;

        synchronized (_sync) {

            value = _map.remove(key);
            if (value != null) {
                _expireMap.remove(key);
            }
        }

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

    private boolean isExpired(Date date) {
        return date.compareTo(new Date()) <= 0;
    }

    private boolean isExpired(Object entry, boolean removeIfExpired) {
        //noinspection SuspiciousMethodCalls
        Date expires = _expireMap.get(entry);
        if (expires == null)
            return true;

        if (isExpired(expires)) {
            if (removeIfExpired) {
                //noinspection SuspiciousMethodCalls
                _map.remove(entry);
                //noinspection SuspiciousMethodCalls
                _expireMap.remove(entry);
            }
            return true;
        }

        return false;
    }

    private Date getExpires(int lifespan) {
        return DateUtils.addMilliseconds(new Date(), _timeFactor * lifespan);
    }

    private void cleanup() {

        Iterator<Entry<K, Date>> iterator = _expireMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<K, Date> entry = iterator.next();
            if (isExpired(entry.getValue())) {
                iterator.remove();
                _map.remove(entry.getKey());
            }
        }
    }
}
