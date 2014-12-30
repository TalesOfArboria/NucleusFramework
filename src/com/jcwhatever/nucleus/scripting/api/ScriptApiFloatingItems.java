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


package com.jcwhatever.nucleus.scripting.api;

import com.jcwhatever.nucleus.floatingitems.FloatingItem;
import com.jcwhatever.nucleus.floatingitems.FloatingItem.PickupHandler;
import com.jcwhatever.nucleus.floatingitems.FloatingItemManager;
import com.jcwhatever.nucleus.floatingitems.IFloatingItem;
import com.jcwhatever.nucleus.scripting.IEvaluatedScript;
import com.jcwhatever.nucleus.scripting.ScriptApiInfo;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;

@ScriptApiInfo(
        variableName = "floating",
        description = "Get floating items.")
public class ScriptApiFloatingItems extends NucleusScriptApi {

    private final ApiObject _api;

    /**
     * Constructor.
     *
     * @param plugin   The owning plugin.
     * @param manager  The floating item manager.
     */
    public ScriptApiFloatingItems(Plugin plugin, FloatingItemManager manager) {
        super(plugin);

        _api = new ApiObject(manager);
    }

    /**
     * Constructor.
     *
     * @param plugin    The owning plugin.
     * @param dataNode  The floating item data storage node.
     */
    public ScriptApiFloatingItems(Plugin plugin, IDataNode dataNode) {
        super(plugin);

        _api = new ApiObject(new FloatingItemManager(plugin, dataNode));
    }

    @Override
    public IScriptApiObject getApiObject(IEvaluatedScript script) {
        return _api;
    }

    public void reset() {
        _api.dispose();
    }

    public static class ApiObject implements IScriptApiObject {

        private LinkedList<PickupWrapper> _pickupCallbacks = new LinkedList<>();
        private LinkedList<CallbackWrapper> _spawnCallbacks = new LinkedList<>();
        private LinkedList<CallbackWrapper> _despawnCallbacks = new LinkedList<>();
        private final FloatingItemManager _manager;
        private boolean _isDisposed;

        ApiObject(FloatingItemManager manager) {
            _manager = manager;
        }

        @Override
        public boolean isDisposed() {
            return _isDisposed;
        }

        @Override
        public void dispose() {

            while (!_pickupCallbacks.isEmpty()) {
                PickupWrapper wrapper = _pickupCallbacks.remove();

                if (wrapper.getItem() instanceof FloatingItem)
                    ((FloatingItem) wrapper.getItem()).removeOnPickup(wrapper);
            }

            while (!_spawnCallbacks.isEmpty()) {
                CallbackWrapper wrapper = _spawnCallbacks.remove();

                if (wrapper.getItem() instanceof FloatingItem)
                    ((FloatingItem) wrapper.getItem()).removeOnSpawn(wrapper);
            }

            while (!_despawnCallbacks.isEmpty()) {
                CallbackWrapper wrapper = _despawnCallbacks.remove();

                if (wrapper.getItem() instanceof FloatingItem)
                    ((FloatingItem) wrapper.getItem()).removeOnDespawn(wrapper);
            }

            _isDisposed = true;
        }

        /**
         * Get all floating items.
         */
        public List<IFloatingItem> getItems() {
            return _manager.getAll();
        }

        /**
         * Get a floating item by name.
         *
         * @param name  The name of the floating item.
         *
         * @return  Null if not found.
         */
        @Nullable
        public IFloatingItem getItem(String name) {
            PreCon.notNullOrEmpty(name);

            return _manager.get(name);
        }

        /**
         * Add an item pickup handler.
         *
         * @param name      The name of the floating item.
         * @param callback  The callback to run when the item is picked up.
         */
        public void onPickup(String name, final PickupCallback callback) {
            PreCon.notNullOrEmpty(name);
            PreCon.notNull(callback);

            IFloatingItem item = _manager.get(name);
            PreCon.notNull(item);

            if (!(item instanceof FloatingItem))
                return;

            PickupWrapper wrapper = new PickupWrapper(item, callback);
            ((FloatingItem) item).addOnPickup(wrapper);
            _pickupCallbacks.add(wrapper);
        }

        /**
         * Add an item spawn handler.
         *
         * @param name      The name of the floating item.
         * @param callback  The callback to run when the item is spawned.
         */
        public void onSpawn(String name, Runnable callback) {
            PreCon.notNullOrEmpty(name);
            PreCon.notNull(callback);

            IFloatingItem item = _manager.get(name);
            PreCon.notNull(item);

            if (!(item instanceof FloatingItem))
                return;

            CallbackWrapper wrapper = new CallbackWrapper(item, callback);
            ((FloatingItem) item).addOnSpawn(wrapper);
            _spawnCallbacks.add(wrapper);
        }

        /**
         * Add an item despawn handler.
         *
         * @param name      The name of the floating item.
         * @param callback  The callback to run when the item is despawned.
         */
        public void onDespawn(String name, Runnable callback) {
            PreCon.notNullOrEmpty(name);
            PreCon.notNull(callback);

            IFloatingItem item = _manager.get(name);
            PreCon.notNull(item);

            if (!(item instanceof FloatingItem))
                return;

            CallbackWrapper wrapper = new CallbackWrapper(item, callback);
            ((FloatingItem) item).addOnDespawn(wrapper);
            _despawnCallbacks.add(wrapper);
        }
    }

    public interface PickupCallback {
        void onPickup(Object player, Object item, Object isCancelled);
    }

    private static class PickupWrapper implements PickupHandler {

        private final IFloatingItem _item;
        private final PickupCallback _callback;

        PickupWrapper(IFloatingItem item, PickupCallback callback) {
            _item = item;
            _callback = callback;
        }

        public IFloatingItem getItem() {
            return _item;
        }

        @Override
        public void onPickup(Player p, FloatingItem item, boolean isCancelled) {
            _callback.onPickup(p, item, isCancelled);
        }
    }

    private static class CallbackWrapper implements Runnable {

        private final IFloatingItem _item;
        private final Runnable _callback;

        CallbackWrapper(IFloatingItem item, Runnable callback) {
            _item = item;
            _callback = callback;
        }

        public IFloatingItem getItem() {
            return _item;
        }

        @Override
        public void run() {
            _callback.run();
        }
    }
}
