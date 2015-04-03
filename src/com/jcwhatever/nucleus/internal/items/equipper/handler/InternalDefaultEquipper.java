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

package com.jcwhatever.nucleus.internal.items.equipper.handler;

import com.jcwhatever.nucleus.managed.items.equipper.IEquipper;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.materials.MaterialProperty;
import com.jcwhatever.nucleus.utils.materials.Materials;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Default equipper to use when an equipper is not registered
 * for an entity type.
 */
public class InternalDefaultEquipper implements IEquipper {

    @Override
    public boolean equip(Entity entity, ItemStack item) {
        PreCon.notNull(entity);
        PreCon.notNull(item);

        if (entity instanceof LivingEntity) {

            LivingEntity livingEntity = (LivingEntity)entity;

            EntityEquipment equipment = livingEntity.getEquipment();

            if (entity instanceof HumanEntity) {

                Set<MaterialProperty> properties = Materials.getProperties(item.getType());

                if (properties.contains(MaterialProperty.ARMOR)) {

                    if (properties.contains(MaterialProperty.HELMET)) {
                        equipment.setHelmet(item);
                    }
                    else if (properties.contains(MaterialProperty.CHESTPLATE)) {
                        equipment.setChestplate(item);
                    }
                    else if (properties.contains(MaterialProperty.LEGGINGS)) {
                        equipment.setLeggings(item);
                    }
                    else if (properties.contains(MaterialProperty.BOOTS)) {
                        equipment.setBoots(item);
                    }
                }
                else {
                    equipment.setItemInHand(item);
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
            equipment.setItemInHand(null);

            if (entity instanceof HumanEntity) {

                addItemToList(result, equipment.getHelmet());
                equipment.setHelmet(null);

                addItemToList(result, equipment.getChestplate());
                equipment.setChestplate(null);

                addItemToList(result, equipment.getLeggings());
                equipment.setLeggings(null);

                addItemToList(result, equipment.getBoots());
                equipment.setBoots(null);
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
