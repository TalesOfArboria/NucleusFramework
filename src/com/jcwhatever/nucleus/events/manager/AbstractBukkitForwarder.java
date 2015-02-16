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

package com.jcwhatever.nucleus.events.manager;

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.update.UpdateSubscriber;

import org.bukkit.event.Event;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.hanging.HangingEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.vehicle.VehicleEvent;
import org.bukkit.plugin.Plugin;

/**
 * Used to forward Bukkit events from a {@link EventManager} instance.
 *
 * <p>The forwarder is automatically attached the event manager source
 * specified in the constructor.</p>
 *
 * <p>The global event manager receives Bukkit events. An event forwarder can
 * be used to forward the received Bukkit events to another event manager instance
 * that is used in a context that might be interested in the event.</p>
 *
 * <p>The use of the forwarder should be avoided except in instances where an
 * event may relate to a specific event manager. (i.e. An event manager for 1 of hundreds of
 * regions might want to receive Bukkit events that occur within itself).</p>
 *
 * <p>The forwarder can be avoided by simply using the global event manager
 * instead of a child instance or using Bukkit events directly.</p>
 *
 * <p>If you need to detach the forwarder, call the {@link #dispose} method. The forwarder
 * will not be usable afterwards.</p>
 */
public abstract class AbstractBukkitForwarder implements IPluginOwned, IDisposable {

    private final Plugin _plugin;
    private final Forwarder _forwarder;
    private boolean _isDisposed;

    /**
     * Constructor.
     *
     * <p>Automatically attaches forwarder to the specified source event manager.</p>
     *
     * @param plugin        The owning plugin.
     * @param eventManager  The event manager to register the forwarder with.
     */
    protected AbstractBukkitForwarder(Plugin plugin, EventManager eventManager) {
        PreCon.notNull(plugin);
        PreCon.notNull(eventManager);

        _plugin = plugin;
        _forwarder = new Forwarder();

        eventManager.onCall(plugin, _forwarder);
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        _forwarder.dispose();
        _isDisposed = true;
    }

    protected abstract void onBlockEvent(BlockEvent event);

    protected abstract void onPlayerEvent(PlayerEvent event);

    protected abstract void onInventoryEvent(InventoryEvent event);

    protected abstract void onHangingEvent(HangingEvent event);

    protected abstract void onVehicleEvent(VehicleEvent event);

    protected abstract void onEntityEvent(EntityEvent event);

    protected abstract void onOtherEvent(Event event);

    public class Forwarder extends UpdateSubscriber<Object> {

        @Override
        public void on(Object e) {

            if (!(e instanceof Event))
                return;

            Event event = (Event) e;

            if (event instanceof PlayerEvent) {
                onPlayerEvent((PlayerEvent)event);
            }
            else if (event instanceof BlockEvent) {
                onBlockEvent((BlockEvent)event);
            }
            else if (event instanceof HangingEvent) {
                onHangingEvent((HangingEvent)event);
            }
            else if (event instanceof InventoryEvent) {
                onInventoryEvent((InventoryEvent) event);
            }
            else if (event instanceof VehicleEvent) {
                onVehicleEvent((VehicleEvent) event);
            }
            else if (event instanceof EntityEvent) {
                onEntityEvent((EntityEvent) event);
            }
            else {
                onOtherEvent(event);
            }
        }
    }
}
