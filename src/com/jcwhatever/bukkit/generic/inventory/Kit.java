package com.jcwhatever.bukkit.generic.inventory;

import com.jcwhatever.bukkit.generic.extended.ArmorType;
import com.jcwhatever.bukkit.generic.items.ItemStackComparer;
import com.jcwhatever.bukkit.generic.utils.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A kit of items that can be given to a player
 *
 * @author JC The Pants
 *
 */
public class Kit {

    private final Plugin _plugin;
    private final String _name;
    private final String _searchName;

    private ItemStack _helmet;
    private ItemStack _chestplate;
    private ItemStack _leggings;
    private ItemStack _boots;
    private List<ItemStack> _items;


    /**
     * Constructor
     * @param name {String} - The name of the kit
     */
    public Kit(Plugin plugin, String name) {
        _plugin = plugin;
        _name = name;
        _searchName = name.toLowerCase();
        _items = new ArrayList<ItemStack>(15);
    }

    /**
     * Gets the name of the kit
     * @return {String}
     */
    public String getName() {
        return _name;
    }

    /**
     * Gets the name of the kit in lowercase
     * @return {String}
     */
    public String getSearchName() {
        return _searchName;
    }

    /**
     * Gets the kit helmet, if any. Null if none.
     * @return {ItemStack}
     */
    public ItemStack getHelmet() {
        return _helmet != null ? _helmet.clone() : null;
    }

    /**
     * Gets the kit chest plate, if any. Null if none.
     * @return {ItemStack}
     */
    public ItemStack getChestplate() {
        return _chestplate != null ? _chestplate.clone() : null;
    }

    /**
     * Gets the kit leggings, if any. Null if none.
     * @return {ItemStack}
     */
    public ItemStack getLeggings() {
        return _leggings != null ? _leggings.clone() : null;
    }

    /**
     * Gets the kit boots, if any. Null if none.
     * @return
     */
    public ItemStack getBoots() {
        return _boots != null ? _boots : null;
    }

    /**
     * Gets a new array of non-armor items in the kit.
     * @return {ItemStack[]}
     */
    public ItemStack[] getItems() {

        // deep clone into an array
        ItemStack[] array = new ItemStack[_items.size()];
        for (int i = 0; i < _items.size(); ++i) {
            array[i] = _items.get(i).clone();
        }
        return array;
    }

    /**
     * Gets the kit armor items as an a new array.
     * @return {ItemStack[]}
     */
    public ItemStack[] getArmor() {
        List<ItemStack> armorList = getArmorList();

        // deep clone into an array
        ItemStack[] array = new ItemStack[armorList.size()];
        for (int i = 0; i < armorList.size(); ++i) {
            array[i] = armorList.get(i);
        }
        return array;
    }

    /**
     * Gets a list of the kits armor items.
     * @return {List<ItemStack>}
     */
    private List<ItemStack> getArmorList() {
        List<ItemStack> armorList = new ArrayList<ItemStack>(5);

        if (_helmet != null)
            armorList.add(_helmet.clone());

        if (_chestplate != null)
            armorList.add(_chestplate.clone());

        if (_leggings != null)
            armorList.add(_leggings.clone());

        if (_boots != null)
            armorList.add(_boots.clone());

        return armorList;
    }

    /**
     * Set the kits helmet item.
     * @param helmet {ItemStack}
     */
    public void setHelmet(ItemStack helmet) {
        _helmet = helmet != null ? helmet.clone() : null;
    }

    /**
     * Set the kits chest plate item.
     * @param chestplate {ItemStack}
     */
    public void setChestplate(ItemStack chestplate) {
        _chestplate = chestplate != null ? chestplate.clone() : null;
    }

    /**
     * Set the kits legging item.
     * @param leggings {ItemStack}
     */
    public void setLeggings(ItemStack leggings) {
        _leggings = leggings != null ? leggings.clone() : null;
    }

    /**
     * Set the kits boots item.
     * @param boots {ItemStack}
     */
    public void setBoots(ItemStack boots) {
        _boots = boots != null ? boots.clone() : null;
    }

    /**
     * Add an item, armor or non-armor, to the kit.
     * @param item {ItemStack}
     */
    public void addItem(ItemStack item) {
        if (ArmorType.getType(item) == ArmorType.NOT_ARMOR) {
            _items.add(item.clone());
        } else {
            addArmor(item);
        }
    }

