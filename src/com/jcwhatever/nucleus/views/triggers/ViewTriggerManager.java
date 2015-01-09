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

package com.jcwhatever.nucleus.views.triggers;

import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.views.factory.IViewFactory;
import com.jcwhatever.nucleus.views.factory.IViewFactoryStorage;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Manages view triggers.
 */
public class ViewTriggerManager implements IViewTriggerStorage, IPluginOwned {

    private final Plugin _plugin;
    private final IDataNode _dataNode;
    private final IViewFactoryStorage _viewStorage;
    private final Map<String, IViewTriggerFactory> _registeredTypes = new HashMap<>(10);
    private final Map<String, IViewTrigger> _triggers = new HashMap<>(20);

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     */
    public ViewTriggerManager(Plugin plugin, IViewFactoryStorage viewStorage) {
        this(plugin, viewStorage, null);
    }

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     */
    public ViewTriggerManager(Plugin plugin, IViewFactoryStorage viewStorage, @Nullable IDataNode dataNode) {
        PreCon.notNull(plugin);

        _plugin = plugin;
        _dataNode = dataNode;
        _viewStorage = viewStorage;
    }

    /**
     * Get the owning plugin.
     */
    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Add a trigger factory.
     *
     * @param factory  The trigger factory.
     */
    public void addTriggerFactory(IViewTriggerFactory factory) {
        PreCon.notNull(factory);

        _registeredTypes.put(factory.getSearchName(), factory);
    }

    /**
     * Remove a trigger factory.
     *
     * @param name  The name of the trigger factory.
     *
     * @return  True if the factory was found and removed.
     */
    @Nullable
    public boolean removeTriggerFactory(String name) {
        PreCon.notNullOrEmpty(name);

        return _registeredTypes.remove(name.toLowerCase()) != null;
    }

    /**
     * Get all registered trigger factories.
     */
    public List<IViewTriggerFactory> getTriggerFactories() {
        return new ArrayList<>(_registeredTypes.values());
    }

    /**
     * Get a trigger factory by name.
     *
     * @param name  The name of the trigger factory.
     *
     * @return  Null if not found.
     */
    @Override
    @Nullable
    public IViewTriggerFactory getTriggerFactory(String name) {
        PreCon.notNullOrEmpty(name);

        return _registeredTypes.get(name.toLowerCase());
    }

    /**
     * Get a trigger by name.
     *
     * @param name  The name of the trigger.
     *
     * @return  Null if the trigger was not found.
     */
    @Override
    @Nullable
    public IViewTrigger getTrigger(String name) {
        PreCon.notNullOrEmpty(name);

        return _triggers.get(name.toLowerCase());
    }

    /**
     * Add a new trigger.
     *
     * @param factoryName  The name of the trigger factory.
     * @param triggerName  The name of the new trigger.
     * @param targetName   The name of the target view factory.
     *
     * @return  Null if failed to find the factory or the trigger name already exists.
     */
    @Nullable
    public IViewTrigger addTrigger(String factoryName, String triggerName, String targetName) {
        PreCon.notNullOrEmpty(factoryName);
        PreCon.notNullOrEmpty(triggerName);

        IViewTriggerFactory factory = getTriggerFactory(factoryName);
        if (factory == null) {
            NucMsg.debug(getPlugin(), "Failed to find a view trigger factory named '{0}'", factoryName);
            return null;
        }

        if (_triggers.containsKey(factoryName.toLowerCase())) {
            NucMsg.debug(getPlugin(), "Failed to add a view trigger because a " +
                    "trigger named '{0}' already exists.", triggerName);

            return null;
        }

        IViewFactory target = _viewStorage.getViewFactory(targetName);
        if (target == null) {
            NucMsg.debug(getPlugin(), "Failed to find a view factory named '{0}'", factoryName);
            return null;
        }

        IDataNode node = null;

        if (_dataNode != null) {
            node = _dataNode.getNode(triggerName);
            node.set("factory", factory.getName());
            node.set("target", target.getName());
            node.saveAsync(null);
        }

        IViewTrigger trigger = factory.create(triggerName, target, node);
        if (trigger == null)
            throw new AssertionError();

        _triggers.put(trigger.getSearchName(), trigger);

        return trigger;
    }

    /**
     * Remove a trigger.
     *
     * @param name  The name of the trigger to remove.
     *
     * @return  True if the trigger was found and removed.
     */
    public boolean removeTrigger(String name) {
        PreCon.notNullOrEmpty(name);

        IViewTrigger trigger = _triggers.remove(name.toLowerCase());
        if (trigger == null)
            return false;

        trigger.dispose();

        if (_dataNode != null) {
            _dataNode.remove(trigger.getName());
            _dataNode.saveAsync(null);
        }

        return true;
    }

    /**
     * Load triggers from the data node, if any.
     */
    public void load() {
        if (_dataNode == null)
            return;

        for (IDataNode node : _dataNode) {

            String triggerName = node.getName();

            if (_triggers.containsKey(triggerName))
                continue;

            String factoryName = node.getString("factory");
            if (factoryName == null)
                continue;

            String targetName = node.getString("target");
            if (targetName == null)
                continue;

            IViewTriggerFactory factory = getTriggerFactory(factoryName);
            if (factory == null)
                continue;

            IViewFactory target = _viewStorage.getViewFactory(targetName);
            if (target == null)
                continue;

            IViewTrigger trigger = factory.create(triggerName, target, node);

            _triggers.put(trigger.getSearchName(), trigger);
        }
    }
}
