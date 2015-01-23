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

import com.jcwhatever.nucleus.collections.wrap.ConversionQueueWrapper;
import com.jcwhatever.nucleus.collections.wrap.SyncStrategy;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

/**
 * A Queue of {@code Player} objects.
 *
 * <p>{@code Player} objects are automatically removed if the player logs out.</p>
 *
 * <p>Thread safe.</p>
 *
 * <p>The queues iterators must be used inside a synchronized block which locks the
 * queue instance. Otherwise, a {@link java.lang.IllegalStateException} is thrown.</p>
 */
public class PlayerQueue extends ConversionQueueWrapper<Player, PlayerElement> implements IPlayerCollection {

	private final Plugin _plugin;
	private final transient Queue<PlayerElement> _queue = new LinkedList<PlayerElement>();
	private final transient PlayerCollectionTracker _tracker;
	private final transient Object _sync = new Object();

	/**
	 * Constructor.
	 */
	public PlayerQueue(Plugin plugin) {
		super(SyncStrategy.SYNC);

		PreCon.notNull(plugin);

		_plugin = plugin;
		_tracker = new PlayerCollectionTracker(this);
	}

	@Override
	public Plugin getPlugin() {
		return _plugin;
	}

	@Override
	protected void onAdded(PlayerElement element) {
		_tracker.notifyPlayerAdded(element.getUniqueId());
	}

	@Override
	protected void onRemoved(@SuppressWarnings("unused") Object element) {

		UUID playerID = PlayerUtils.getPlayerId(element);
		if (playerID == null)
			throw new ClassCastException();

		_tracker.notifyPlayerRemoved(playerID);
	}

	@Override
	protected Player convert(PlayerElement internal) {
		return internal.getPlayer();
	}

	@Override
	protected PlayerElement unconvert(Object external) {

		Player player = PlayerUtils.getPlayer(external);
		if (player == null)
			throw new ClassCastException();

		return new PlayerElement(player);
	}

	@Override
	protected Queue<PlayerElement> queue() {
		return _queue;
	}

	@Override
	public void removePlayer(Player player) {
		synchronized (_sync) {
			_queue.remove(new PlayerElement(player));
		}
	}
}
