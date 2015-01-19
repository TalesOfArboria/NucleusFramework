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
import com.jcwhatever.nucleus.collections.wrap.ConversionListWrapper;
import com.jcwhatever.nucleus.collections.wrap.ConversionListIteratorWrapper;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.utils.TimeScale;
import com.jcwhatever.nucleus.utils.scheduler.ScheduledTask;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Rand;
import com.jcwhatever.nucleus.utils.Scheduler;
import com.jcwhatever.nucleus.utils.observer.update.IUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.update.NamedUpdateAgents;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;
import javax.annotation.Nullable;


/**
 * An array list where each item has an individual lifespan that when reached, causes
 * the item to be removed.
 *
 * <p>The items lifespan cannot be reset except by removing it.</p>
 *
 * <p>Items can be added using the default lifespan or a lifespan can be specified
 * per item.</p>
 *
 * <p>Subscribers that are added to track when an item expires or when the collection is
 * empty will have a varying degree of resolution up to 10 ticks, meaning the subscriber
 * may be notified up to 10 ticks after an element expires (but not before).</p>
 *
 * <p>Getter operations cease to return an element within approximately 50 milliseconds
 * (1 tick) of expiring.</p>
 *
 * <p>Note that the indexing operations of the list should be used with care or avoided.
 * It is possible to check the size of the list then use an indexing operation and have
 * a difference between the size when checked and the size when the indexing operation
 * is performed. The list will not remove an expired item if retrieved through an
 * indexing operation, however the janitor (removes expired items at interval) may remove
 * an element between operations from a different thread.</p>
 *
 * <p>Thread safe.</p>
 */
public class TimedArrayList<E> implements List<E>, IPluginOwned {

    // The minimum interval the cleanup is allowed to run at.
    // Used to prevent cleanup from being run too often.
    private static final int MIN_CLEANUP_INTERVAL_MS = 50;

    // The interval the janitor runs at
    private static final int JANITOR_INTERVAL_TICKS = 10;

    // random initial delay interval for janitor, random to help spread out
    // task execution in relation to other scheduled tasks
    private static final int JANITOR_INITIAL_DELAY_TICKS = Rand.getInt(1, 9);

    private final static Map<TimedArrayList, Void> _instances = new WeakHashMap<>(10);
    private static ScheduledTask _janitor;

    private final Plugin _plugin;
    private final List<Element<E>> _list;

    private final int _lifespan; // milliseconds
    private final TimeScale _timeScale;

    private final transient Object _sync = new Object();
    private transient long _nextCleanup;

    private final transient NamedUpdateAgents _agents = new NamedUpdateAgents();

    /**
     * Constructor. Default item lifespan is 20 ticks.
     */
    public TimedArrayList(Plugin plugin) {
        this(plugin, 10, 20, TimeScale.TICKS);
    }

    /**
     * Constructor. Default item lifespan is 20 ticks.
     *
     * @param size  The initial capacity of the list.
     */
    public TimedArrayList(Plugin plugin, int size) {
        this(plugin, size, 20, TimeScale.TICKS);
    }

    /**
     * Constructor. Specify default item lifespan in ticks.
     *
     * @param size         The initial capacity of the list.
     * @param defaultTime  The default lifespan of items.
     */
    public TimedArrayList(Plugin plugin, int size, int defaultTime) {
        this(plugin, size, defaultTime, TimeScale.TICKS);
    }

    /**
     * Constructor. Specify default item lifespan and time scale.
     *
     * @param size         The initial capacity of the list.
     * @param defaultTime  The default lifespan of items.
     * @param timeScale    The lifespan time scale.
     */
    public TimedArrayList(Plugin plugin, int size, int defaultTime, TimeScale timeScale) {
        PreCon.notNull(plugin);
        PreCon.positiveNumber(defaultTime);
        PreCon.notNull(timeScale);

        _plugin = plugin;
        _lifespan = defaultTime * timeScale.getTimeFactor();
        _timeScale = timeScale;
        _list = new ArrayList<>(size);

        synchronized (_instances) {
            _instances.put(this, null);
        }

        startJanitor();
    }

