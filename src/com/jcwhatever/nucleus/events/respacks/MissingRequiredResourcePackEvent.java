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

package com.jcwhatever.nucleus.events.respacks;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.events.HandlerListExt;
import com.jcwhatever.nucleus.managed.resourcepacks.IResourcePack;
import com.jcwhatever.nucleus.mixins.ICancellable;
import com.jcwhatever.nucleus.utils.PreCon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import javax.annotation.Nullable;

/**
 * Called when a player is removed from a world for not having a required resource pack.
 */
public class MissingRequiredResourcePackEvent extends PlayerEvent implements ICancellable, Cancellable {

    private static final HandlerList handlers = new HandlerListExt(
            Nucleus.getPlugin(), MissingRequiredResourcePackEvent.class);

    private final World _world;
    private final IResourcePack _pack;
    private final Location _relocation;

    private Action _action;
    private boolean _isCancelled;
    private CharSequence _message;

    public enum Action {
        /**
         * Relocate the player to another location.
         */
        RELOCATE,
        /**
         * Kick the player from the server.
         */
        KICK,
        /**
         * Ignore and do nothing.
         *
         * <p>Message is still displayed.</p>
         */
        IGNORE
    }

    /**
     * Constructor.
     *
     * @param player      The player.
     * @param world       The world the resource pack is required in.
     * @param pack        The required resource pack.
     * @param relocation  The location to send the player if the action is {@link Action#RELOCATE}.
     * @param action      The action to take.
     * @param message     The message to display to the player.
     */
    public MissingRequiredResourcePackEvent(Player player, World world, IResourcePack pack,
                                            Location relocation, Action action,
                                            @Nullable CharSequence message) {
        super(player);
        PreCon.notNull(player);
        PreCon.notNull(world);
        PreCon.notNull(pack);
        PreCon.notNull(relocation);
        PreCon.notNull(action);

        _world = world;
        _pack = pack;
        _relocation = relocation;
        _action = action;
        _message = message;
    }

    /**
     * Get the world the resource pack is required in.
     */
    public World getWorld() {
        return _world;
    }

    /**
     * Get the required resource pack.
     */
    public IResourcePack getResourcePack() {
        return _pack;
    }

    /**
     * Get a direct reference to the location to send the player.
     */
    public Location getRelocation() {
        return _relocation;
    }

    /**
     * Get the action.
     */
    public Action getAction() {
        return _action;
    }

    /**
     * Set the action.
     *
     * @param action  The action.
     */
    public void setAction(Action action) {
        PreCon.notNull(action);

        _action = action;
    }

    /**
     * Get the message to display to the user.
     *
     * <p>The message is displayed for all actions. The message is not displayed
     * if the event is cancelled or the message is null.</p>
     */
    @Nullable
    public CharSequence getMessage() {
        return _message;
    }

    /**
     * Set the message to display to the user.
     *
     * @param message  The message.
     */
    public void setMessage(@Nullable CharSequence message) {
        _message = message;
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