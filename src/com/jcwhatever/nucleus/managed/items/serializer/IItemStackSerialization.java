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

package com.jcwhatever.nucleus.managed.items.serializer;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;

import org.bukkit.inventory.ItemStack;

/**
 * Interface for an {@link org.bukkit.inventory.ItemStack} serialization manager.
 *
 * @see Nucleus#getItemSerialization
 * @see ItemStackUtils#serialize
 * @see ItemStackUtils#parse
 */
public interface IItemStackSerialization {

    /**
     * Parse an item stack string into a deserializer.
     *
     * @param itemStackString  The item stack string to parse and deserialize.
     *
     * @return  The {@link IItemStackDeserializer} used to deserialize the string.
     *
     * @throws InvalidItemStackStringException if the string could not be parsed as a valid item stack string.
     */
    IItemStackDeserializer parse(String itemStackString) throws InvalidItemStackStringException;

    /**
     * Create a new {@link IItemStackSerializer}.
     */
    IItemStackSerializer createSerializer();

    /**
     * Create a new {@link IItemStackSerializer}.
     *
     * @param size  The expected number of {@link ItemStack} to be serialized. Used to optimize
     *              initial capacities.
     */
    IItemStackSerializer createSerializer(int size);

    /**
     * Create a new {@link IItemStackSerializer}.
     *
     * @param buffer  The {@link StringBuilder} to append the results to.
     */
    IItemStackSerializer createSerializer(StringBuilder buffer);
}