    /**
     * Add an array of items, armor or non-armor, to the kit.
     * @param items {ItemStack}
     */
    public void addItems(ItemStack[] items) {
        if (items == null) {
            return;
        }
        for (ItemStack item : items) {
            addItem(item);
        }
    }

    /**
     * Add a collection of items, armor or non-armor, to the kit.
     * @param items {Collection<ItemStack> items}
     */
    public void addItems(Collection<ItemStack> items) {
        if (items == null) {
            return;
        }
        for (ItemStack item : items) {
            addItem(item);
        }
    }

    /**
     * Add an array of armor items to the kit.
     * @param armor {ItemStack[]}
     */
    public void addArmor(ItemStack[] armor) {
        if (armor == null) {
            return;
        }
        for (ItemStack item : armor) {
            addArmor(item);
        }
    }

    /**
     * Add an armor item to the kit
     * @param item {ItemStack}
     */
    public void addArmor(ItemStack item) {
        switch (ArmorType.getType(item)) {
            case HELMET:
                setHelmet(item);
                break;

            case CHESTPLATE:
                setChestplate(item);
                break;

            case LEGGINGS:
                setLeggings(item);
                break;

            case BOOTS:
                setBoots(item);
        }
    }

    /**
     * Remove an item from the kit, armor or non-armor.
     * @param item {ItemStack}
     * @return {Boolean} - True if removed.
     */
    public boolean removeItem(ItemStack item) {
        if (item == null) {
            return false;
        }

        switch (ArmorType.getType(item)) {
            case HELMET:
                setHelmet(null);
                break;

            case CHESTPLATE:
                setChestplate(null);
                break;

            case LEGGINGS:
                setLeggings(null);
                break;

            case BOOTS:
                setBoots(null);
                break;

            default:
                int count = _items.size();
                for (int i = 0; i < count; i++) {
                    if (_items.get(i).getType() != item.getType())
                        continue;

                    _items.remove(i);
                    i--;
                    count--;
                }
        }
        return true;
    }

    /**
     * Take items from the kit away from the specified player.
     *
     * <p>Does not take items if the player does not have all required items.</p>
     *
     * @param p    The player to take from.
     * @param qty  The number of items to take. (kit * qty)
     *
     * @return  True if the player had all the items.
     */
    public boolean take(Player p, int qty) {
        return take(p, ItemStackComparer.getDurability(), qty);
    }

    /**
     * Take items from the kit away from the specified player.
     *
     * <p>Does not take items if the player does not have all required items.</p>
     *
     * @param p        The player to take from.
     * @param comparer The {@code ItemStackComparer} used to compare items.
     * @param qty      The number of items to take. (kit * qty)
     *
     * @return  True if the player had all the items.
     */
    public boolean take(Player p, ItemStackComparer comparer, int qty) {

        List<ItemStack> itemsToTake = new ArrayList<>(_items.size() + 4);

        itemsToTake.addAll(_items);

        if (_helmet != null)
            itemsToTake.add(_helmet);

        if (_chestplate != null)
            itemsToTake.add(_chestplate);

        if (_leggings != null)
            itemsToTake.add(_leggings);

        if (_boots != null)
            itemsToTake.add(_boots);

        // check player has all required items
        for (ItemStack item : itemsToTake) {
            if (!InventoryHelper.has(p.getInventory(), item, comparer, qty))
                return false;
        }

        // take items
        for (ItemStack item : itemsToTake) {
            InventoryHelper.remove(p.getInventory(), item, comparer, qty);
        }

        return true;
    }

    /**
     * Give the kit to the specified player
     *
     * @param p  The player to give a copy of the kit to.
     */
    public void give(final Player p) {

        class TGive implements Runnable {
            GiveKitEvent _event;

            public TGive(GiveKitEvent event) {
                _event = event;
            }

            @Override
            public void run() {

                if (p == null)
                    return;

                Bukkit.getServer().getPluginManager().callEvent(_event);

                PlayerInventory inv = p.getInventory();

                for (ItemStack item : _event.getItems()) {
                    inv.addItem(item);
                }


                if (_event.getHelmet() != null)
                    inv.setHelmet(_event.getHelmet());

                if (_event.getChestplate() != null)
                    inv.setChestplate(_event.getChestplate());

                if (_event.getLeggings() != null)
                    inv.setLeggings(_event.getLeggings());

                if (_event.getBoots() != null)
                    inv.setBoots(_event.getBoots());
            }
        }

        Scheduler.runTaskLater(_plugin, 1, new TGive(new GiveKitEvent(p, this)));
    }



}

