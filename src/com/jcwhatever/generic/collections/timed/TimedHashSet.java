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


package com.jcwhatever.generic.collections.timed;

import com.jcwhatever.generic.GenericsLib;
import com.jcwhatever.generic.collections.CollectionEmptyAction;
import com.jcwhatever.generic.scheduler.ScheduledTask;
import com.jcwhatever.generic.utils.DateUtils;
import com.jcwhatever.generic.utils.PreCon;
import com.jcwhatever.generic.utils.Scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * A hash set where each item has its own lifespan. When an items lifespan ends,
 * it is removed from the hash set.
 *
 * <p>If a duplicate item is added, the items lifespan is reset, in addition to normal
 * hash set operations.</p>
 */
public class TimedHashSet<E> implements Set<E>, ITimedCollection<E>, ITimedCallbacks<E, TimedHashSet<E>> {

    private static Map<TimedHashSet, Void> _instances = new WeakHashMap<>(10);
    private static ScheduledTask _janitor;

    private final int _defaultTime;
    private final Map<E, Date> _expireMap;
    private final int _timeFactor;

    private transient final Object _sync = new Object();

    private transient List<LifespanEndAction<E>> _onLifespanEnd = new ArrayList<>(5);
    private transient List<CollectionEmptyAction<TimedHashSet<E>>> _onEmpty = new ArrayList<>(5);

    /**
     * Constructor. Default lifespan is 20 ticks.
     */
    public TimedHashSet() {
        this(10, 20, TimeScale.TICKS);
    }

    /**
     * Constructor. Default lifespan is 20 ticks.
     *
     * @param size  The initial capacity.
     */
    public TimedHashSet(int size) {
        this(size, 20, TimeScale.TICKS);
    }

    /**
     * Constructor. Default lifespan is 20 ticks.
     *
     * @param size             The initial capacity.
     * @param defaultLifespan  The default lifespan.
     */
    public TimedHashSet(int size, int defaultLifespan) {
        this(size, defaultLifespan, TimeScale.TICKS);
    }