    /**
     * Add an item to the list and specify its lifetime using
     * the time scale specified in the constructor.
     *
     * @param item       The item to add.
     * @param lifespan   The amount of time the element will stay in the list.
     */
    public boolean add(final E item, int lifespan) {
        return add(item, lifespan, _timeScale);
    }

    /**
     * Add an item to the list and specify its lifetime using the
     * specified time scale.
     *
     * @param item       The item to add.
     * @param lifespan   The amount of time the element will stay in the list.
     * @param timeScale  The time scale of the specified lifespan.
     */
    public boolean add(final E item, int lifespan, TimeScale timeScale) {
        PreCon.notNull(item);
        PreCon.positiveNumber(lifespan);
        PreCon.notNull(timeScale);

        Element<E> entry = new Element<>(item, lifespan, timeScale);

        synchronized (_sync) {
            return _list.add(entry);
        }
    }

    /**
     * Insert an item into the list at the specified index
     * and specify its lifespan using the time scale specified
     * in the constructor.
     *
     * @param index     The index position to insert at.
     * @param item      The item to insert.
     * @param lifespan  The amount of time in the element will stay in the list.
     */
    public void add(int index, E item, int lifespan) {
        add(index, item, lifespan, _timeScale);
    }

    /**
     * Insert an item into the list at the specified index
     * and specify its lifespan using the the time scale specified.
     *
     * @param index      The index position to insert at.
     * @param item       The item to insert.
     * @param lifespan   The amount of time in the element will stay in the list.
     * @param timeScale  The time scale of the specified lifespan.
     */
    public void add(int index, E item, int lifespan, TimeScale timeScale) {
        PreCon.positiveNumber(index);
        PreCon.notNull(item);
        PreCon.positiveNumber(lifespan);
        PreCon.notNull(timeScale);

        Element<E> entry = new Element<>(item, lifespan, timeScale);

        synchronized (_sync) {
            _list.add(index, entry);
        }
    }

    /**
     * Add a collection to the list and specify the lifespan using the
     * time scale specified in the constructor.
     *
     * @param collection  The collection to add.
     * @param lifespan    The amount of time in ticks it will stay in the list.
     */
    public boolean addAll(Collection<? extends E> collection, int lifespan) {
        return addAll(collection, lifespan, _timeScale);
    }

    /**
     * Add a collection to the list and specify the lifespan using the
     * specified time scale.
     *
     * @param collection  The collection to add.
     * @param lifespan    The amount of time in the element will stay in the list.
     * @param timeScale   The time scale of the specified lifespan.
     */
    public boolean addAll(Collection<? extends E> collection, int lifespan, TimeScale timeScale) {
        PreCon.notNull(collection);
        PreCon.positiveNumber(lifespan);
        PreCon.notNull(timeScale);

        boolean isChanged = false;

        for (E item : collection) {
            isChanged = add(item, lifespan, timeScale) || isChanged;
        }
        return isChanged;
    }

    /**
     * Insert a collection into the list at the specified index
     * and specify the lifespan using the time scale specified
     * in the constructor.
     *
     * @param index       The index position to insert at.
     * @param collection  The collection to add.
     * @param lifespan    The amount of time in the element will stay in the list.
     */
    public boolean addAll(int index, Collection<? extends E> collection, int lifespan) {
        return addAll(index, collection, lifespan, _timeScale);
    }

    /**
     * Insert a collection into the list at the specified index
     * and specify the lifespan using the specified time scale.
     *
     * @param index       The index position to insert at.
     * @param collection  The collection to add.
     * @param lifespan    The amount of time in ticks it will stay in the list.
     * @param timeScale   The time scale of the specified lifespan.
     */
    public boolean addAll(int index, Collection<? extends E> collection, int lifespan, TimeScale timeScale) {
        PreCon.positiveNumber(index);
        PreCon.notNull(collection);
        PreCon.positiveNumber(lifespan);
        PreCon.notNull(timeScale);

        List<Element<E>> list = new ArrayList<>(collection.size());

        for (E item : collection) {
            list.add(new Element<E>(item, lifespan, timeScale));
        }
        synchronized (_sync) {
            return _list.addAll(index, list);
        }
    }

