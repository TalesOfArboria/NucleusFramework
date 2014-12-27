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

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
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
 * A {@code HashSetMap} where each key value has an individual lifespan that when reached, causes the item
 * to be removed.
 *
 * <p>The lifespan can only be reset by re-adding an item.</p>
 *
 * <p>Items can be added using the default lifespan time or a lifespan can be specified per item.</p>
 */
public class TimedMultimap<K, V> implements Multimap<K, V>, ITimedMap<K, V>, ITimedCallbacks<K, TimedMultimap<K, V>> {

    private static Map<TimedMultimap, Void> _instances = new WeakHashMap<>(10);
    private static ScheduledTask _janitor;

    private final Multimap<K, V> _multiMap;
    private final Map<K, Date> _expireMap;
    private final int _timeFactor;
    private final int _defaultTime;
    private transient final Object _sync = new Object();

    private transient List<LifespanEndAction<K>> _onLifespanEnd = new ArrayList<>(5);
    private transient List<CollectionEmptyAction<TimedMultimap<K, V>>> _onEmpty = new ArrayList<>(5);

    /**
     * Constructor. Default lifespan is 20 ticks.
     */
    public TimedMultimap(Multimap<K, V> multiMap) {
        this(multiMap, 20, TimeScale.TICKS);
    }

    /**
     * Constructor.
     *
     * @param defaultLifespan  The lifespan used when one is not specified.
     * @param timeScale        The lifespan timescale.
     */
    public TimedMultimap(Multimap<K, V> multiMap, int defaultLifespan, TimeScale timeScale) {

        _defaultTime = defaultLifespan;
        _multiMap = multiMap;
        _expireMap = new HashMap<>(multiMap.keySet().size() + 20);
        _instances.put(this, null);

        _timeFactor = timeScale.getTimeFactor();

        if (_janitor == null) {
            _janitor = Scheduler.runTaskRepeatAsync(Nucleus.getPlugin(), 1, 20, new Runnable() {
                @Override
                public void run() {

                    List<TimedMultimap> maps = new ArrayList<>(_instances.keySet());

                    for (TimedMultimap map : maps) {
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
        _multiMap.clear();
        _expireMap.clear();

        onEmpty();
    }

    /**
     * Put an item into the map using the specified lifespan.
     *
     * @param key       The item key.
     * @param value     The item to add.
     * @param lifespan  The items lifespan.
     */
    @Override
    public boolean put(final K key, final V value, int lifespan) {
        PreCon.notNull(key);
        PreCon.notNull(value);
        PreCon.positiveNumber(lifespan);

        if (lifespan == 0)
            return false;

        synchronized (_sync) {

            if (_multiMap.put(key, value)) {

                if (lifespan > 0) {
                    _expireMap.put(key, getExpires(lifespan));
                }
                return true;
            }
            return false;
        }
    }

    @Override
    public int size() {
        synchronized (_sync) {
            cleanup();
            return _multiMap.size();
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (_sync) {
            cleanup();
            return _multiMap.isEmpty();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        PreCon.notNull(key);

        synchronized (_sync) {
            return !isExpired(key, true) &&
                    _multiMap.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        PreCon.notNull(value);

        synchronized (_sync) {
            cleanup();
            return _multiMap.containsValue(value);
        }
    }

    @Override
    public boolean containsEntry(@Nullable Object o, @Nullable Object o1) {
        return _multiMap.containsEntry(o, o1);
    }

    @Override
    @Nullable
    public Collection<V> get(K key) {
        PreCon.notNull(key);

        synchronized (_sync) {
            if (isExpired(key, true)) {
                return new ArrayList<>(0);
            }
            return _multiMap.get(key);
        }
    }

    @Override
    public Set<K> keySet() {
        return _multiMap.keySet();
    }

    @Override
    public Multiset<K> keys() {
        return _multiMap.keys();
    }

    @Override
    public Collection<V> values() {
        return _multiMap.values();
    }

    @Override
    public Collection<Map.Entry<K, V>> entries() {
        return _multiMap.entries();
    }

    @Override
    public Map<K, Collection<V>> asMap() {
        return _multiMap.asMap();
    }

    /**
     * Put an item into the map using the default lifespan.
     *
     * @param key    The item key.
     * @param value  The item to add.
     */
    @Override
    @Nullable
    public boolean put(K key, V value) {
        PreCon.notNull(key);
        PreCon.notNull(value);

        return put(key, value, _defaultTime);
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
    public boolean putAll(Multimap<? extends K, ? extends V> entries) {
        PreCon.notNull(entries);

        boolean isChanged = false;

        for (Map.Entry<? extends K, ? extends V> entry : entries.entries()) {
            isChanged = isChanged || put(entry.getKey(), entry.getValue());
        }

        return isChanged;
    }

    @Override
    public Collection<V> replaceValues(@Nullable K k, Iterable<? extends V> iterable) {
        return _multiMap.replaceValues(k, iterable);
    }

    @Override
    public Collection<V> removeAll(@Nullable Object o) {
        Collection<V> result = _multiMap.removeAll(o);
        //noinspection SuspiciousMethodCalls
        _expireMap.remove(o);

        return result;
    }

    @Override
    public boolean remove(Object key, Object value) {
        PreCon.notNull(key);

        synchronized (_sync) {

            if (_multiMap.remove(key, value)) {
                //noinspection SuspiciousMethodCalls
                _expireMap.remove(key);
                onEmpty();
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean putAll(@Nullable K k, Iterable<? extends V> iterable) {
        if (iterable == null)
            return false;

        boolean isChanged = false;

        for (V element : iterable) {
            isChanged = isChanged || put(k, element);
        }

        return isChanged;
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
    public void addOnCollectionEmpty(CollectionEmptyAction<TimedMultimap<K, V>> callback) {
        PreCon.notNull(callback);

        _onEmpty.add(callback);
    }

    /**
     * Remove a handler.
     *
     * @param callback  The handler to remove.
     */
    @Override
    public void removeOnCollectionEmpty(CollectionEmptyAction<TimedMultimap<K, V>> callback) {
        PreCon.notNull(callback);

        _onEmpty.remove(callback);
    }

    private void onEmpty() {
        if (!isEmpty() || _onEmpty.isEmpty())
            return;

        for (CollectionEmptyAction<TimedMultimap<K, V>> action : _onEmpty) {
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

    private boolean isExpired(Object key, boolean removeIfExpired) {
        //noinspection SuspiciousMethodCalls
        Date expires = _expireMap.get(key);
        if (expires == null)
            return true;

        if (isExpired(expires)) {
            if (removeIfExpired) {
                //noinspection SuspiciousMethodCalls
                _multiMap.removeAll(key);
                //noinspection SuspiciousMethodCalls
                _expireMap.remove(key);
            }
            return true;
        }

        return false;
    }

    private Date getExpires(int lifespan) {
        return DateUtils.addMilliseconds(new Date(), _timeFactor * lifespan);
    }

    private void cleanup() {

        Iterator<Map.Entry<K, Date>> iterator = _expireMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<K, Date> entry = iterator.next();
            if (isExpired(entry.getValue())) {
                iterator.remove();
                _multiMap.removeAll(entry.getKey());
            }
        }
    }
}
