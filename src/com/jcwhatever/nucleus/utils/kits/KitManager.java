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


package com.jcwhatever.nucleus.utils.kits;

import com.jcwhatever.nucleus.mixins.IPluginOwned;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.managers.NamedInsensitiveDataManager;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Manages player kits.
 */
public class KitManager extends NamedInsensitiveDataManager<IKit> implements IPluginOwned {

    private final Plugin _plugin;

    /**True to load data from the data node during the constructor.
     * Constructor.
     *
     * @param plugin    The owning plugin.
     * @param dataNode  Config section settings to store and retrieve kit information.
     */
    public KitManager(Plugin plugin, @Nullable IDataNode dataNode) {
        this(plugin, dataNode, true);
    }

    /**
     * Constructor.
     *
     * @param plugin    The owning plugin.
     * @param dataNode  Config section settings to store and retrieve kit information.
     * @param loadKits  True to load kits from the data node during the constructor.
     */
    public KitManager(Plugin plugin, @Nullable IDataNode dataNode, boolean loadKits) {
        super(dataNode, false);
        PreCon.notNull(plugin);

        _plugin = plugin;

        if (loadKits)
            load();
    }

    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Creates a new kit.
     *
     * @param name  The name of the kit.
     *
     * @return Returns the created kit or null if the kit name already exists.
     */
    public IKit add(String name) {
        PreCon.notNullOrEmpty(name);
        PreCon.validNodeName(name);

        if (contains(name))
            return null;

        IKit kit = createKit(name);
        add(kit);

        return kit;
    }

    /**
     * Get an {@link IModifiableKit} instance for the
     * given git.
     *
     * @param kit  The kit to modify.
     *
     * @return  The modifiable kit or null if the manager does not own the kit.
     */
    public IModifiableKit modifyKit(IKit kit) {
        PreCon.notNull(kit);

        IKit current = _map.get(getName(kit));
        if (current == null || kit != current) {
            return null;
        }

        return getModifiableKit(kit);
    }

    /**
     * Create a new {@link IKit} instance.
     *
     * @param kitName  The name of the kit.
     */
    protected IKit createKit(String kitName) {
        return new Kit(_plugin, kitName);
    }

    /**
     * Get a {@link IModifiableKit} used to modify the contents
     * of the specified {@link IKit}.
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

    @Nullable
    @Override
    protected IKit load(String name, IDataNode kitNode) {
        IKit kit = createKit(name);
        IModifiableKit modKit = getModifiableKit(kit);

        ItemStack[] items = kitNode.getItemStacks("items");
        ItemStack[] armor = kitNode.getItemStacks("armor");

        if (items != null)
            modKit.addItems(items);

        if (armor != null)
            modKit.addAnyItems(armor);

        return kit;
    }

    @Nullable
    @Override
    protected void save(IKit kit, IDataNode node) {
        node.set("items", kit.getItems());
        node.set("armor", kit.getArmor());
    }

    protected void save(IKit item) {
        if (_dataNode == null)
            return;

        IDataNode dataNode = _dataNode.getNode(getName(item));
        save(item, dataNode);
        dataNode.save();
    }
}

