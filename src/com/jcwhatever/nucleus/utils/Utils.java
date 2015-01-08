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

import com.jcwhatever.nucleus.utils.validate.IValidator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Generic utilities
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
     * Search a collection for valid candidates using an
     * {@code IValidator} to validate.
     *
     * @param searchCandidates  The search candidates.
     * @param validator         The validator.
     */
    public static <T> List<T> search(Collection<T> searchCandidates, IValidator<T> validator) {
        PreCon.notNull(searchCandidates);
        PreCon.notNull(validator);

        List<T> result = new ArrayList<>(searchCandidates.size());

        for (T candidate : searchCandidates) {

            if (validator.isValid(candidate)) {
                result.add(candidate);
            }
        }

        return result;
    }

}

