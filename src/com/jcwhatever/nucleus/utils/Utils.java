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


package com.jcwhatever.nucleus.utils;

import com.jcwhatever.nucleus.mixins.IWrapper;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

/**
 * Generic utilities.
 */
public final class Utils {

    private Utils() {}

    /**
     * Execute a command as console.
     *
     * @param cmd  The command to execute.
     */
    public static void executeAsConsole(String cmd) {
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
    }

    /**
     * Execute a command as a player.
     *
     * @param p    The player.
     * @param cmd  The command to execute.
     */
    public static void executeAsPlayer(Player p, String cmd) {
        p.performCommand(cmd);
    }

    /**
     * Unwrap a potentially wrapped object.
     *
     * <p>If the object implements {@link IWrapper} and the wrapped instance is
     * of the expected type, the encapsulated object is returned.</p>
     *
     * <p>If the object does not implement {@link IWrapper} but the object is
     * of the expected type, the object is returned.</p>
     *
     * <p>If the encapsulated object and the object are not of the expected type,
     * null is returned.</p>
     *
     * @param object  The potential wrapper.
     * @param clazz   The expected wrapped class.
     *
     * @param <T>  The expected wrapped type.
     *
     * @return  The type instance or null.
     */
    @Nullable
    public static <T> T unwrap(@Nullable Object object, Class<T> clazz) {
        PreCon.notNull(clazz);
        if (object == null)
            return null;

        if (object instanceof IWrapper) {
            Object wrapped = ((IWrapper) object).getHandle();

            if (clazz.isInstance(wrapped))
                return clazz.cast(wrapped);
        }

        if (clazz.isInstance(object)) {
            return clazz.cast(object);
        }

        return null;
    }
}

