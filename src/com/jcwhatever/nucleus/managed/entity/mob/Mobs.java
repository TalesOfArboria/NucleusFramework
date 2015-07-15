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

package com.jcwhatever.nucleus.managed.entity.mob;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.storage.IDataNode;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

/**
 * Static convenience methods for the internal {@link IMobSerializer} implementation.
 */
public final class Mobs {

    private Mobs() {}

    /**
     * Record entity data into an {@link ISerializableMob} instance.
     *
     * @param entity  The entity to serialize.
     *
     * @return  The serialized mob object or null if the entity cannot be serialized.
     */
    @Nullable
    public static ISerializableMob getSerializable(Entity entity) {
        return serializer().getSerializable(entity);
    }

    /**
     * Deserialize mob data from an {@link ItemStack}'s lore meta.
     *
     * @param itemStack  The item stack to deserialize from.
     *
     * @return  An {@link ISerializableMob} that can be used to spawn the entity
     * or null if the item stacks lore meta is invalid.
     */
    @Nullable
    public static ISerializableMob deserialize(ItemStack itemStack) {
        return serializer().deserialize(itemStack);
    }

    /**
     * Deserialize mob data from a data node.
     *
     * @param dataNode  The data node to deserialize from.
     *
     * @return  An {@link ISerializableMob} that can be used to spawn the entity
     * or null if the item stacks lore meta is invalid.
     */
    @Nullable
    public static ISerializableMob deserialize(IDataNode dataNode) {
        return serializer().deserialize(dataNode);
    }

    private static IMobSerializer serializer() {
        return Nucleus.getMobSerializer();
    }
}
