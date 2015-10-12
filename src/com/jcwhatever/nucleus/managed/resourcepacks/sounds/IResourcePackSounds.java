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

package com.jcwhatever.nucleus.managed.resourcepacks.sounds;

import com.jcwhatever.nucleus.managed.resourcepacks.IResourcePack;
import com.jcwhatever.nucleus.managed.resourcepacks.sounds.types.IResourceSound;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Interface for the sounds that are in a resource pack.
 */
public interface IResourcePackSounds {

    /**
     * Get the resource pack the sounds are from.
     */
    IResourcePack getResourcePack();

    /**
     * Get a resource sound by name.
     *
     * @param name  The name of the sound.
     */
    @Nullable
    IResourceSound get(String name);

    /**
     * Get all resource sounds.
     */
    Collection<IResourceSound> getAll();

    /**
     * Get all resource sounds.
     *
     * @param output  The output collection to add results to.
     *
     * @return  The output collection.
     */
    <T extends Collection<IResourceSound>> T getAll(T output);

    /**
     * Get resource sounds by type.
     *
     * @param type  The type to look for.
     */
    <T extends IResourceSound> Collection<T> getTypes(Class<T> type);

    /**
     * Get resource sounds by type.
     *
     * @param type    The type to look for.
     * @param output  The output collection to add results to.
     *
     * @return  The output collection.
     */
    <T extends IResourceSound, E extends Collection<T>> E getTypes(Class<T> type, E output);
}
