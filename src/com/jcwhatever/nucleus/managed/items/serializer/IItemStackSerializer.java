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

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import javax.annotation.Nullable;

/**
 * Interface for an {@link ItemStack} serializer.
 *
 * @see IItemStackSerialization
 */
public interface IItemStackSerializer {

    /**
     * Get the number of {@link org.bukkit.inventory.ItemStack}'s serialized.
     */
    int size();

    /**
     * Serialize an {@link org.bukkit.inventory.ItemStack} and append the results to
     * the current results.
     *
     * @param stack  The {@link org.bukkit.inventory.ItemStack} to serialize. Null values
     *               are converted to {@link Material#AIR} with an amount of -1.
     *
     * @return Self for chaining.
     */
    IItemStackSerializer append(@Nullable ItemStack stack);

    /**
     * Serialize a collection of {@link org.bukkit.inventory.ItemStack}'s and append the results
     * to the current results.
     *
     * @param stacks  The {@link org.bukkit.inventory.ItemStack}'s to serialize.
     *
     * @return  Self for chaining.
     */
    IItemStackSerializer appendAll(Collection<? extends ItemStack> stacks);

    /**
     * Serialize an array of {@link org.bukkit.inventory.ItemStack}'s and append the results
     * to the current results.
     *
     * @param stacks  The array of {@link org.bukkit.inventory.ItemStack}'s to serialize.
     *
     * @param <T>  The ItemStack type
     *
     * @return  Self for chaining.
     */
    <T extends ItemStack> IItemStackSerializer appendAll(T[] stacks);

    /**
     * Return the serialized result.
     */
    @Override
    String toString();
}
