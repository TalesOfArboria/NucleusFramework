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

package com.jcwhatever.nucleus.providers.friends;

import com.jcwhatever.nucleus.providers.IProvider;

import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Interface for a friend management provider.
 *
 * <p>Should be implemented by a type that extends {@link com.jcwhatever.nucleus.providers.Provider}.</p>
 */
public interface IFriendsProvider extends IProvider {

    /**
     * Get the default friendship context.
     */
    IFriendsContext getDefaultContext();

    /**
     * Create a transient context that is not stored.
     *
     * @return  The new transient context or null if failed.
     */
    @Nullable
    IFriendsContext createTransientContext();

    /**
     * Creates a new named friendship context.
     *
     * @param name  The name of the context.
     *
     * @return  The new named context or null if failed.
     */
    @Nullable
    IFriendsContext createContext(String name);

    /**
     * Get a named friendship context.
     *
     * @param name  The name of the context.
     *
     * @return  The context or null if not found.
     */
    @Nullable
    IFriendsContext getContext(String name);

    /**
     * Get all friendship contexts.
     */
    Collection<IFriendsContext> getContexts();

    /**
     * Remove a named context.
     *
     * @param name  The name of the context.
     *
     * @return  True if found and removed, otherwise false.
     */
    boolean removeContext(String name);

    /**
     * Get the available registered friend levels from lowest
     * level to highest level.
     */
    List<IFriendLevel> getLevels();

    /**
     * Get a friend level by name.
     *
     * @param name  The case insensitive name of the level.
     *
     * @return  The level or null if not found.
     */
    @Nullable
    IFriendLevel getLevel(String name);

    /**
     * Register a friendship level.
     *
     * @param level  The level to register.
     *
     * @return  True if the level was registered, false if a level with
     * the name is already registered.
     */
    boolean registerLevel(IFriendLevel level);

    /**
     * Unregister a friendship level.
     *
     * @param level  The level to remove.
     *
     * @return  True if the level was found and removed, otherwise false.
     */
    boolean unregisterLevel(IFriendLevel level);
}