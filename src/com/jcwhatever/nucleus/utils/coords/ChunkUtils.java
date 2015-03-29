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

package com.jcwhatever.nucleus.utils.coords;

import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Chunk related utilities.
 */
public final class ChunkUtils {

    private ChunkUtils() {}

    private static final MutableCoords2Di CHUNK_COORDS = new MutableCoords2Di();

    /**
     * Get chunk coordinates from a location.
     *
     * @param location  The location.
     */
    public static Coords2Di getChunkCoords(Location location) {
        PreCon.notNull(location);

        int x = (int)Math.floor(location.getX() / 16);
        int z = (int)Math.floor(location.getZ() / 16);

        return new Coords2Di(x, z);
    }

    /**
     * Get chunk coordinates from a location and copy the result into an output
     * {@link MutableCoords2Di}.
     *
     * @param location  The location.
     * @param output    The output {@link MutableCoords2Di}.
     *
     * @return  The output {@link MutableCoords2Di}.
     */
    public static MutableCoords2Di getChunkCoords(Location location, MutableCoords2Di output) {
        PreCon.notNull(location);
        PreCon.notNull(output);

        int x = (int)Math.floor(location.getX() / 16);
        int z = (int)Math.floor(location.getZ() / 16);

        output.setX(x);
        output.setZ(z);

        return output;
    }

    /**
     * Determine if the {@link org.bukkit.Chunk} at the specified {@link org.bukkit.Location} and all
     * chunks within the specified radius around it are loaded.
     *
     * @param location  The {@link org.bukkit.Location} to check.
     * @param radius    The radius to check.
     *
     * @return  True if all chunks within the radius are loaded, otherwise false.
     */
    public static boolean isNearbyChunksLoaded(Location location, int radius) {
        Coords2Di chunkCoords = Bukkit.isPrimaryThread()
                ? getChunkCoords(location, CHUNK_COORDS)
                : getChunkCoords(location);

        return isNearbyChunksLoaded(location.getWorld(), chunkCoords.getX(), chunkCoords.getZ(), radius);
    }

    /**
     * Determine if the {@link org.bukkit.Chunk} at the specified coordinates and all
     * chunks within the specified radius around it are loaded.
     *
     * @param world   The {@link org.bukkit.World} to check in.
     * @param chunkX  The X coordinates of the chunk.
     * @param chunkZ  The Z coordinates of the chunk.
     * @param radius  The radius to check.
     *
     * @return  True if all chunks within the radius are loaded, otherwise false.
     */
    public static boolean isNearbyChunksLoaded(World world, int chunkX, int chunkZ, int radius) {
        PreCon.notNull(world);
        PreCon.positiveNumber(radius);

        int startX = chunkX - radius;
        int startZ = chunkZ - radius;
        int endX = chunkX + radius;
        int endZ = chunkZ + radius;

        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                if (!world.isChunkLoaded(x, z)) {
                    return false;
                }
            }
        }

        return true;
    }
}
