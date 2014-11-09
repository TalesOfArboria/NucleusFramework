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

package com.jcwhatever.bukkit.generic.items.serializer.metahandlers;

import com.jcwhatever.bukkit.generic.utils.PreCon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Manages {@code ItemStack} meta data handlers.
 */
public class MetaHandlerManager {

    private static final Map<String, MetaHandler> _handlers = new HashMap<>(20);

    static {
        register(new ColorHandler());
        register(new DisplayNameHandler());
        register(new EnchantmentHandler());
        register(new LoreHandler());
        register(new BookAuthorHandler());
        register(new BookTitleHandler());
        register(new BookPageHandler());
    }

    /**
     * Register a meta data handler.
     * <p>
     *     If a meta data handler with the same meta name is already
     *     registered, it is overwritten.
     * </p>
     *
     * @param handler  The handler to register.
     */
    public static void register(MetaHandler handler) {
        PreCon.notNull(handler);

        _handlers.put(handler.getMetaName(), handler);
    }

    /**
     * Get a meta handler by its case sensitive meta name.
     *
     * @param metaName  The meta name.
     *
     * @return  Null if not found.
     */
    @Nullable
    public static MetaHandler getHandler(String metaName) {
        return _handlers.get(metaName);
    }

    /**
     * Get all registered meta handlers.
     */
    public static List<MetaHandler> getHandlers() {
        return new ArrayList<>(_handlers.values());
    }
}
