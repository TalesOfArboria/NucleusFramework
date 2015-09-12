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

import java.util.ArrayList;
import java.util.Collection;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.ThreadSingletons;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Chunk related utilities.
 */
public final class ChunkUtils {

    private ChunkUtils() {}

    private static final ThreadSingletons<CacheCoords> CACHE_COORDS =
            new ThreadSingletons<>(new ThreadSingletons.ISingletonFactory<CacheCoords>() {
                @Override
                public CacheCoords create(Thread thread) {
                    return new CacheCoords();
                }
            });

    private static ICoords2Di[] CHUNK_CORNER_BLOCKS = new ICoords2Di[] {
            new Coords2Di(0, 0),
            new Coords2Di(0, 15),
            new Coords2Di(15, 15),
            new Coords2Di(15, 0)
    };

    /**
     * Get chunk coordinates from a location.
     *
     * @param location  The location.
     */
    public static Coords2Di getChunkCoords(Location location) {
        PreCon.notNull(location, "location");

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
        PreCon.notNull(location, "location");
        PreCon.notNull(output, "output");

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
        PreCon.notNull(location, "location");
        PreCon.positiveNumber(radius, "radius");

        Coords2Di chunkCoords = getChunkCoords(location, CACHE_COORDS.get().chunk);
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
        PreCon.notNull(world, "world");
        PreCon.positiveNumber(radius, "radius");

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

    /**
     * Get all chunks that contain the location and the specified radius around it.
     *
     * @param location     The location.
     * @param blockRadius  The block radius around the location.
     */
    public static Collection<IChunkCoords> getChunksInRadius(Location location, int blockRadius) {
        return getChunksInRadius(location, blockRadius,
                new ArrayList<IChunkCoords>((blockRadius >> 4) * (blockRadius >> 4)));
    }

    /**
     * Get all chunks that contain the location and the specified radius around it and
     * add the the specified output collection.
     *
     * @param location     The location.
     * @param blockRadius  The block radius around the location.
     * @param output       The output collection.
     *
     * @return  The output collection.
     */
    public static <T extends Collection<IChunkCoords>> T getChunksInRadius(Location location,
                                                                           int blockRadius, T output) {
        PreCon.notNull(location, "location");
        PreCon.notNull(location.getWorld(), "location world");
        PreCon.positiveNumber(blockRadius, "blockRadius");
        PreCon.notNull(output, "output");

        int chunkRadius = (blockRadius >> 4) + 1;
        int blockRadiusSquared = blockRadius * blockRadius;

        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;

        CacheCoords cache = CACHE_COORDS.get();
        MutableChunkCoords chunkCoords = new MutableChunkCoords();

        for (int x=-chunkRadius; x <= chunkRadius; x++) {
            for (int z=-chunkRadius; z <= chunkRadius; z++) {

                setChunkCoords(chunkCoords, location.getWorld(), chunkX + x, chunkZ + z);

                if (x == 0 && z == 0) {
                    output.add(chunkCoords);
                    chunkCoords = new MutableChunkCoords();
                    continue;
                }

                double distance = getChunkDistanceSquared(location, chunkCoords, cache);
                if (distance <= blockRadiusSquared) {
                    output.add(chunkCoords);
                    chunkCoords = new MutableChunkCoords();
                }
            }
        }

        return output;
    }

    /**
     * Get the corner of a chunk that is closest to the specified source location.
     *
     * @param chunkCoords  The coords of the chunk to check.
     * @param source       The source location.
     *
     * @return  1 of 4 possible singleton values. The coords returned represents the coordinate
     * of the corner within the chunk relative to the chunk.
     */
    public static ICoords2Di getClosestChunkCorner(ICoords2Di chunkCoords, Location source) {
        PreCon.notNull(chunkCoords, "chunkCoords");
        PreCon.notNull(source, "source");
        PreCon.notNull(source.getWorld(), "source world");

        return getClosestChunkCorner(chunkCoords, source, CACHE_COORDS.get());
    }

    /**
     * Get the distance squared from the source location to the closest corner of a specified chunk.
     *
     * @param chunkCoords  The coords of the chunk to check.
     * @param source       The source location.
     */
    public static double getCornerDistanceSquared(Location source, ICoords2Di chunkCoords) {
        PreCon.notNull(chunkCoords, "chunkCoords");
        PreCon.notNull(source, "source");
        PreCon.notNull(source.getWorld(), "source world");

        return getCornerDistanceSquared(source, chunkCoords, CACHE_COORDS.get());
    }

    /**
     * Get the location within a specified chunk that is closest to the specified
     * source location.
     *
     * @param source       The source location.
     * @param chunkCoords  The chunk boundary.
     */
    public static Location getClosestChunkLocation(Location source, ICoords2Di chunkCoords) {
        return getClosestChunkLocation(source, chunkCoords, new Location(null, 0, 0, 0));
    }

    /**
     * Get the location within a specified chunk that is closest to the specified
     * source location.
     *
     * @param source       The source location.
     * @param chunkCoords  The chunk boundary.
     * @param output       The output location.
     *
     * @return  The output location;
     */
    public static Location getClosestChunkLocation(Location source, ICoords2Di chunkCoords,
                                                   Location output) {
        PreCon.notNull(source, "source");
        PreCon.notNull(source.getWorld(), "source world");
        PreCon.notNull(chunkCoords, "chunkCoords");
        PreCon.notNull(output, "output");

        return getClosestChunkLocation(source, chunkCoords, output, CACHE_COORDS.get());
    }

    /**
     * Get the distance to the closest block within a specified chunk from the specified
     * source location.
     *
     * @param source       The source location.
     * @param chunkCoords  The chunk boundary.
     */
    public static int getChunkDistanceSquared(Location source, ICoords2Di chunkCoords) {
        PreCon.notNull(source, "source");
        PreCon.notNull(source.getWorld(), "source world");
        PreCon.notNull(chunkCoords, "chunkCoords");

        return getChunkDistanceSquared(source, chunkCoords, CACHE_COORDS.get());
    }

    private static ICoords2Di getClosestChunkCorner(ICoords2Di chunkCoords, Location source,
                                                   CacheCoords coords) {
        ICoords2Di closest = null;
        double closestDist = 0D;

        ICoords2Di sourceCoords = set2DCoords(coords.source,
                source.getBlockX(), source.getBlockZ());

        int chunkWorldX = chunkCoords.getX() * 16;
        int chunkWorldZ = chunkCoords.getZ() * 16;

        for (ICoords2Di corner : CHUNK_CORNER_BLOCKS) {

            ICoords2Di cornerLoc = set2DCoords(coords.target,
                    chunkWorldX + corner.getX(), chunkWorldZ + corner.getZ());

            double distance = Coords2Di.distanceSquared(sourceCoords, cornerLoc);
            if (closest == null || closestDist > distance) {
                closest = corner;
                closestDist = distance;
            }
        }
        return closest;
    }

    private static double getCornerDistanceSquared(Location source, ICoords2Di chunkCoords,
                                                   CacheCoords cache) {
        double closestDist = -1D;

        ICoords2Di sourceCoords = set2DCoords(cache.source,
                source.getBlockX(), source.getBlockZ());

        int chunkWorldX = chunkCoords.getX() * 16;
        int chunkWorldZ = chunkCoords.getZ() * 16;

        for (ICoords2Di corner : CHUNK_CORNER_BLOCKS) {

            ICoords2Di cornerLoc = set2DCoords(cache.target,
                    chunkWorldX + corner.getX(), chunkWorldZ + corner.getZ());

            double distance = Coords2Di.distanceSquared(sourceCoords, cornerLoc);
            if (closestDist < 0 || closestDist > distance) {
                closestDist = distance;
            }
        }
        return closestDist;
    }

    private static int getChunkDistanceSquared(Location source, ICoords2Di chunkCoords,
                                               CacheCoords cache) {

        int sourceChunkX = source.getBlockX() >> 4;
        int sourceChunkZ = source.getBlockZ() >> 4;
        int chunkX = chunkCoords.getX();
        int chunkZ = chunkCoords.getZ();

        if (chunkX == sourceChunkX && chunkZ == sourceChunkZ) {
            return 0;
        }

        int faceX = sourceChunkX > chunkX ? 15 : sourceChunkX == chunkX ? -1 : 0;
        int faceZ = sourceChunkZ > chunkZ ? 15 : sourceChunkZ == chunkZ ? -1 : 0;

        int arraySize = 0;

        if (faceX != -1) {
            for (int z = 0; z < 16; z++, arraySize++) {
                cache.chunksX[arraySize] = faceX;
                cache.chunksZ[arraySize] = z;
            }
        }

        if (faceZ != -1) {
            for (int x = 0; x < 16; x++, arraySize++) {
                cache.chunksX[arraySize] = x;
                cache.chunksZ[arraySize] = faceZ;
            }
        }

        int closest = -1;
        for (int i=0; i < arraySize; i++) {
            int x = cache.chunksX[i];
            int z = cache.chunksZ[i];

            int deltaX = (chunkX + x) - source.getBlockX();
            int deltaZ = (chunkZ + z) - source.getBlockZ();
            int distance = deltaX * deltaX + deltaZ * deltaZ;

            if (closest == -1 || distance < closest) {
                closest = distance;
            }
        }

        return closest;
    }

    private static Location getClosestChunkLocation(Location source, ICoords2Di chunkCoords,
                                                   Location output, CacheCoords cache) {
        PreCon.notNull(source, "source");
        PreCon.notNull(source.getWorld(), "source world");
        PreCon.notNull(chunkCoords, "chunkCoords");
        PreCon.notNull(output, "output");

        int sourceChunkX = source.getBlockX() >> 4;
        int sourceChunkZ = source.getBlockZ() >> 4;
        int chunkX = chunkCoords.getX();
        int chunkZ = chunkCoords.getZ();

        if (chunkX == sourceChunkX && chunkZ == sourceChunkZ) {
            return LocationUtils.copy(source, output);
        }

        int faceX = sourceChunkX > chunkX ? 15 : sourceChunkX == chunkX ? -1 : 0;
        int faceZ = sourceChunkZ > chunkZ ? 15 : sourceChunkZ == chunkZ ? -1 : 0;

        int arraySize = 0;

        if (faceX != -1) {
            for (int z = 0; z < 16; z++, arraySize++) {
                cache.chunksX[arraySize] = faceX;
                cache.chunksZ[arraySize] = z;
            }
        }

        if (faceZ != -1) {
            for (int x = 0; x < 16; x++, arraySize++) {
                cache.chunksX[arraySize] = x;
                cache.chunksZ[arraySize] = faceZ;
            }
        }

        int closest = -1;
        int closestX = 0;
        int closestZ = 0;
        for (int i=0; i < arraySize; i++) {
            int x = cache.chunksX[i];
            int z = cache.chunksZ[i];

            int deltaX = (chunkX + x) - source.getBlockX();
            int deltaZ = (chunkZ + z) - source.getBlockZ();
            int distance = deltaX * deltaX + deltaZ * deltaZ;

            if (closest == -1 || distance < closest) {
                closest = distance;
                closestX = x;
                closestZ = z;
            }
        }

        output.setWorld(source.getWorld());
        output.setX(chunkX + closestX);
        output.setY(source.getBlockY());
        output.setZ(chunkZ + closestZ);
        output.setYaw(0f);
        output.setPitch(0f);

        return output;
    }

    private static class CacheCoords {
        final MutableCoords2Di source = new MutableCoords2Di();
        final MutableCoords2Di target = new MutableCoords2Di();
        final MutableCoords2Di chunk = new MutableCoords2Di();
        final int[] chunksX = new int[34];
        final int[] chunksZ = new int[34];
    }

    private static ICoords2Di set2DCoords(MutableCoords2Di coords, int x, int z) {
        coords.setX(x);
        coords.setZ(z);
        return coords;
    }

    private static ICoords2Di setChunkCoords(MutableChunkCoords coords,
                                             World world, int x, int z) {
        coords.setWorld(world);
        coords.setX(x);
        coords.setZ(z);
        return coords;
    }
}
