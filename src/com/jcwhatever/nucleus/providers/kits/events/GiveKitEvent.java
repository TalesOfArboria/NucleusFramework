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


package com.jcwhatever.nucleus.providers.kits.events;

import com.jcwhatever.nucleus.providers.kits.IKit;
import com.jcwhatever.nucleus.mixins.ICancellable;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Called when a Kit is given.
 */
public class GiveKitEvent extends Event
        implements Cancellable, ICancellable {

    private static final HandlerList handlers = new HandlerList();

    private ItemStack _helmet;
    private ItemStack _chestplate;
    private ItemStack _leggings;
    private ItemStack _boots;
    private List<ItemStack> _items;
    private boolean _isCancelled = false;
    private Entity _entity;
    private IKit _kit;

    public GiveKitEvent(Entity entity, IKit kit) {
        PreCon.notNull(entity);
        PreCon.notNull(kit);

        _entity = entity;
        _kit = kit;

        if (kit.getHelmet() != null) {
            _helmet = kit.getHelmet().clone();
        }
        if (kit.getChestplate() != null) {
            _chestplate = kit.getChestplate().clone();
        }
        if (kit.getLeggings() != null) {
            _leggings = kit.getLeggings().clone();
        }
        if (kit.getBoots() != null) {
            _boots = kit.getBoots().clone();
        }

        _items = new ArrayList<ItemStack>(6 * 9);

        for (ItemStack item : kit.getItems()) {
            _items.add(item.clone());
        }
    }

    /**
     * Get the entity receiving the kit.
     */
    public Entity getEntity() {
        return _entity;
    }

    /**
     * Get the kit.
     */
    public IKit getKit() {
        return _kit;
    }

    /**
     * Get the helmet being given
     * to the player.
     *
     * @return  Null if none.
     */
    @Nullable
    public ItemStack getHelmet() {
        return _helmet;
    }

    /**
     * Get the chestplate being given
     * to the player.
     *
     * @return  Null if none.
     */
    @Nullable
    public ItemStack getChestplate() {
        return _chestplate;
    }

    /**
     * Get the leggings being given
     * to the player.
     *
     * @return  Null if none.
     */
    public ItemStack getLeggings() {
        return _leggings;
    }

    /**
     * Get the boots being given
     * to the player.
     *
     * @return  Null if none.
     */
    public ItemStack getBoots() {
        return _boots;
    }

    /**
     * Get the inventory items being given to the player.
     */
    public List<ItemStack> getItems() {
        return _items;
    }

    /**
     * Get the armor items being given to the player as an
     * array with 4 elements. Null elements indicate an armor
     * item is not being given.
     *
     * <p>Armor item index order is: helmet, chestplate, leggings, boots.</p>
     */
    public ItemStack[] getArmorArray() {
        return new ItemStack[] {
                _helmet,
                _chestplate,
                _leggings,
                _boots
        };
    }

    /**
     * Set the helmet that will be given to the player.
     */
    public void setHelmet(@Nullable ItemStack helmet) {
        _helmet = helmet;
    }

    /**
     * Set the chestplate that will be given to the player.
     */
    public void setChestplate(@Nullable ItemStack chestplate) {
        _chestplate = chestplate;
    }

    /**
     * Set the leggings that will be given to the player.
     */
    public void setLeggings(@Nullable ItemStack leggings) {
        _leggings = leggings;
    }

    /**
     * Set the boots that will be given to the player.
     */
    public void setBoots(@Nullable ItemStack boots) {
        _boots = boots;
    }

    /**
     * Add an item to give to the player.
     */
    public void addItem(ItemStack item) {
        PreCon.notNull(item);

        _items.add(item);
    }

    /**
     * Add a collection of items to give to the player.
     */
    public void addItems(Collection<ItemStack> items) {
        PreCon.notNull(items);

        for (ItemStack item : items) {
            addItem(item);
        }
    }

    /**
     * Remove an item.
     */
    public boolean removeItem(ItemStack item) {
        PreCon.notNull(item);

        int count = _items.size();
        for (int i = 0; i < count; i++) {

            if (_items.get(i).getType() != item.getType())
                continue;
            _items.remove(i);
            i--;
            count--;
        }

        return true;
    }

    @Override
    public boolean isCancelled() {
        return _isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        _isCancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}

