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


package com.jcwhatever.nucleus.kits;

import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Manages player kits.
 */
public class KitManager implements IPluginOwned {

    private final Plugin _plugin;
    private final IDataNode _dataNode;
    protected Map<String, IKit> _kits;

    /**
     * Constructor.
     *
     * @param plugin    The owning plugin.
     * @param dataNode  Config section settings to store and retrieve kit information.
     */
    public KitManager(Plugin plugin, IDataNode dataNode) {
        PreCon.notNull(plugin);
        PreCon.notNull(dataNode);

        _plugin = plugin;
        _dataNode = dataNode;

        load();
    }

    /**
     * Get the owning plugin.
     */
    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Gets a kit by name.
     *
     * @param name  The name of the kit. Not case sensitive.
     *
     * @return Null if kit not found.
     */
    @Nullable
    public IKit getKit(String name) {
        PreCon.notNull(name);

        return _kits.get(name.toLowerCase());
    }

    /**
     * Gets a new list containing all available kits.
     *
     * @return  New List of Kit objects
     */
    public List<IKit> getKits() {
        return CollectionUtils.unmodifiableList(_kits.values());
    }

    /**
     * Creates a new kit.
     *
     * @param name  The name of the kit.
     *
     * @return Returns the created kit or null if the kit name already exists.
     */
    public IKit addKit(String name) {
        PreCon.notNullOrEmpty(name);
        PreCon.validNodeName(name);

        if (_kits.containsKey(name.toLowerCase()))
            return null;

        IDataNode kitNode = getKitNode(name);
        kitNode.set("", null);
        kitNode.saveAsync(null);

        IKit kit = createKit(name);
        _kits.put(kit.getSearchName(), kit);

        return kit;
    }

    /**
     * Deletes a kit.
     *
     * @param name  The name of the kit. Not case sensitive.
     *
     * @return True if kit found and deleted.
     */
    public boolean deleteKit(String name) {
        PreCon.notNull(name);

        name = name.toLowerCase();
        IKit kit = _kits.get(name);
        if (kit == null) {
            return false;
        }

        _kits.remove(name);

        IDataNode kitNode = getKitNode(kit.getName());
        kitNode.remove();
        kitNode.saveAsync(null);

        return true;
    }

    /**
     * Get an {@code IModifiableKit} instance for the
     * given git.
     *
     * @param kit  The kit to modify.
     *
     * @return  The modifiable kit or null if the manager does not own the kit.
     */
    public IModifiableKit modifyKit(IKit kit) {
        PreCon.notNull(kit);

        IKit current = _kits.get(kit.getSearchName());
        if (current == null || kit != current) {
            return null;
        }

        return getModifiableKit(kit);
    }

    /**
     * Get the kit manager's data node.
     */
    protected IDataNode getDataNode() {
        return _dataNode;
    }

    /**
     * Called to get a new instance of a kits data node.
     *
     * @param kitName  The name of the kit.
     */
    protected IDataNode getKitNode(String kitName) {
        return getDataNode().getNode(kitName);
    }

    /**
     * Create a new {@code IKit} instance.
     *
     * @param kitName  The name of the kit.
     */
    protected IKit createKit(String kitName) {
        return new Kit(_plugin, kitName);
    }

    /**
     * Get a {@code IModifiableKit} used to modify the contents
     * of the specified {@code IKit}.
     *
     * @param kit  The kit to modify.
     *
     * @return  Null if the kit cannot be modified.
     */
    protected IModifiableKit getModifiableKit(IKit kit) {
        if (kit instanceof Kit) {
            return new KitModifier(this, (Kit)kit);
        }
        return null;
    }

    protected void load() {

        Set<String> kits = _dataNode.getSubNodeNames();
        _kits = new HashMap<String, IKit>(kits.size() + 10);

        for (String kitName : kits) {
            IKit kit = createKit(kitName);
            IModifiableKit modKit = getModifiableKit(kit);

            IDataNode kitNode = getKitNode(kitName);

            ItemStack[] items = kitNode.getItemStacks("items");
            ItemStack[] armor = kitNode.getItemStacks("armor");

            if (items != null)
                modKit.addItems(items);

            if (armor != null)
                modKit.addAnyItems(armor);

            _kits.put(kit.getSearchName(), kit);
        }
    }

}

