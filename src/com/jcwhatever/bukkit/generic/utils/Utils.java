/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Generic utilities
 */
public class Utils {

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
     * Parse the {@code UUID} from the supplied string.
     * If parsing fails, null is returned.
     *
     * @param id  The id to parse.
     */
    @Nullable
    public static UUID getId(String id) {

        try {
            return UUID.fromString(id);
        }
        catch (IllegalArgumentException iae) {
            return null;
        }
    }

    /**
     * Parse the {@code UUID}'s from the supplied collection
     * of strings. If a string cannot be parsed, it is not
     * included in the results.
     *
     * @param uuids  The ids to parse.
     */
    public static List<UUID> getIds(Collection<String> uuids) {

        List<UUID> results = new ArrayList<UUID>(uuids.size());

        for (String raw : uuids) {
            UUID id = Utils.getId(raw);
            if (id == null)
                continue;

            results.add(id);
        }
        return results;
    }

    /**
     * Parse the {@code UUID}'s from the supplied comma
     * delimited string of ids. If an Id cannot be parsed,
     * it is not included in the results.
     *
     * @param uuidStr  The string Ids to parse.
     */
    public static List<UUID> getIds(String uuidStr) {
        uuidStr = uuidStr.toLowerCase();
        String[] rawIds = TextUtils.PATTERN_COMMA.split(uuidStr);

        List<UUID> results = new ArrayList<UUID>(rawIds.length);

        for (String rawId : rawIds) {
            String trimmedId = rawId.trim();

            UUID id = Utils.getId(trimmedId);
            if (id == null)
                continue;

            results.add(id);
        }

        return results;
    }

}

