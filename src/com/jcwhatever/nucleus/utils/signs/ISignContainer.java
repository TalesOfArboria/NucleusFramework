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

package com.jcwhatever.nucleus.utils.signs;

import com.jcwhatever.nucleus.storage.IDataNode;

import org.bukkit.Location;
import org.bukkit.block.Sign;

import javax.annotation.Nullable;

/**
 * Container for a sign.
 *
 * <p>Used to pass signs into {@link SignHandler} event methods and allow
 * retrieving and saving from the signs data node.</p>
 *
 * @see ISignManager
 */
public interface ISignContainer {

    /**
     * Get the encapsulated sign.
     *
     * @return  The {@link Sign} or null if a sign was not found.
     */
    @Nullable
    Sign getSign();

    /**
     * Get the data node where meta for the sign can be stored.
     */
    IDataNode getMetaNode();

    /**
     * Get the location of the sign.
     */
    Location getLocation();

    /**
     * Copy the location values of the sign to an output {@link Location}.
     *
     * @param output  The output {@link Location}.
     *
     * @return  The output {@link Location}.
     */
    Location getLocation(Location output);

    /**
     * Determine if changes have been made that require a data node save.
     *
     * <p>Only detects changes made by the {@link ISignContainer} instance.</p>
     */
    boolean isDirty();

    /**
     * Get a line from the sign as is.
     *
     * @param index  The index of the line. (0-3)
     */
    String getLine(int index);

    /**
     * Set the line on a sign.
     *
     * <p>Causes {@link #isDirty} to return true.</p>
     *
     * @param index  The index of the line. (0-3)
     * @param line   The new text line.
     */
    void setLine(int index, String line);

    /**
     * Get a line from the sign with color formatting removed.
     *
     * @param index  The index of the line. (0-3)
     */
    String getRawLine(int index);

    /**
     * Get a saved line from the data node.
     *
     * @param index  The index of the line. (0-3)
     *
     * @return  The text saved in the signs data node or null if there is no
     * data node set.
     */
    @Nullable
    String getSavedLine(int index);

    /**
     * Set a saved line from the data node.
     *
     * @param index  The index of the line. (0-3)
     * @param line   The new text line.
     *
     * @return  True if the data node was updated, false if there is no data node to set.
     */
    boolean setSavedLine(int index, String line);

    /**
     * Update changes to the sign in the world.
     */
    boolean update();

    /**
     * Remove the sign from the world and the data node.
     */
    void remove();

    /**
     * Save changes to the signs data node.
     *
     * @return  True if the changes were saved, false if the sign has no data node to save to.
     */
    boolean save();
}
