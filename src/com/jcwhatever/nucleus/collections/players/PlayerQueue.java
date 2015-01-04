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


package com.jcwhatever.nucleus.collections.players;

import com.jcwhatever.nucleus.collections.players.PlayerElement.PlayerElementMatcher;
import com.jcwhatever.nucleus.collections.wrappers.AbstractConversionIterator;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import javax.annotation.Nullable;

/**
 * A Queue of {@code Player} objects.
 *
 * <p>{@code Player} objects are automatically removed if the player logs out.</p>
 */
public class PlayerQueue implements IPlayerCollection, Queue<Player> {

	private final Plugin _plugin;
	private final transient Queue<PlayerElement> _queue = new LinkedList<PlayerElement>();
	private final transient PlayerCollectionTracker _tracker;
	private final transient Object _sync = new Object();

	/**
	 * Constructor.
	 */
	public PlayerQueue(Plugin plugin) {
		PreCon.notNull(plugin);

		_plugin = plugin;
		_tracker = new PlayerCollectionTracker(this);
	}

	@Override
	public Plugin getPlugin() {
		return _plugin;
	}

	@Override
	public Object getSync() {
		return _sync;
	}

	@Override
	public boolean addAll(Collection<? extends Player> players) {
		PreCon.notNull(players);

		boolean isChanged = false;

		synchronized (_sync) {

			for (Player p : players) {
				if (_queue.add(new PlayerElement(p))) {
					_tracker.notifyPlayerAdded(p.getUniqueId());
					isChanged = true;
				}
			}
		}

		return isChanged;
	}

	@Override
	public void clear() {
		synchronized (_sync) {
			while (!_queue.isEmpty()) {
				PlayerElement p = _queue.remove();
				_tracker.notifyPlayerRemoved(p.getUniqueId());
			}

			_queue.clear();
		}
	}

	@Override
	public boolean contains(Object arg) {
		synchronized (_sync) {
			return _queue.contains(arg);
		}
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		PreCon.notNull(collection);

		boolean isChanged = false;
		synchronized (_sync) {
			for (Object obj : collection) {

			}
			return _queue.containsAll(collection);
		}
	}

	@Override
	public boolean isEmpty() {
		synchronized (_sync) {
			return _queue.isEmpty();
		}
	}

	@Override
	public Iterator<Player> iterator() {
		return new PlayerIterator();
	}

	@Override
	public boolean remove(Object o) {

		synchronized (_sync) {
			if (_queue.remove(o)) {
				PlayerElementMatcher matcher = new PlayerElementMatcher(o);
				//noinspection SuspiciousMethodCalls
				if (!_queue.contains(matcher) && matcher.getUniqueId() != null) {
					_tracker.notifyPlayerRemoved(matcher.getUniqueId());
				}
				return true;
			}

			return false;
		}
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		PreCon.notNull(collection);

		boolean isChanged = false;
		synchronized (_sync) {
			for (Object obj : collection) {
				isChanged = remove(obj) || isChanged;
			}
		}
		return isChanged;
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		PreCon.notNull(collection);

		boolean isChanged = false;

		synchronized (_sync) {

			Iterator<PlayerElement> iterator = _queue.iterator();
			while (iterator.hasNext()) {
				PlayerElement entry = iterator.next();

				boolean keep = false;
				for (Object obj : collection) {
					if (obj.equals(entry.getPlayer())) {
						keep = true;
						break;
					}
				}

				if (!keep) {
					iterator.remove();
					isChanged = true;
				}
			}
		}

		return isChanged;
	}

	@Override
	public int size() {
		synchronized (_sync) {
			return _queue.size();
		}
	}

	@Override
	public Object[] toArray() {
		synchronized (_sync) {
			return _queue.toArray();
		}
	}

	@Override
	public <T> T[] toArray(T[] array) {
		synchronized (_sync) {
			//noinspection SuspiciousToArrayCall
			return _queue.toArray(array);
		}
	}

	@Override
	public boolean add(Player player) {
		synchronized (_sync) {
			if (_queue.add(new PlayerElement(player))) {
				_tracker.notifyPlayerAdded(player.getUniqueId());
				return true;
			}
			return false;
		}
	}

	@Override
	public Player element() {
		synchronized (_sync) {
			PlayerElement entry = _queue.element();
			return entry != null ? entry.getPlayer() : null;
		}
	}

	@Override
	public boolean offer(Player player) {
		synchronized (_sync) {
			if (_queue.offer(new PlayerElement(player))) {
				_tracker.notifyPlayerAdded(player.getUniqueId());
				return true;
			}
			return false;
		}
	}

	@Override
	public Player peek() {
		synchronized (_sync) {
			PlayerElement entry = _queue.peek();
			return entry != null ? entry.getPlayer() : null;
		}
	}

	@Override
	@Nullable
	public Player poll() {
		synchronized (_sync) {
			PlayerElement entry = _queue.poll();
			if (entry != null && !_queue.contains(entry)) {
				_tracker.notifyPlayerRemoved(entry.getUniqueId());
				return entry.getPlayer();
			}
			return null;
		}
	}

	@Override
	@Nullable
	public Player remove() {
		synchronized (_sync) {
			PlayerElement entry = _queue.remove();
			if (entry != null && !_queue.contains(entry)) {
				_tracker.notifyPlayerRemoved(entry.getUniqueId());
				return entry.getPlayer();
			}
			return null;
		}
	}

	@Override
	public void removePlayer(Player player) {
		synchronized (_sync) {
			_queue.remove(new PlayerElement(player));
		}
	}

	private class PlayerIterator extends AbstractConversionIterator<Player, PlayerElement> {

		Iterator<PlayerElement> iterator = _queue.iterator();

		@Override
		public void remove() {
			synchronized (_sync) {
				_tracker.notifyPlayerRemoved(_current.getUniqueId());
				iterator.remove();
			}
		}

		@Override
		protected Player getElement(PlayerElement trueElement) {
			return trueElement.getPlayer();
		}

		@Override
		protected Iterator<PlayerElement> getIterator() {
			return iterator;
		}
	}
}
