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

package com.jcwhatever.nucleus.providers.kits;

import com.jcwhatever.nucleus.Nucleus;

import java.util.Collection;
import javax.annotation.Nullable;

/**
 * Static convenience methods for accessing the kit provider.
 */
public final class Kits {

    private Kits() {}

    /**
     * Creates a new kit in the kit providers global context.
     *
     * @param name The name of the kit.
     *
     * @return The created kit or null if the kit name already exists.
     */
    public static IKit add(String name) {
        return provider().add(name);
    }

    /**
     * Get an {@link IModifiableKit} instance for the given git.
     *
     * @param kit The {@link IKit} to modify.
     *
     * @return The modifiable kit or null if the manager does not allow modifying the kit.
     */
    public static IModifiableKit modifyKit(IKit kit) {
        return provider().modifyKit(kit);
    }

    /**
     * Determine if the provider contains a kit in the global context.
     *
     * @param name  The name of the kit.
     */
    public static boolean contains(String name) {
        return provider().contains(name);
    }

    /**
     * Get a kit from the global context by name.
     *
     * @param name  The name of the item.
     *
     * @return  Null if the item was not found.
     */
    @Nullable
    public static IKit get(String name) {
        return provider().get(name);
    }

    /**
     * Get all kits from the global context.
     */
    public static Collection<IKit> getAll() {
        return provider().getAll();
    }

    /**
     * Remove a kit from the global context.
     *
     * @param name  The name of the item.
     *
     * @return  True if found and removed.
     */
    public static boolean remove(String name) {
        return provider().remove(name);
    }

    /**
     * Get the kit provider.
     */
    public static IKitProvider provider() {
        return Nucleus.getProviderManager().getKitProvider();
    }
}
