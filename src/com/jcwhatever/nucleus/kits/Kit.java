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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.events.kits.GiveKitEvent;
import com.jcwhatever.nucleus.extended.ArmorType;
import com.jcwhatever.nucleus.utils.ArrayUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Scheduler;
import com.jcwhatever.nucleus.utils.inventory.InventoryUtils;
import com.jcwhatever.nucleus.utils.items.ItemStackComparer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

/**
 * A kit of inventory items that can be given to a player.
 */
public class Kit implements IKit {

    private final Plugin _plugin;
    private final String _name;
    private final String _searchName;

    private ItemStack[] _armor = new ItemStack[4]; // helmet, chestplate, leggings, boots
    private List<ItemStack> _items;

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     * @param name    The name of the kit.
     */
    public Kit(Plugin plugin, String name) {
        PreCon.notNull(plugin);
        PreCon.notNullOrEmpty(name);

        _plugin = plugin;
        _name = name;
        _searchName = name.toLowerCase();
        _items = new ArrayList<ItemStack>(15);
    }

    /**
     * Get the owning plugin
     */
    @Override
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the name of the kit.
     */
    @Override
    public String getName() {
        return _name;
    }

    /**
     * Get the name of the kit in lowercase.
     */
    @Override
    public String getSearchName() {
        return _searchName;
    }

    /**
     * Get the kit helmet, if any.
     */
    @Override
    @Nullable
    public ItemStack getHelmet() {
        return _armor[0] != null ? _armor[0].clone() : null;
    }

    /**
     * Get the kit chest plate, if any.
     */
    @Override
    @Nullable
    public ItemStack getChestplate() {
        return _armor[1] != null ? _armor[1].clone() : null;
    }

    /**
     * Get the kit leggings, if any.
     */
    @Override
    @Nullable
    public ItemStack getLeggings() {
        return _armor[2] != null ? _armor[2].clone() : null;
    }

    /**
     * Gets the kit boots, if any.
     */
    @Override
    @Nullable
    public ItemStack getBoots() {
        return _armor[3] != null ? _armor[3].clone() : null;
    }

