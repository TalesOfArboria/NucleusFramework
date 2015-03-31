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


package com.jcwhatever.nucleus.events.sounds;


import com.jcwhatever.nucleus.mixins.ICancellable;
import com.jcwhatever.nucleus.mixins.IPlayerReference;
import com.jcwhatever.nucleus.sounds.types.ResourceSound;
import com.jcwhatever.nucleus.sounds.SoundSettings;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a resource pack sound is played.
 */
public class PlayResourceSoundEvent extends Event
		implements Cancellable, ICancellable, IPlayerReference {
	
	private static final HandlerList handlers = new HandlerList();
	
	private final Player _player;
    private final SoundSettings _settings;

	private ResourceSound _sound;
	private boolean _isCancelled;

	/**
	 * Constructor.
	 *
	 * @param p         The player the sound is being played to.
	 * @param sound     The sound being played.
	 * @param settings  The sound settings.
	 */
	public PlayResourceSoundEvent(Player p, ResourceSound sound, SoundSettings settings) {
		PreCon.notNull(p);
		PreCon.notNull(sound);
		PreCon.notNull(settings);

		_player = p;
		_sound = sound;
		_settings = settings;
	}

	/**
	 * Get the player the sound is being played to.
	 */
	@Override
	public Player getPlayer() {
		return _player;
	}

	/**
	 * Get the resource sound being played.
	 */
	public ResourceSound getResourceSound() {
		return _sound;
	}

	/**
	 * Get the sound settings.
	 */
	public SoundSettings getSettings() {
		return _settings;
	}

	@Override
	public boolean isCancelled() {
		return _isCancelled;
	}

	@Override
	public void setCancelled(boolean isCancelled) {
		_isCancelled = isCancelled;
	}

	@Override
    public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
