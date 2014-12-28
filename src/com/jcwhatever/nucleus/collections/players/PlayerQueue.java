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

import com.jcwhatever.nucleus.collections.wrappers.AbstractIteratorWrapper;

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
public class PlayerQueue extends AbstractPlayerCollection implements Queue<Player> {

	private final transient Queue<Player> _queue = new LinkedList<Player>();

	private transient boolean _isDisposed;

	/**
	 * Constructor.
	 */
	public PlayerQueue(Plugin plugin) {
		super(plugin);
	}

	@Override
	public boolean addAll(Collection<? extends Player> players) {

		synchronized (_sync) {

			for (Player p : players) {
				notifyPlayerAdded(p.getUniqueId());
			}

			return _queue.addAll(players);
		}
	}

	@Override
	public void clear() {
		synchronized (_sync) {
			while (!_queue.isEmpty()) {
				Player p = _queue.remove();

				notifyPlayerRemoved(p.getUniqueId());
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
	public boolean containsAll(Collection<?> items) {
		synchronized (_sync) {
			return _queue.containsAll(items);
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
	public boolean remove(Object item) {
		synchronized (_sync) {
			if (_queue.remove(item)) {
				if (item instanceof Player && !_queue.contains(item)) {
					notifyPlayerRemoved(((Player) item).getUniqueId());
				}
				return true;
			}

			return false;
		}
	}

	@Override
	public boolean removeAll(Collection<?> items) {
		synchronized (_sync) {
			for (Object obj : items) {
				if (obj instanceof Player) {
					notifyPlayerRemoved(((Player) obj).getUniqueId());
				}
			}
			return _queue.removeAll(items);
		}
	}

	@Override
	public boolean retainAll(Collection<?> items) {
		synchronized (_sync) {
			LinkedList<Player> temp = new LinkedList<>(_queue);
			//noinspection SuspiciousMethodCalls
			if (temp.removeAll(items)) {
				while (!temp.isEmpty()) {
					notifyPlayerRemoved(temp.remove().getUniqueId());
				}
			}

			return _queue.retainAll(items);
		}
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
	public boolean add(Player p) {
		synchronized (_sync) {
			if (_queue.add(p)) {
				notifyPlayerAdded(p.getUniqueId());
				return true;
			}
			return false;
		}
	}

	@Override
	public Player element() {
		synchronized (_sync) {
			return _queue.element();
		}
	}

	@Override
	public boolean offer(Player p) {
		synchronized (_sync) {
			if (_queue.offer(p)) {
				notifyPlayerAdded(p.getUniqueId());
				return true;
			}
			return false;
		}
	}

	@Override
	public Player peek() {
		synchronized (_sync) {
			return _queue.peek();
		}
	}

	@Override
	@Nullable
	public Player poll() {
		synchronized (_sync) {
			Player p = _queue.poll();
			if (p != null && !_queue.contains(p)) {
				notifyPlayerRemoved(p.getUniqueId());
			}
			return p;
		}
	}

	@Override
	@Nullable
	public Player remove() {
		synchronized (_sync) {
			Player p = _queue.remove();
			if (p != null && !_queue.contains(p)) {
				notifyPlayerRemoved(p.getUniqueId());
			}
			return p;
		}
	}

	@Override
	public void removePlayer(Player p) {
		synchronized (_sync) {
			_queue.remove(p);
		}
	}

	@Override
	public boolean isDisposed() {
		return _isDisposed;
	}

	/**
	 * Call to remove references that prevent
	 * the garbage collector from collecting
	 * the instance after it is not longer needed.
	 */
	@Override
	public void dispose() {
		clear();
		_isDisposed = true;
	}

	private class PlayerIterator extends AbstractIteratorWrapper<Player> {

		Iterator<Player> iterator = _queue.iterator();

		@Override
		public void remove() {
			synchronized (_sync) {
				notifyPlayerRemoved(_current.getUniqueId());
				iterator.remove();
			}
		}

		@Override
		protected Iterator<Player> getIterator() {
			return iterator;
		}
	}
}
