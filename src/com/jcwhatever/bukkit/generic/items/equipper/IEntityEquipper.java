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

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Abstract living entity equipper. Used to give an {@code ItemStack}
 * as equipment to a living entity.
 */
public interface IEntityEquipper {

    /**
     * Equip an entity with the specified {@code ItemStack}.
     *
     * @param entity  The entity to equip.
     * @param item    The item to equip the npc with.
     */
    boolean equip(Entity entity, ItemStack item);

    /**
     * Clear equipment from entity.
     *
     * @param entity  The entity whose equipment is to be cleared.
     *
     * @return  {@code ItemStack} array containing the cleared equipment.
     */
    List<ItemStack> clear(Entity entity);
}
