package com.jcwhatever.bukkit.generic.collections;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An hash map where each key value has an individual lifespan that when reached, causes the item
 * to be removed.
 *
 * <p>The lifespan can only be reset by re-adding an item.</p>
 *
 * <p>Items can be added using the default lifespan time or a lifespan can be specified per item.</p>
 */
public class TimedMap<K, V> extends HashMap<K, V> {

	private static final long serialVersionUID = 1945035628724130125L;

	private final int _defaultTime;
	private final TimedMap<K, V> _instance;
	private Map<Object, BukkitTask> _tasks;
    private List<LifespanEndAction<K>> _onLifespanEnd = new ArrayList<>(5);
    private List<CollectionEmptyAction<TimedMap<K, V>>> _onEmpty = new ArrayList<>(5);

    /**
     * Constructor. Default lifespan is 1 second.
     */
	public TimedMap() {
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
    public TimedMap(int size) {
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
	public TimedMap(int size, int defaultLifespan) {
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
     * @param lifespan  The items lifespan in ticks.
     */
	public V put(final K key, final V value, int lifespan) {
        PreCon.notNull(key);
        PreCon.notNull(value);
        PreCon.positiveNumber(lifespan);

		if (lifespan == 0)
			return value;
		
		if (lifespan < 0) {
			return super.put(key, value);
		}
		
		BukkitTask current = _tasks.remove(key);
		if (current != null) {
		    current.cancel();
		}
		
		BukkitTask task = Bukkit.getScheduler().runTaskLater(GenericsLib.getPlugin(), new Runnable() {

			@Override
			public void run() {
				_tasks.remove(value);
				_instance.remove(key);
                onLifespanEnd(key);
			}
			
		}, lifespan);
		
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
     * @param lifespan  The lifespan of the added items in ticks.
     */
	public void putAll(Map<? extends K, ? extends V> entries, int lifespan) {
        PreCon.notNull(entries);
        PreCon.positiveNumber(lifespan);

		for (Map.Entry<? extends K, ? extends V> entry : entries.entrySet()) {
			put(entry.getKey(), entry.getValue(), lifespan);
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
     * @param action  The handler to call.
     */
    public void addOnLifetimeEnd(LifespanEndAction<K> action) {
        PreCon.notNull(action);

        _onLifespanEnd.add(action);
    }

    /**
     * Remove a handler.
     *
     * @param action  The handler to remove.
     */
    public void removeOnLifespanEnd(LifespanEndAction<K> action) {
        PreCon.notNull(action);

        _onLifespanEnd.remove(action);
    }

    /**
     * Add a handler to be called whenever the collection becomes empty.
     *
     * @param action  The handler to call
     */
    public void addOnCollectionEmpty(CollectionEmptyAction<TimedMap<K, V>> action) {
        PreCon.notNull(action);

        _onEmpty.add(action);
    }

    /**
     * Remove a handler.
     *
     * @param action  The handler to remove.
     */
    public void removeOnCollectionEmpty(CollectionEmptyAction<TimedMap<K, V>> action) {
        PreCon.notNull(action);

        _onEmpty.remove(action);
    }

    private void onEmpty() {
        if (!isEmpty() || _onEmpty.isEmpty())
            return;

        for (CollectionEmptyAction<TimedMap<K, V>> action : _onEmpty) {
            action.onEmpty(this);
        }
    }

    private void onLifespanEnd(K key) {
        for (LifespanEndAction<K> action : _onLifespanEnd) {
            action.onEnd(key);
        }
    }

}
