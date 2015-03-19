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

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.items.ItemStackMatcher;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import javax.annotation.Nullable;

/**
 * A {@link Kit} wrapper that allows modifying and saving
 * the kit contents.
 */
public class KitModifier implements IModifiableKit {

    protected final KitManager _manager;
    protected final Kit _kit;

    /**
     * Constructor.
     *
     * @param manager  The owning {@link KitManager}.
     * @param kit      The {@link Kit} to modify.
     */
    protected KitModifier(KitManager manager, Kit kit) {
        PreCon.notNull(manager);
        PreCon.notNull(kit);

        _manager = manager;
        _kit = kit;
    }

    @Override
    public void setHelmet(@Nullable ItemStack helmet) {
        _kit.setHelmet(helmet);
    }

    @Override
    public void setChestplate(@Nullable ItemStack chestplate) {
        _kit.setChestplate(chestplate);
    }

    @Override
    public void setLeggings(@Nullable ItemStack leggings) {
        _kit.setLeggings(leggings);
    }

    @Override
    public void setBoots(@Nullable ItemStack boots) {
        _kit.setBoots(boots);
    }

    @Override
    public void addItems(ItemStack... items) {
        _kit.addItems(items);
    }

    @Override
    public void addItems(Collection<ItemStack> items) {
        _kit.addItems(items);
    }

    @Override
    public boolean removeItems(ItemStack... items) {
        return _kit.removeItems(items);
    }

    @Override
    public boolean removeItems(Collection<ItemStack> items) {

        ItemStack[] clone = items.toArray(new ItemStack[items.size()]);
        return _kit.removeItems(clone);
    }

    @Override
    public void addAnyItems(ItemStack... items) {
        _kit.addAnyItems(items);
    }

    @Override
    public void addAnyItems(Collection<ItemStack> items) {
        _kit.addAnyItems(items);
    }

    @Override
    public boolean removeAnyItems(ItemStack... items) {
        return _kit.removeAnyItems(items);
    }

    @Override
    public boolean removeAnyItems(Collection<ItemStack> items) {
        ItemStack[] clone = items.toArray(new ItemStack[items.size()]);
        return _kit.removeAnyItems(clone);
    }

    @Override
    public boolean save() {
        _manager.save(this);
        return true;
    }

    @Nullable
    @Override
    public ItemStack getHelmet() {
        return _kit.getHelmet();
    }

    @Nullable
    @Override
    public ItemStack getChestplate() {
        return _kit.getChestplate();
    }

    @Nullable
    @Override
    public ItemStack getLeggings() {
        return _kit.getLeggings();
    }

    @Nullable
    @Override
    public ItemStack getBoots() {
        return _kit.getBoots();
    }

    @Override
    public ItemStack[] getItems() {
        return _kit.getItems();
    }

    @Override
    public ItemStack[] getArmor() {
        return _kit.getArmor();
    }

    @Override
    public void give(Entity entity) {
        _kit.give(entity);
    }

    @Override
    public boolean take(Entity entity, int qty) {
        return _kit.take(entity, qty);
    }

    @Override
    public boolean take(Entity entity, ItemStackMatcher comparer, int qty) {
        return _kit.take(entity, comparer, qty);
    }

    @Override
    public String getSearchName() {
        return _kit.getSearchName();
    }

    @Override
    public String getName() {
        return _kit.getName();
    }

    @Override
    public Plugin getPlugin() {
        return _kit.getPlugin();
    }
}