    /**
     * Constructor.
     *
     * @param size             The initial capacity.
     * @param defaultLifespan  The default lifespan.
     * @param timeScale        The lifespan time scale.
     */
    public TimedHashSet(int size, int defaultLifespan, TimeScale timeScale) {
        PreCon.positiveNumber(defaultLifespan);
        PreCon.notNull(timeScale);

        _defaultTime = defaultLifespan;
        _expireMap = new HashMap<>(size);
        _instances.put(this, null);

        _timeFactor = timeScale.getTimeFactor();

        if (_janitor == null) {
            _janitor = Scheduler.runTaskRepeatAsync(GenericsLib.getPlugin(), 1, 20, new Runnable() {
                @Override
                public void run() {

                    List<TimedHashSet> sets = new ArrayList<TimedHashSet>(_instances.keySet());

                    for (TimedHashSet set : sets) {

                        synchronized (set._sync) {
                            set.cleanup();
                        }
                    }
                }
            });
        }
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
    @Override
    public boolean add(final E item, int lifespan) {
        PreCon.notNull(item);
        PreCon.positiveNumber(lifespan);

        synchronized (_sync) {
            Date expires = getExpires(lifespan);

            _expireMap.put(item, expires);

            return true;
        }
    }

    @Override
    public int size() {
        synchronized (_sync) {
            cleanup();
            return _expireMap.size();
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (_sync) {
            cleanup();
            return _expireMap.isEmpty();
        }
    }

    @Override
    public boolean contains(Object o) {
        PreCon.notNull(o);

        synchronized (_sync) {

            //noinspection SuspiciousMethodCalls
            Date date = _expireMap.get(o);
            if (date == null)
                return false;

            if (isExpired(date)) {
                //noinspection SuspiciousMethodCalls
                _expireMap.remove(o);
                return false;
            }

            return true;
        }
    }

    /**
     * Determine if the set contains the specified item and
     * reset the items lifespan.
     *
     * @param item       The item to check.
     * @param lifespan   The new lifespan of the item.
     */
    public boolean contains(E item, int lifespan) {
        PreCon.notNull(item);
        PreCon.positiveNumber(lifespan);

        synchronized (_sync) {
            boolean isExpired = isExpired(item, true);
            if (isExpired)
                return false;

            Date expires = _expireMap.get(item);
            if (expires == null)
                return false;

            if (isExpired(expires)) {
                _expireMap.remove(item);
                return false;
            }

            _expireMap.put(item, getExpires(lifespan));

            return true;
        }
    }

    @Override
    public Iterator<E> iterator() {
        final Iterator<E> iterator;

        synchronized (_sync) {
            iterator = _expireMap.keySet().iterator();
        }

        return new Iterator<E>() {
            E peek;

            @Override
            public boolean hasNext() {

                synchronized (_sync) {

                    if (!iterator.hasNext())
                        return false;

                    while (iterator.hasNext()) {
                        peek = iterator.next();
                        if (isExpired(peek, false)) {
                            iterator.remove();
                        }
                        else {
                            return true;
                        }
                    }

                    return false;
                }
            }

            @Override
            public E next() {
                synchronized (_sync) {
                    if (peek != null) {
                        E n = peek;
                        peek = null;
                        return n;
                    }
                    return iterator.next();
                }
            }

            @Override
            public void remove() {
                synchronized (_sync) {
                    iterator().remove();
                }
            }
        };
    }

    @Override
    public Object[] toArray() {
        synchronized (_sync) {
            cleanup();
            return _expireMap.entrySet().toArray();
        }
    }

    @Override
    public <T> T[] toArray(T[] a) {
        PreCon.notNull(a);

        synchronized (_sync) {
            cleanup();
            //noinspection SuspiciousToArrayCall
            return _expireMap.entrySet().toArray(a);
        }
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
    public boolean add(E item) {
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
    @Override
    public boolean addAll(Collection<? extends E> collection, int lifespan) {
        PreCon.notNull(collection);
        PreCon.positiveNumber(lifespan);

        synchronized (_sync) {
            for (E item : collection) {
                _expireMap.put(item, getExpires(lifespan));
            }
        }

        return true;
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
    public boolean addAll(Collection<? extends E> collection) {
        PreCon.notNull(collection);

        return addAll(collection, _defaultTime);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        PreCon.notNull(c);

        synchronized (_sync) {
            return _expireMap.keySet().retainAll(c);
        }
    }

    /**
     * Remove all items.
     */
    @Override
    public void clear() {
        synchronized (_sync) {
            _expireMap.clear();
        }

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

        synchronized (_sync) {
            Date expires = _expireMap.remove(item);
            if (expires == null)
                return false;
        }

        onEmpty();
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        PreCon.notNull(c);

        synchronized (_sync) {
            return _expireMap.keySet().containsAll(c);
        }
    }

    /**
     * Remove all items from the collection.
     *
     * @param collection  The items to remove.
     */
    @Override
    public boolean removeAll(Collection<?> collection) {
        PreCon.notNull(collection);

        boolean isChanged = false;

        synchronized (_sync) {
            for (Object item : collection) {
                //noinspection SuspiciousMethodCalls
                Date expires = _expireMap.remove(item);
                if (expires != null)
                    isChanged = true;
            }

            if (isChanged) {
                onEmpty();
                return true;
            }

            return false;
        }
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
    public void addOnCollectionEmpty(CollectionEmptyAction<TimedHashSet<E>> callback) {
        PreCon.notNull(callback);

        _onEmpty.add(callback);
    }

    /**
     * Remove a handler.
     *
     * @param callback  The handler to remove.
     */
    @Override
    public void removeOnCollectionEmpty(CollectionEmptyAction<TimedHashSet<E>> callback) {
        PreCon.notNull(callback);

        _onEmpty.remove(callback);
    }

    private void onEmpty() {
        if (!isEmpty() || _onEmpty.isEmpty())
            return;

        for (CollectionEmptyAction<TimedHashSet<E>> action : _onEmpty) {
            action.onEmpty(this);
        }
    }

    private void onLifespanEnd(E item) {
        for (LifespanEndAction<E> action : _onLifespanEnd) {
            action.onEnd(item);
        }
    }

    private boolean isExpired(Date date) {
        return date.compareTo(new Date()) <= 0;
    }

    private boolean isExpired(E entry, boolean removeIfExpired) {
        Date expires = _expireMap.get(entry);
        if (expires == null)
            return true;

        if (isExpired(expires)) {
            if (removeIfExpired) {
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
        Iterator<Entry<E, Date>> iterator = _expireMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<E, Date> entry = iterator.next();
            if (isExpired(entry.getValue())) {
                iterator.remove();
            }
        }
    }
}
