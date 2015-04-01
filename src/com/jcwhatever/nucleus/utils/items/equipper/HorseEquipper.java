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

package com.jcwhatever.nucleus.utils.items.equipper;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.materials.MaterialProperty;
import com.jcwhatever.nucleus.utils.materials.Materials;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Equipper used for Horse entities.
 *
 * @see EntityEquipperManager
 * @see IEntityEquipper
 */
public class HorseEquipper implements IEntityEquipper {

    @Override
    public boolean equip(Entity entity, ItemStack item) {
        PreCon.notNull(entity);
        PreCon.notNull(item);

        if (!(entity instanceof Horse))
            return false;

        Horse horse = (Horse)entity;

        HorseInventory inventory = horse.getInventory();

        Material material = item.getType();
        if (material == Material.SADDLE) {

            inventory.setSaddle(item);
        }
        else {

            if (Materials.hasProperty(material, MaterialProperty.HORSE_ARMOR)) {
                inventory.setArmor(item);
            }
            else {
                return false;
            }
        }

        return true;
    }

    @Override
    public List<ItemStack> clear(Entity entity) {
        PreCon.notNull(entity);

        if (!(entity instanceof Horse))
            return new ArrayList<>(0);

        Horse horse = (Horse)entity;

        HorseInventory inventory = horse.getInventory();

        List<ItemStack> result = new ArrayList<>(2);

        ItemStack saddle = inventory.getSaddle();
        if (saddle != null) {
            result.add(saddle);
            inventory.setSaddle(null);
        }

        ItemStack armor = inventory.getArmor();
        if (armor != null) {
            result.add(armor);
            inventory.setArmor(null);
        }

        return result;
    }
}
