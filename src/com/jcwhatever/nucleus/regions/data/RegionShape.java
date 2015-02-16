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

package com.jcwhatever.nucleus.regions.data;

/**
 * Describes the basic shape of a region.
 */
public enum RegionShape {
    /**
     * The region is an undefinable cuboid shape.
     */
    CUBOID           (FlatnessPosition.NONE, Flatness.NOT_FLAT, ShapeDirection.NONE),
    /**
     * The region is a single block.
     */
    POINT            (FlatnessPosition.NONE, Flatness.NOT_FLAT, ShapeDirection.NONE),
    /**
     * The region is 1 block thick in both the north/south and west/east
     * direction. Shaped like a pipe that extends up/down.
     */
    VERTICAL_LINE    (FlatnessPosition.VERTICAL, Flatness.LINE, ShapeDirection.UP_DOWN),
    /**
     * The region is 1 block thick in both the west/east and up/down direction.
     * Shaped like a horizontally positioned pipe extending in the north/south direction.
     */
    NORTH_SOUTH_LINE (FlatnessPosition.HORIZONTAL, Flatness.LINE, ShapeDirection.NORTH_SOUTH),
    /**
     * The region is 1 block thick in both the north/south and up/down direction.
     * Shaped like a horizontally positioned pipe extending in the west/east direction.
     */
    WEST_EAST_LINE   (FlatnessPosition.HORIZONTAL, Flatness.LINE, ShapeDirection.WEST_EAST),
    /**
     * The regions is only 1 block thick in the north/south direction. Is flat
     * vertically.
     */
    FLAT_NORTH_SOUTH (FlatnessPosition.VERTICAL, Flatness.FLAT, ShapeDirection.NORTH_SOUTH),
    /**
     * The region is only 1 block thick in the west/east direction. Is flat
     * vertically.
     */
    FLAT_WEST_EAST   (FlatnessPosition.VERTICAL, Flatness.FLAT, ShapeDirection.WEST_EAST),
    /**
     * The region is only 1 block thick in the up/down direction. Is flat
     * horizontally.
     */
    FLAT_HORIZONTAL  (FlatnessPosition.HORIZONTAL, Flatness.FLAT, ShapeDirection.UP_DOWN);

    /**
     * Describes the common flatness shape of a {@link Flatness} constant.
     */
    public enum FlatnessPosition {
        /**
         * The shape is not horizontal or vertical.
         */
        NONE,
        /**
         * The shape is horizontal.
         */
        HORIZONTAL,
        /**
         * The shape is vertical.
         */
        VERTICAL
    }

    /**
     * Describes the flatness of the shape.
     */
    public enum Flatness {
        /**
         * The shape has no flatness.
         */
        NOT_FLAT,
        /**
         * The shape is flat (1 block thick) in 1 dimension.
         */
        FLAT,
        /**
         * The shape is flat (1 block thick) in 2 dimensions.
         */
        LINE
    }

    /**
     * Describes the direction of the shape.
     */
    public enum ShapeDirection {
        /**
         * The shape does not have a direction.
         */
        NONE,
        /**
         * The shape is in the north/south direction. (Z axis)
         */
        NORTH_SOUTH,
        /**
         * The shape is in the west/east direction (X axis)
         */
        WEST_EAST,
        /**
         * The shape is in the up/down direction (Y axis)
         */
        UP_DOWN
    }

    private final FlatnessPosition _position;
    private final Flatness _flatness;
    private final ShapeDirection _direction;

    RegionShape(FlatnessPosition shape, Flatness flatness, ShapeDirection direction) {
        _position = shape;
        _flatness = flatness;
        _direction = direction;
    }

    /**
     * Get the shape position in regards to its flatness.
     */
    public FlatnessPosition getPosition() {
        return _position;
    }

    /**
     * Get the flatness of the shape.
     */
    public Flatness getFlatness() {
        return _flatness;
    }

    /**
     * Get the direction of the shape.
     */
    public ShapeDirection getDirection() {
        return _direction;
    }
}
