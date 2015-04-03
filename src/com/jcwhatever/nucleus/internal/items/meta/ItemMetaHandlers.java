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

package com.jcwhatever.nucleus.internal.items.meta;

import com.jcwhatever.nucleus.utils.PreCon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Manages {@link org.bukkit.inventory.ItemStack} meta data handlers.
 */
public class ItemMetaHandlers {

    private static ItemMetaHandlers _global;

    /**
     * Get the global singleton instance.
     */
    public static ItemMetaHandlers getGlobal() {
        if (_global == null)
            _global = createDefault();

        return _global;
    }

    /**
     * Create a new instance and fill with the default meta handlers.
     */
    public static ItemMetaHandlers createDefault() {
        return fillDefault(new ItemMetaHandlers());
    }

    /**
     * Fill an instance with the default meta handlers.
     *
     * @param output  The output instance to fill.
     *
     * @return  The output instance.
     */
    public static ItemMetaHandlers fillDefault(ItemMetaHandlers output) {
        return output
                .register(new ColorHandler())
                .register(new DisplayNameHandler())
                .register(new EnchantmentHandler())
                .register(new LoreHandler())
                .register(new BookAuthorHandler())
                .register(new BookTitleHandler())
                .register(new BookPageHandler());
    }

    private final Map<String, IMetaHandler> _handlers = new HashMap<>(15);

    /**
     * Get a meta handler by its case sensitive meta name.
     *
     * @param metaName  The meta name.
     *
     * @return  The meta handler or null if not found.
     */
    @Nullable
    public IMetaHandler getHandler(String metaName) {
        return _handlers.get(metaName);
    }

    /**
     * Get all registered meta handlers.
     */
    public List<IMetaHandler> getHandlers() {
        return new ArrayList<>(_handlers.values());
    }

    /**
     * Register a meta data handler.
     *
     * <p>If a meta data handler with the same meta name is already
     * registered, it is overwritten.</p>
     *
     * @param handler  The handler to register.
     *
     * @return  Self for chaining.
     */
    protected ItemMetaHandlers register(IMetaHandler handler) {
        PreCon.notNull(handler);

        _handlers.put(handler.getMetaName(), handler);

        return this;
    }
}
