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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.events.kits.GiveKitEvent;
import com.jcwhatever.nucleus.utils.ArrayUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Scheduler;
import com.jcwhatever.nucleus.utils.inventory.InventoryUtils;
import com.jcwhatever.nucleus.utils.items.ItemStackMatcher;
import com.jcwhatever.nucleus.utils.materials.MaterialProperty;
import com.jcwhatever.nucleus.utils.materials.Materials;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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

    @Override
    public boolean take(Entity entity, int qty) {
        return take(entity, ItemStackMatcher.getDefault(), qty);
    }

    @Override
    public boolean take(Entity entity, ItemStackMatcher matcher, int qty) {
        PreCon.notNull(entity);
        PreCon.notNull(matcher);
        PreCon.greaterThanZero(qty);

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

        if (entity instanceof InventoryHolder) {

            InventoryHolder holder = (InventoryHolder)entity;

            // check entity has all required items
            for (ItemStack item : itemsToTake) {
                if (!InventoryUtils.has(holder.getInventory(), item, matcher, qty))
                    return false;
            }

            // take items
            for (ItemStack item : itemsToTake) {
                InventoryUtils.removeAmount(holder.getInventory(), item, matcher, qty);
            }
            return true;
        }

        return false;
    }

    @Override
    public void give(final Entity entity) {
        PreCon.notNull(entity);

        if (!(entity instanceof InventoryHolder) &&
                !(entity instanceof LivingEntity))
            return;

        final GiveKitEvent event = new GiveKitEvent(entity, Kit.this);
        Nucleus.getEventManager().callBukkit(Kit.this, event);

        if (event.isCancelled())
            return;

        Scheduler.runTaskLater(_plugin, new Runnable() {
            @Override
            public void run() {

                Inventory inventory = entity instanceof InventoryHolder
                        ? ((InventoryHolder) entity).getInventory()
                        : null;

                EntityEquipment equipment = entity instanceof LivingEntity
                        ? ((LivingEntity) entity).getEquipment()
                        : null;

                // add items
                if (inventory != null) {
                    for (ItemStack item : event.getItems()) {
                        inventory.addItem(item);
                    }
                }
                else if (equipment != null) {
                    List<ItemStack> items = event.getItems();
                    if (items.size() > 0)
                        equipment.setItemInHand(items.get(0));
                }

                // add equipment
                if (equipment != null) {
                    giveEquipment(equipment, event);
                }
                else if (inventory != null) {
                    giveEquipmentInventory(inventory, event);
                }
            }
        });
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

            Set<MaterialProperty> properties = Materials.getProperties(item.getType());

            if (properties.contains(MaterialProperty.ARMOR)) {

                if (properties.contains(MaterialProperty.HELMET)) {
                    helmet = item;
                    iterator.remove();
                }
                else if (properties.contains(MaterialProperty.CHESTPLATE)) {
                    chestplate = item;
                    iterator.remove();
                }
                else if (properties.contains(MaterialProperty.LEGGINGS)) {
                    leggings = item;
                    iterator.remove();
                }
                else if (properties.contains(MaterialProperty.BOOTS)) {
                    boots = item;
                    iterator.remove();
                }
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

        ItemStackMatcher comparer = ItemStackMatcher.getDefault();

        boolean[] armorFlags = new boolean[4];

        Iterator<ItemStack> iterator = clone.iterator();
        while (iterator.hasNext()) {
            ItemStack item = iterator.next();

            Set<MaterialProperty> properties = Materials.getProperties(item.getType());

            if (properties.contains(MaterialProperty.ARMOR)) {
                int index;

                if (properties.contains(MaterialProperty.HELMET)) {
                    setHelmet(null);
                    index = 0;
                } else if (properties.contains(MaterialProperty.CHESTPLATE)) {
                    setChestplate(null);
                    index = 1;
                } else if (properties.contains(MaterialProperty.LEGGINGS)) {
                    setLeggings(null);
                    index = 2;
                } else if (properties.contains(MaterialProperty.BOOTS)) {
                    setBoots(null);
                    index = 3;
                }
                else {
                    continue;
                }

                armorFlags[index] = true;
                iterator.remove();
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

    // give equipment to EntityEquipment
    private void giveEquipment(EntityEquipment equipment, GiveKitEvent event) {

        if (event.getHelmet() != null)
            equipment.setHelmet(event.getHelmet());

        if (event.getChestplate() != null)
            equipment.setChestplate(event.getChestplate());

        if (event.getLeggings() != null)
            equipment.setLeggings(event.getLeggings());

        if (event.getBoots() != null)
            equipment.setBoots(event.getBoots());
    }

    // give equipment to Inventory
    private void giveEquipmentInventory(Inventory inventory, GiveKitEvent event) {

        if (event.getHelmet() != null)
            inventory.addItem(event.getHelmet());

        if (event.getChestplate() != null)
            inventory.addItem(event.getChestplate());

        if (event.getLeggings() != null)
            inventory.addItem(event.getLeggings());

        if (event.getBoots() != null)
            inventory.addItem(event.getBoots());
    }
}

