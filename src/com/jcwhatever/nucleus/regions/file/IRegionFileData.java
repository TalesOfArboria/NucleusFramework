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

package com.jcwhatever.nucleus.regions.file;

import com.jcwhatever.nucleus.utils.file.SerializableBlockEntity;
import com.jcwhatever.nucleus.utils.file.SerializableFurnitureEntity;
import com.jcwhatever.nucleus.utils.performance.queued.QueueTask;

import org.bukkit.Material;

import javax.annotation.Nullable;

/**
 * Interface for an object that can receive region data from an
 * {@link IRegionFileLoader}.
 */
public interface IRegionFileData {

    /**
     * Add a block that was read from a file.
     *
     * @param x         The world X coordinate of the block.
     * @param y         The world y coordinate of the block.
     * @param z         The world z coordinate of the block.
     * @param material  The block material.
     * @param data      The block byte data.
     * @param light     The block light value.
     * @param skylight  The block skylight value.
     */
    void addBlock(int x, int y, int z, Material material, int data, int light, int skylight);

    /**
     * Add a block entity (tile entity) that was read from a file.
     *
     * @param blockEntity  The block entity to add.
     */
    void addBlockEntity(SerializableBlockEntity blockEntity);

    /**
     * Add an entity (tile entity) that was read from a file.
     *
     * @param entity  The entity to add.
     */
    void addEntity(SerializableFurnitureEntity entity);

    /**
     * Commit the previously added data.
     *
     * <p>This indicates the loader is either finished or has completed a set of data.
     * This may be invoked multiple times by a loader.</p>
     *
     * @return  A {@link QueueTask} if more work needs to be done, otherwise null.
     */
    @Nullable
    QueueTask commit();
}