    /**
     * Subscribe to updates called when an elements lifespan ends.
     *
     * @param subscriber  The lifespan end subscriber.
     *
     * @return  Self for chaining.
     */
    public TimedArrayList<E> onLifespanEnd(IUpdateSubscriber<E> subscriber) {
        PreCon.notNull(subscriber);

        _agents.getAgent("onLifespanEnd").register(subscriber);

        return this;
    }

    /**
     * Subscribe to updates called when the collection is empty.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    public TimedArrayList<E> onEmpty(IUpdateSubscriber<TimedArrayList<E>> subscriber) {
        PreCon.notNull(subscriber);

        _agents.getAgent("onEmpty").register(subscriber);

        return this;
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
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
            Iterator<Element<E>> iterator = _list.iterator();
            while (iterator.hasNext()) {
                Element<E> entry = iterator.next();

                if (entry.isExpired()) {
                    iterator.remove();
                    onLifespanEnd(entry.element);
                    continue;
                }

                if (o == null && entry.element == null)
                    return true;

                if (o != null && o.equals(entry.element))
                    return true;
            }
            return false;
        }
    }

    @Override
    public Iterator<E> iterator() {

        final Iterator<Element<E>> iterator;

        synchronized (_sync) {
            iterator = _list.iterator();
        }

        return new Iterator<E>() {

            Element<E> peek;
            boolean invokedHasNext;
            boolean invokedNext;

            @Override
            public boolean hasNext() {

                invokedHasNext = true;

                synchronized (_sync) {

                    if (!iterator.hasNext())
                        return false;

                    while (iterator.hasNext()) {

                        peek = iterator.next();

                        if (peek.isExpired()) {
                            iterator.remove();
                            onLifespanEnd(peek.element);
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

                if (!invokedHasNext)
                    throw new IllegalStateException("Cannot invoke 'next' until 'hasNext' has been invoked.");

                invokedHasNext = false;
                invokedNext = true;

                synchronized (_sync) {

                    if (peek == null)
                        hasNext();

                    if (peek == null)
                        throw new NoSuchElementException();


                    Element<E> n = peek;
                    peek = null;
                    return n.element;
                }
            }

            @Override
            public void remove() {

                if (!invokedNext)
                    throw new IllegalStateException("Cannot 'remove' until 'next' method is invoked.");

                invokedNext =false;

                synchronized (_sync) {
                    iterator.remove();
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
                array[i] = _list.get(i).element;
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
                T item = (T) _list.get(i).element;

                array[i] = item;
            }

            return array;
        }
    }

    @Override
    public boolean add(E item) {
        return add(item, _lifespan, TimeScale.MILLISECONDS);
    }

    @Override
    public void add(int index, E item) {
        PreCon.positiveNumber(index);
        PreCon.notNull(item);

        add(index, item, _lifespan, TimeScale.MILLISECONDS);
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        PreCon.notNull(collection);

        return addAll(collection, _lifespan, TimeScale.MILLISECONDS);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> collection) {
        PreCon.positiveNumber(index);
        PreCon.notNull(collection);

        return addAll(index, collection, _lifespan, TimeScale.MILLISECONDS);
    }

    @Override
    public void clear() {
        synchronized (_sync) {
            _list.clear();
        }
    }

    @Override
    public E get(int index) {

        synchronized (_sync) {
            Element<E> entry = _list.get(index);
            return entry.element;
        }
    }

    @Override
    @Nullable
    public E set(int index, E element) {
        synchronized (_sync) {
            Element<E> previous = _list.set(index, new Element<E>(element, _lifespan, TimeScale.MILLISECONDS));
            if (previous == null)
                return null;

            return previous.element;
        }
    }

    @Override
    public boolean remove(Object item) {
        PreCon.notNull(item);

        synchronized (_sync) {
            //noinspection unchecked
            return _list.remove(new Element(item));
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        PreCon.notNull(c);

        synchronized (_sync) {

            cleanup();

            for (Object obj : c) {
                if (!_list.contains(new Element<E>(obj)))
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

            Element<E> previous = _list.remove(index);
            if (previous == null)
                return null;

            return previous.element;
        }
    }

    @Override
    public int indexOf(Object o) {
        PreCon.notNull(o);

        synchronized (_sync) {
            int i = 0;
            Iterator<Element<E>> iterator = _list.iterator();
            while (iterator.hasNext()) {

                Element<E> entry = iterator.next();

                if (entry.isExpired()) {
                    iterator.remove();
                    onLifespanEnd(entry.element);
                    i--;
                } else if (o.equals(entry.element)) {
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
                Element<E> entry = _list.get(i);
                if (entry.isExpired())
                    continue;

                if (entry.element.equals(o))
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

            final List<Element<E>> subList = _list.subList(fromIndex, toIndex);

            return new ConversionListWrapper<E, Element<E>>() {
                @Override
                protected List<Element<E>> list() {
                    return subList;
                }

                @Override
                protected E convert(Element<E> internal) {
                    return internal.element;
                }

                @Override
                protected Element<E> unconvert(Object external) {

                    @SuppressWarnings("unchecked")
                    E element = (E)external;

                    return new Element<E>(element, _lifespan, _timeScale);
                }
            };
        }
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        PreCon.notNull(collection);

        boolean isChanged = false;

        synchronized (_sync) {
            for (Object item : collection) {
                isChanged = remove(item) || isChanged;
            }
        }
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

    private void onLifespanEnd(E item) {
        if (_agents.hasAgent("onLifespanEnd")) {
            _agents.getAgent("onLifespanEnd").update(item);
        }

        if (isEmpty() && _agents.hasAgent("onEmpty")) {
            _agents.getAgent("onEmpty").update(this);
        }
    }

    private void cleanup() {

        if (_list.isEmpty())
            return;

        // prevent cleanup from running too often
        if (_nextCleanup > System.currentTimeMillis())
            return;

        _nextCleanup = System.currentTimeMillis() + MIN_CLEANUP_INTERVAL_MS;

        int size = _list.size();

        // remove backwards to reduce the amount of
        // element shifting
        for (int i = size - 1; i >= 0; i--) {

            Element<E> element = _list.get(i);

            if (element.isExpired()) {
                _list.remove(i);
                onLifespanEnd(element.element);
            }
        }
    }

    private void startJanitor() {
        if (_janitor != null)
            return;

        _janitor = Scheduler.runTaskRepeatAsync(Nucleus.getPlugin(),
                JANITOR_INITIAL_DELAY_TICKS, JANITOR_INTERVAL_TICKS, new Runnable() {
                    @Override
                    public void run() {

                        List<TimedArrayList> lists;

                        synchronized (_instances) {
                            lists = new ArrayList<TimedArrayList>(_instances.keySet());
                        }

                        for (TimedArrayList list : lists) {

                            if (!list.getPlugin().isEnabled()) {

                                synchronized (_instances) {
                                    _instances.remove(list);
                                }
                                continue;
                            }

                            synchronized (list._sync) {
                                list.cleanup();
                            }
                        }
                    }
                });
    }

    private final static class Element<T> {
        final T element;
        final long expires;
        final Object matcher;

        Element(T item, long lifespan, TimeScale timeScale) {
            this.element = item;
            this.expires = System.currentTimeMillis() + (lifespan * timeScale.getTimeFactor());
            this.matcher = item;
        }

        Element(Object matcher) {
            this.element = null;
            this.expires = 0;
            this.matcher = matcher;
        }

        boolean isExpired() {
            return System.currentTimeMillis() >= expires;
        }

        @Override
        public int hashCode() {
            return element != null ? element.hashCode() : 0;
        }

        @Override
        public boolean equals(Object obj) {

            if (obj instanceof Element) {
                obj = ((Element) obj).matcher;
            }

            if (matcher == null && obj == null)
                return true;

            if (matcher != null && matcher.equals(obj))
                return true;

            return false;
        }
    }

    private final class TimedListIterator extends ConversionListIteratorWrapper<E, Element<E>> {

        ListIterator<Element<E>> iterator;

        TimedListIterator(int index) {
            this.iterator = _list.listIterator(index);
        }

        @Override
        protected E convert(Element<E> internal) {
            return internal.element;
        }

        @Override
        protected Element<E> unconvert(Object external) {
            return new Element<E>(external);
        }

        @Override
        protected ListIterator<Element<E>> iterator() {
            return iterator;
        }
    }
}