    /**
     * Gets a new array of non-armor items in the kit.
     */
    @Override
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
     *
     * <p>The array always has 4 elements. Starting from index 0 the items are
     * helmet, chestplate, leggings, boots. If any of the items is not present in
     * the kit then the value of the element is null.</p>
     */
    @Override
    public ItemStack[] getArmor() {

        ItemStack[] armor = new ItemStack[4];

        armor[0] = getHelmet();
        armor[1] = getChestplate();
        armor[2] = getLeggings();
        armor[3] = getBoots();

        return armor;
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
    @Override
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
    @Override
    public boolean take(Player p, ItemStackComparer comparer, int qty) {

        List<ItemStack> itemsToTake = new ArrayList<>(_items.size() + 4);

        itemsToTake.addAll(_items);

        if (_armor[0] != null)
            itemsToTake.add(getHelmet());

        if (_armor[1] != null)
            itemsToTake.add(getChestplate());

        if (_armor[2] != null)
            itemsToTake.add(getLeggings());

        if (_armor[3] != null)
            itemsToTake.add(getBoots());

        // check player has all required items
        for (ItemStack item : itemsToTake) {
            if (!InventoryUtils.has(p.getInventory(), item, comparer, qty))
                return false;
        }

        // take items
        for (ItemStack item : itemsToTake) {
            InventoryUtils.removeAmount(p.getInventory(), item, comparer, qty);
        }

        return true;
    }

    /**
     * Give the kit to the specified player
     *
     * @param p  The player to give a copy of the kit to.
     */
    @Override
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

                Nucleus.getEventManager().callBukkit(_event);

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

    /**
     * Set the kits helmet item.
     *
     * @param helmet  The helmet.
     */
    protected void setHelmet(@Nullable ItemStack helmet) {
        _armor[0] = helmet != null ? helmet.clone() : null;
    }

    /**
     * Set the kits chest plate item.
     *
     * @param chestplate  The chestplate.
     */
    protected void setChestplate(@Nullable ItemStack chestplate) {
        _armor[1] = chestplate != null ? chestplate.clone() : null;
    }

    /**
     * Set the kits legging item.
     *
     * @param leggings  The leggings.
     */
    protected void setLeggings(@Nullable ItemStack leggings) {
        _armor[2] = leggings != null ? leggings.clone() : null;
    }

    /**
     * Set the kits boots item.
     *
     * @param boots  The boots.
     */
    protected void setBoots(@Nullable ItemStack boots) {
        _armor[3] = boots != null ? boots.clone() : null;
    }

    /**
     * Add an array of items, armor or non-armor, to the kit.
     *
     * <p>Armor items automatically replace the appropriate
     * armor item.</p>
     *
     * @param items  The items to add.
     */
    protected void addItems(ItemStack... items) {
        PreCon.notNull(items);

        InventoryUtils.add(_items, items);
    }

    /**
     * Add an array of items, armor or non-armor, to the kit.
     *
     * <p>Armor items automatically replace the appropriate
     * armor item.</p>
     *
     * @param items  The items to add.
     */
    protected void addItems(Collection<ItemStack> items) {
        PreCon.notNull(items);

        ItemStack[] itemArray = items.toArray(new ItemStack[items.size()]);
        InventoryUtils.add(_items, itemArray);
    }

    /**
     * Add a collection of items, armor or non-armor, to the kit.
     *
     * <p>Armor items automatically replace the appropriate
     * armor item.</p>
     *
     * @param items  The items to add.
     */
    protected void addAnyItems(ItemStack... items) {
        PreCon.notNull(items);

        List<ItemStack> clone = new ArrayList<>(items.length);
        Collections.addAll(clone, items);

        addAnyItems(clone);
    }

    /**
     * Add a collection of items, armor or non-armor, to the kit.
     *
     * <p>Armor items automatically replace the appropriate
     * armor item.</p>
     *
     * @param items  The items to add.
     */
    protected void addAnyItems(Collection<ItemStack> items) {
        PreCon.notNull(items);

        List<ItemStack> clone = new ArrayList<>(items);

        ItemStack helmet = null;
        ItemStack chestplate = null;
        ItemStack leggings = null;
        ItemStack boots = null;

        Iterator<ItemStack> iterator = clone.iterator();
        while (iterator.hasNext()) {
            ItemStack item = iterator.next();

            ArmorType type = ArmorType.getType(item);
            switch (type) {
                case HELMET:
                    if (helmet == null) {
                        helmet = item;
                        iterator.remove();
                    }
                    break;
                case CHESTPLATE:
                    if (chestplate == null) {
                        chestplate = item;
                        iterator.remove();
                    }
                    break;
                case LEGGINGS:
                    if (leggings == null) {
                        leggings = item;
                        iterator.remove();
                    }
                    break;
                case BOOTS:
                    if (boots == null) {
                        boots = item;
                        iterator.remove();
                    }
                    break;
            }

            if (helmet != null && chestplate != null && leggings != null && boots != null)
                break;
        }

        if (helmet != null)
            setHelmet(helmet);

        if (chestplate != null)
            setChestplate(chestplate);

        if (leggings != null)
            setLeggings(leggings);

        if (boots != null)
            setBoots(boots);

        if (!clone.isEmpty())
            InventoryUtils.add(_items, clone.toArray(new ItemStack[clone.size()]));
    }


    /**
     * Remove an item from the kit, armor or non-armor.
     *
     * @param items {ItemStack}
     * @return {Boolean} - True if removed.
     */
    protected boolean removeItems(ItemStack... items) {
        PreCon.notNull(items);

        ItemStack[] kitItems = _items.toArray(new ItemStack[_items.size()]);

        if (!InventoryUtils.remove(kitItems, items).isEmpty()) {

            _items.clear();

            kitItems = ArrayUtils.removeNull(kitItems);
            Collections.addAll(_items, kitItems);
            return true;
        }
        return false;
    }

    /**
     * Remove an item from the kit, armor or non-armor.
     *
     * @param items {ItemStack}
     * @return {Boolean} - True if removed.
     */
    protected boolean removeAnyItems(ItemStack... items) {
        PreCon.notNull(items);

        List<ItemStack> clone = new ArrayList<>(items.length);
        Collections.addAll(clone, items);

        ItemStackComparer comparer = ItemStackComparer.getDefault();

        boolean[] armorFlags = new boolean[4];

        Iterator<ItemStack> iterator = clone.iterator();
        while (iterator.hasNext()) {
            ItemStack item = iterator.next();

            switch (ArmorType.getType(item)) {
                case HELMET:
                    if (comparer.isSame(item, _armor[0])) {
                        setHelmet(null);
                        armorFlags[0] = true;
                        iterator.remove();
                    }
                    break;

                case CHESTPLATE:
                    if (comparer.isSame(item, _armor[1])) {
                        setChestplate(null);
                        armorFlags[1] = true;
                        iterator.remove();
                    }
                    break;

                case LEGGINGS:
                    if (comparer.isSame(item, _armor[2])) {
                        setLeggings(null);
                        armorFlags[2] = true;
                        iterator.remove();
                    }
                    break;

                case BOOTS:
                    if (comparer.isSame(item, _armor[3])) {
                        setBoots(null);
                        armorFlags[3] = true;
                        iterator.remove();
                    }
                    break;
            }

            if (armorFlags[0] && armorFlags[1] && armorFlags[2] && armorFlags[3])
                break;
        }

        ItemStack[] kitItems = _items.toArray(new ItemStack[_items.size()]);

        if (!InventoryUtils.remove(kitItems, items).isEmpty()) {

            _items.clear();

            kitItems = ArrayUtils.removeNull(kitItems);
            Collections.addAll(_items, kitItems);
            return true;
        }
        return false;
    }
}

