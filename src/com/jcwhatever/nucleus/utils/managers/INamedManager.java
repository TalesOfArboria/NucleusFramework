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

package com.jcwhatever.nucleus.utils.managers;

import com.jcwhatever.nucleus.mixins.INamed;

import java.util.Collection;
import javax.annotation.Nullable;

/**
 * Interface for a manager of {@link INamed} objects.
 */
public interface INamedManager<E extends INamed> {

    /**
     * Determine if the manager contains an item.
     *
     * @param name  The name of the item.
     */
    boolean contains(String name);

    /**
     * Get an item by name.
     *
     * @param name  The name of the item.
     *
     * @return  Null if the item was not found.
     */
    @Nullable
    E get(String name);

    /**
     * Get all managed items.
     */
    Collection<E> getAll();

    /**
     * Get all managed items.
     *
     * @param output  The output collection to place results into.
     *
     * @return  The output collection.
     */
    <T extends Collection<E>> T getAll(T output);

    /**
     * Remove an item.
     *
     * @param name  The name of the item.
     *
     * @return  True if found and removed.
     */
    boolean remove(String name);
}
