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
import com.jcwhatever.bukkit.generic.scheduler.ScheduledTask;
import com.jcwhatever.bukkit.generic.utils.DateUtils;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.Scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;


/**
 * An array list where each item has an individual lifespan that when reached, causes the item
 * to be removed.
 *
 * <p>The items lifespan cannot be reset except by removing it.</p>
 *
 * <p>Items can be added using the default lifespan time or a lifespan can be specified per item.</p>
 */
public class TimedArrayList<E>
        implements List<E>, ITimedList<E>, ITimedCallbacks<E, TimedArrayList<E>> {

    private static Map<TimedArrayList, Void> _instances = new WeakHashMap<>(10);
    private static ScheduledTask _janitor;

    private final List<Entry<E>> _list;
    private final int _timeFactor;
    private final int _defaultTime;
    private final Object _sync = new Object();

    private List<LifespanEndAction<E>> _onLifespanEnd = new ArrayList<>(5);
    private List<CollectionEmptyAction<TimedArrayList<E>>> _onEmpty = new ArrayList<>(5);

    /**
     * Constructor. Default item lifespan is 20 ticks.
     */
    public TimedArrayList() {
        this(10, 20, TimeScale.TICKS);
    }

    /**
     * Constructor. Default item lifespan is 20 ticks.
     *
     * @param size  The initial capacity of the list.
     */
    public TimedArrayList(int size) {
        this(size, 20, TimeScale.TICKS);
    }

    /**
     * Constructor. Specify default item lifespan in ticks.
     *
     * @param size         The initial capacity of the list.
     * @param defaultTime  The default lifespan of items.
     */
    public TimedArrayList(int size, int defaultTime) {
        this(size, defaultTime, TimeScale.TICKS);
    }

    /**
     * Constructor. Specify default item lifespan and time scale.
     *
     * @param size         The initial capacity of the list.
     * @param defaultTime  The default lifespan of items.
     * @param timeScale    The lifespan time scale.
     */
    public TimedArrayList(int size, int defaultTime, TimeScale timeScale) {
        PreCon.positiveNumber(defaultTime);
        PreCon.notNull(timeScale);

        _defaultTime = defaultTime;
        _list = new ArrayList<>(size);
        _instances.put(this, null);

        _timeFactor = timeScale.getTimeFactor();

        if (_janitor == null) {
            _janitor = Scheduler.runTaskRepeatAsync(GenericsLib.getPlugin(), 1, 20, new Runnable() {
                @Override
                public void run() {

                    List<TimedArrayList> lists = new ArrayList<TimedArrayList>(_instances.keySet());

                    for (TimedArrayList list : lists) {

                        synchronized (list._sync) {
                            list.cleanup();
                        }
                    }
                }
            });
        }
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

        Entry<E> entry = new Entry<>(item, getExpires(lifespan));

        synchronized (_sync) {
            return _list.add(entry);
        }
    }

    @Override
    public int size() {
        synchronized (_sync) {
            cleanup();
            return _list.size();
        }
    }

    @Override
    public boolean isEmpty() {
        synchronized (_sync) {
            cleanup();
            return _list.isEmpty();
        }
    }

    @Override
    public boolean contains(Object o) {
        synchronized (_sync) {
            Iterator<Entry<E>> iterator = _list.iterator();
            while (iterator.hasNext()) {
                Entry entry = iterator.next();

                if (isExpired(entry.expires)) {
                    iterator.remove();
                    continue;
                }

                if (o.equals(entry.item))
                    return true;
            }
            return false;
        }
    }

    @Override
    public Iterator<E> iterator() {

        final Iterator<Entry<E>> iterator;

        synchronized (_sync) {
            iterator = _list.iterator();
        }

        return new Iterator<E>() {

            Entry<E> peek;

            @Override
            public boolean hasNext() {

                synchronized (_sync) {

                    if (!iterator.hasNext())
                        return false;

                    while (iterator.hasNext()) {
                        peek = iterator.next();
                        if (isExpired(peek.expires)) {
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
                        Entry<E> n = peek;
                        peek = null;
                        return n.item;
                    }
                    return iterator.next().item;
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

            Object[] array = new Object[_list.size()];

            for (int i = 0; i < array.length; i++) {
                array[i] = _list.get(i).item;
            }

            return array;
        }
    }

    @Override
    public <T> T[] toArray(T[] array) {
        synchronized (_sync) {
            cleanup();

            for (int i = 0; i < array.length; i++) {

                @SuppressWarnings("unchecked")
                T item = (T) _list.get(i).item;

                array[i] = item;
            }

            return array;
        }
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

        Entry<E> entry = new Entry<>(item, getExpires(lifespan));

        synchronized (_sync) {
            _list.add(index, entry);
        }
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

        synchronized (_sync) {
            boolean isChanged = false;

            for (E item : collection) {
                isChanged = isChanged | add(item, lifespan);
            }
            return isChanged;
        }
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

        List<Entry<E>> list = new ArrayList<>(collection.size());

        for (E item : collection) {
            list.add(new Entry<E>(item, getExpires(lifespan)));
        }
        synchronized (_sync) {
            return _list.addAll(index, list);
        }
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
        synchronized (_sync) {
            _list.clear();
        }

        onEmpty();
    }

    @Override
    public E get(int index) {

        synchronized (_sync) {
            Entry<E> entry = _list.get(index);
            return entry.item;
        }
    }

    @Override
    @Nullable
    public E set(int index, E element) {
        synchronized (_sync) {
            Entry<E> previous = _list.set(index, new Entry<E>(element, getExpires(_defaultTime)));
            if (previous == null)
                return null;

            return previous.item;
        }
    }

    @Override
    public boolean remove(Object item) {
        PreCon.notNull(item);

        synchronized (_sync) {
            //noinspection unchecked
            return _list.remove(new Entry(item, new Date()));
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        PreCon.notNull(c);

        synchronized (_sync) {
            for (Object obj : c) {
                if (!contains(obj))
                    return false;
            }

            return true;
        }
    }

    @Override
    @Nullable
    public E remove(int index) {
        PreCon.positiveNumber(index);

        synchronized (_sync) {
            Entry<E> previous = _list.remove(index);
            if (previous == null)
                return null;

            return previous.item;
        }
    }

    @Override
    public int indexOf(Object o) {
        PreCon.notNull(o);

        synchronized (_sync) {
            int i = 0;
            Iterator<Entry<E>> iterator = _list.iterator();
            while (iterator.hasNext()) {
                Entry entry = iterator.next();

                if (isExpired(entry.expires)) {
                    iterator.remove();
                    i--;
                } else if (o.equals(entry.item)) {
                    return i;
                }

                i++;
            }
            return -1;
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        PreCon.notNull(o);

        synchronized (_sync) {
            for (int i = _list.size() - 1; i >= 0; i--) {
                Entry<E> entry = _list.get(i);
                if (isExpired(entry.expires))
                    continue;

                if (entry.item.equals(o))
                    return i;
            }

            return -1;
        }
    }

    @Override
    public ListIterator<E> listIterator() {
        return new TimedListIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new TimedListIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        synchronized (_sync) {
            List<Entry<E>> sle = _list.subList(fromIndex, toIndex);
            List<E> result = new ArrayList<>(sle.size());
            for (Entry<E> entry : sle) {
                result.add(entry.item);
            }
            return result;
        }
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        PreCon.notNull(collection);

        boolean isChanged = false;

        synchronized (_sync) {
            for (Object item : collection) {
                isChanged = isChanged | remove(item);
            }
        }

        onEmpty();

        return isChanged;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        PreCon.notNull(c);

        ListIterator<E> iterator = listIterator();

        boolean isChanged = false;

        synchronized (_sync) {
            while (iterator.hasNext()) {
                E item = iterator.next();

                if (c.contains(item)) {
                    iterator.set(item);
                } else {
                    iterator.remove();
                    isChanged = true;
                }
            }
        }

        return isChanged;
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

    private boolean isExpired(Date date) {
        return date.compareTo(new Date()) <= 0;
    }

    private Date getExpires(int lifespan) {
        return DateUtils.addMilliseconds(new Date(), _timeFactor * lifespan);
    }

    private void cleanup() {

        int size = _list.size();

        // remove backwards to reduce the amount of
        // element shifting
        for (int i = size - 1; i >= 0; i--) {
            Entry<E> entry = _list.get(i);
            if (isExpired(entry.expires)) {
                _list.remove(i);
            }
        }
    }

    static class Entry<T> {
        final T item;
        final Date expires;

        Entry(T item, Date expires) {
            this.item = item;
            this.expires = expires;
        }

        @Override
        public int hashCode() {
            return item.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return item.equals(obj);
        }
    }

    class TimedListIterator implements ListIterator<E> {

        int index;

        TimedListIterator(int index) {
            this.index = index;
        }

        @Override
        public boolean hasNext() {
            return index < _list.size();
        }

        @Override
        public E next() {
            Entry<E> entry = _list.get(index);
            index++;

            return entry.item;
        }

        @Override
        public boolean hasPrevious() {
            return index >= 0;
        }

        @Override
        public E previous() {
            index--;
            return _list.get(index).item;
        }

        @Override
        public int nextIndex() {
            return index+1;
        }

        @Override
        public int previousIndex() {
            return index-1;
        }

        @Override
        public void remove() {
            _list.remove(index);
        }

        @Override
        public void set(E e) {
            _list.set(index, new Entry<E>(e, getExpires(_defaultTime)));
        }

        @Override
        public void add(E e) {
            _list.add(new Entry<E>(e, getExpires(_defaultTime)));
        }
    }
}
