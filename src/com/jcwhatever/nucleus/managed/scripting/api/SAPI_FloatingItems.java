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


package com.jcwhatever.nucleus.managed.scripting.api;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.observer.subscriber.SubscriberLinkedList;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.managed.floatingitems.IFloatingItem;
import com.jcwhatever.nucleus.utils.observer.ISubscriber;
import com.jcwhatever.nucleus.utils.observer.script.IScriptUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.script.ScriptUpdateSubscriber;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Deque;
import javax.annotation.Nullable;

/**
 * Gives scripts access to floating items.
 *
 * @see IFloatingItem
 */
public class SAPI_FloatingItems implements IDisposable {

    private final Deque<ISubscriber> _subscribers = new SubscriberLinkedList<>();
    private boolean _isDisposed;

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {

        while (!_subscribers.isEmpty()) {
            ISubscriber subscriber = _subscribers.remove();
            subscriber.dispose();
        }

        _isDisposed = true;
    }

    /**
     * Get all floating items.
     *
     * @param pluginName  The name of the plugin context.
     */
    public Collection<IFloatingItem> getItems(String pluginName) {
        PreCon.notNullOrEmpty(pluginName);

        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        PreCon.isValid(plugin != null, "plugin not found.");

        return Nucleus.getFloatingItems().getAll(plugin);
    }

    /**
     * Get a floating item by name.
     *
     * @param pluginName  The name of the plugin context.
     * @param name        The name of the floating item.
     *
     * @return  Null if not found.
     */
    @Nullable
    public IFloatingItem getItem(String pluginName, String name) {
        PreCon.notNullOrEmpty(pluginName, "pluginName");
        PreCon.notNullOrEmpty(name, "name");

        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        PreCon.isValid(plugin != null, "plugin not found.");

        return Nucleus.getFloatingItems().get(plugin, name);
    }

    /**
     * Add an item pickup handler.
     *
     * @param pluginName  The name of the plugin context.
     * @param name        The name of the floating item.
     * @param callback    The callback to run when the item is picked up.
     */
    public void onPickup(String pluginName, String name,
                         IScriptUpdateSubscriber<Player> callback) {

        PreCon.notNullOrEmpty(pluginName, "pluginName");
        PreCon.notNullOrEmpty(name, "name");
        PreCon.notNull(callback, "callback");

        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        PreCon.isValid(plugin != null, "plugin not found.");

        IFloatingItem item = Nucleus.getFloatingItems().get(plugin, name);
        PreCon.isValid(item != null, "floating item not found.");

        ScriptUpdateSubscriber<Player> subscriber = new ScriptUpdateSubscriber<>(callback);

        item.onPickup(subscriber);

        _subscribers.add(subscriber);
    }

    /**
     * Add an item spawn handler.
     *
     * @param pluginName  The name of the plugin context.
     * @param name        The name of the floating item.
     * @param callback    The callback to run when the item is spawned.
     */
    public void onSpawn(String pluginName, String name,
                        IScriptUpdateSubscriber<Entity> callback) {

        PreCon.notNullOrEmpty(pluginName, "pluginName");
        PreCon.notNullOrEmpty(name, "name");
        PreCon.notNull(callback, "callback");

        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        PreCon.isValid(plugin != null, "plugin not found.");

        IFloatingItem item = Nucleus.getFloatingItems().get(plugin, name);
        PreCon.isValid(item != null, "floating item not found.");

        ScriptUpdateSubscriber<Entity> subscriber = new ScriptUpdateSubscriber<>(callback);

        item.onSpawn(subscriber);
        _subscribers.add(subscriber);
    }

    /**
     * Add an item despawn handler.
     *
     * @param pluginName  The name of the plugin context.
     * @param name        The name of the floating item.
     * @param callback    The callback to run when the item is despawned.
     */
    public void onDespawn(String pluginName, String name,
                          IScriptUpdateSubscriber<Entity> callback) {

        PreCon.notNullOrEmpty(pluginName, "pluginName");
        PreCon.notNullOrEmpty(name, "name");
        PreCon.notNull(callback, "callback");

        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        PreCon.isValid(plugin != null, "plugin not found.");

        IFloatingItem item = Nucleus.getFloatingItems().get(plugin, name);
        PreCon.isValid(item != null, "floating item not found.");

        ScriptUpdateSubscriber<Entity> subscriber = new ScriptUpdateSubscriber<>(callback);

        item.onDespawn(subscriber);
        _subscribers.add(subscriber);
    }
}

