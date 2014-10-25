package com.jcwhatever.bukkit.generic.inventory;

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Manages player kits.
 */
public class KitManager {

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

            kit.addItems(items);
            kit.addArmor(armor);

            _kits.put(kit.getSearchName(), kit);
            _kitList.add(kit);
        }
    }

    /**
     * Get the owning plugin.
     */
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

