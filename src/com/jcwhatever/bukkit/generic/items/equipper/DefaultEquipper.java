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

package com.jcwhatever.bukkit.generic.items.equipper;

import com.jcwhatever.bukkit.generic.extended.ArmorType;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Default equipper to use when an equipper is not registered
 * for an entity type.
 */
public class DefaultEquipper implements IEntityEquipper {

    @Override
    public boolean equip(Entity entity, ItemStack item) {
        PreCon.notNull(entity);
        PreCon.notNull(item);

        if (entity instanceof LivingEntity) {

            LivingEntity livingEntity = (LivingEntity)entity;

            EntityEquipment equipment = livingEntity.getEquipment();

            if (entity instanceof HumanEntity) {

                ArmorType armorType = ArmorType.getType(item);

                switch (armorType) {
                    case HELMET:
                        equipment.setHelmet(item);
                        break;
                    case CHESTPLATE:
                        equipment.setChestplate(item);
                        break;
                    case LEGGINGS:
                        equipment.setLeggings(item);
                        break;
                    case BOOTS:
                        equipment.setBoots(item);
                        break;
                    default:
                        equipment.setItemInHand(item);
                        break;
                }
            } else {
                equipment.setItemInHand(item);
            }

            return true;
        }

        return false;
    }

    @Override
    public List<ItemStack> clear(Entity entity) {
        PreCon.notNull(entity);

        if (entity instanceof LivingEntity) {

            LivingEntity livingEntity = (LivingEntity)entity;

            EntityEquipment equipment = livingEntity.getEquipment();

            List<ItemStack> result = new ArrayList<>(5);

            addItemToList(result, equipment.getItemInHand());

            if (entity instanceof HumanEntity) {

                addItemToList(result, equipment.getHelmet());
                addItemToList(result, equipment.getChestplate());
                addItemToList(result, equipment.getLeggings());
                addItemToList(result, equipment.getBoots());

            }

            return result;
        }

        return new ArrayList<>(0);
    }

    private void addItemToList(List<ItemStack> list, ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return;

        list.add(item);
    }
}
