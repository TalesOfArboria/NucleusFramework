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

package com.jcwhatever.nucleus.internal.managed.entity.mobs.property;

import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.serialize.DeserializeException;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.items.loremeta.LoreMetaMap;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/*
 * 
 */
public class InventoryHolderAnimal implements IMobProperties {

    public static MobType getType() {
        return new MobType("inventoryHolder", InventoryHolder.class) {

            @Override
            public MobSpecificity getSpecificity() {
                return MobSpecificity.MIXIN;
            }

            @Override
            public IMobProperties getProperties(Entity entity) {
                return new InventoryHolderAnimal(entity);
            }

            @Override
            public IMobProperties getProperties(IDataNode dataNode) {

                InventoryHolderAnimal animal = new InventoryHolderAnimal();

                try {
                    animal.deserialize(dataNode);
                } catch (DeserializeException e) {
                    return null;
                }

                return animal;
            }

            @Override
            public IMobProperties getProperties(LoreMetaMap metaMap) {
                InventoryHolderAnimal animal = new InventoryHolderAnimal();
                animal.deserialize(metaMap);
                return animal;
            }
        };
    }


    private ItemStack[] _contents;

    @Override
    public String getName() {
        return "inventoryHolder";
    }

    private InventoryHolderAnimal() {}

    public InventoryHolderAnimal(Entity entity) {
        PreCon.notNull(entity);

        if (!(entity instanceof InventoryHolder))
            return;

        InventoryHolder holder = (InventoryHolder)entity;

        _contents = holder.getInventory().getContents();
    }


    @Override
    public boolean apply(Entity entity) {
        PreCon.notNull(entity);

        if (!(entity instanceof InventoryHolder))
            return false;

        InventoryHolder holder = (InventoryHolder)entity;

        for (int i=0; i < _contents.length; i++) {
            ItemStack item = _contents[i];
            if (item != null && item.getType() != Material.AIR) {
                holder.getInventory().setItem(i, item.clone());
            }
        }

        return true;
    }

    @Override
    public void serialize(IDataNode dataNode) {
        dataNode.set("contents", _contents);
    }

    @Override
    public void deserialize(IDataNode dataNode) throws DeserializeException {
        _contents = dataNode.getItemStacks("contents");
    }

    @Override
    public void serialize(LoreMetaMap metaMap) {
        // do nothing
    }

    @Override
    public void deserialize(LoreMetaMap metaMap) {
        _contents = new ItemStack[0];
    }
}
