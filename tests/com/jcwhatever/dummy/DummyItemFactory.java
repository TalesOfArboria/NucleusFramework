package com.jcwhatever.dummy;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/*
 * 
 */
public class DummyItemFactory implements ItemFactory {

    @Override
    public ItemMeta getItemMeta(Material material) {
        return null;
    }

    @Override
    public boolean isApplicable(ItemMeta itemMeta, ItemStack itemStack) throws IllegalArgumentException {
        return false;
    }

    @Override
    public boolean isApplicable(ItemMeta itemMeta, Material material) throws IllegalArgumentException {
        return false;
    }

    @Override
    public boolean equals(ItemMeta itemMeta, ItemMeta itemMeta1) throws IllegalArgumentException {
        return false;
    }

    @Override
    public ItemMeta asMetaFor(ItemMeta itemMeta, ItemStack itemStack) throws IllegalArgumentException {
        return null;
    }

    @Override
    public ItemMeta asMetaFor(ItemMeta itemMeta, Material material) throws IllegalArgumentException {
        return null;
    }

    @Override
    public Color getDefaultLeatherColor() {
        return null;
    }
}
