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

package com.jcwhatever.generic.events.manager;

import com.jcwhatever.generic.mixins.IDisposable;
import com.jcwhatever.generic.utils.PreCon;

import org.bukkit.event.Event;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.hanging.HangingEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.vehicle.VehicleEvent;
import org.bukkit.plugin.Plugin;

/**
 * Used to forward Bukkit events from a {@link GenericsEventManager} instance.
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
 * regions might want to receive Bukkit events that occur within itself). In these
 * instances the use of the forwarder should be tightly controlled to prevent issues
 * with Bukkit events being called more than once in child event managers.</p>
 *
 * <p>The forwarder can be avoided by simply using the global event manager
 * instead of a child instance or using Bukkit events directly.</p>
 *
 * <p>Note that because of this ability and the way events "bubble" up to the
 * global event manager, the global event manager does not receive Bukkit events
 * from child event managers.</p>
 *
 * <p>Do not forget to call the {@code dispose} method in the plugins {@code onDisable}
 * method to remove the forwarder from the source event manager.</p>
 */
public abstract class AbstractBukkitForwarder implements IDisposable {

    private final Plugin _plugin;
    private final GenericsEventManager _source;
    private final Forwarder _forwarder;
    private boolean _isDisposed;

    /**
     * Constructor.
     *
     * <p>Automatically attaches forwarder to the specified source event manager.</p>
     *
     * @param plugin  The owning plugin.
     * @param source  The event manager source that Bukkit events are handled from.
     *                Typically this will be the global event manager since it is the
     *                only manager that receives bukkit events as part of its normal
     *                operation.
     */
    protected AbstractBukkitForwarder(Plugin plugin, GenericsEventManager source) {
        PreCon.notNull(plugin);
        PreCon.notNull(source);

        _plugin = plugin;
        _source = source;
        _forwarder = new Forwarder();

        source.addCallHandler(_forwarder);
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        _source.removeCallHandler(_forwarder);
        _isDisposed = true;
    }

    protected abstract void onBlockEvent(BlockEvent event);

    protected abstract void onPlayerEvent(PlayerEvent event);

    protected abstract void onInventoryEvent(InventoryEvent event);

    protected abstract void onHangingEvent(HangingEvent event);

    protected abstract void onVehicleEvent(VehicleEvent event);

    protected abstract void onEntityEvent(EntityEvent event);

    protected abstract void onOtherEvent(Event event);

    public class Forwarder implements IEventCallHandler {

        @Override
        public Plugin getPlugin() {
            return _plugin;
        }

        @Override
        public void onCall(Object e) {

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
