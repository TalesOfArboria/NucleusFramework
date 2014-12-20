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


package com.jcwhatever.bukkit.generic.inventory;

import com.jcwhatever.bukkit.generic.mixins.IPluginOwned;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

/**
 * Manages player kits.
 */
public class KitManager implements IPluginOwned {

    private final Map<String, Kit> _kits;
    private final List<Kit> _kitList;
    private final IDataNode _settings;
    private final Plugin _plugin;

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
        _settings = dataNode;
        _kitList = new LinkedList<Kit>();

        Set<String> kits = dataNode.getSubNodeNames();
        _kits = new HashMap<String, Kit>(kits.size() + 10);

        for (String kitName : kits) {
            Kit kit = new Kit(plugin, kitName);

            ItemStack[] items = dataNode.getItemStacks(kitName + ".items");
            ItemStack[] armor = dataNode.getItemStacks(kitName + ".armor");

            if (items != null)
                kit.addItems(items);

            if (armor != null)
                kit.addArmor(armor);

            _kits.put(kit.getSearchName(), kit);
            _kitList.add(kit);
        }
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
    public Kit getKitByName(String name) {
        name = name.toLowerCase().replace(' ', '_');
        return _kits.get(name);
    }

    /**
     * Deletes a kit.
     *
     * @param name  The name of the kit. Not case sensitive.
     *
     * @return True if kit found and deleted.
     */
    public boolean deleteKit(String name) {
        PreCon.notNullOrEmpty(name);

        name = name.toLowerCase();
        Kit kit = _kits.get(name);
        if (kit == null) {
            return false;
        }
        _kits.remove(name);
        _kitList.remove(kit);
        saveKits();
        return true;
    }

    /**
     * Gets a new list containing all available kits.
     *
     * @return  New List of Kit objects
     */
    public List<Kit> getKits() {
        return new ArrayList<Kit>(_kitList);
    }

    /**
     * Creates a new kit.
     *
     * @param name  The name of the kit.
     *
     * @return Returns the created kit.
     */
    public Kit createKit(String name) {
        PreCon.notNullOrEmpty(name);

        Kit kit = new Kit(_plugin, name);
        _kits.put(kit.getSearchName(), kit);
        _kitList.add(kit);
        return kit;
    }

    /**
     * Save all kits to the config section.
     */
    public void saveKits() {
        for (Kit kit : _kits.values()) {
            _settings.set(kit.getName() + ".items", kit.getItems());
            _settings.set(kit.getName() + ".armor", kit.getArmor());
        }
        _settings.getRoot().saveAsync(null);
    }

}

