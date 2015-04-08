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

package com.jcwhatever.nucleus.internal.managed.items.meta;

import com.jcwhatever.nucleus.managed.items.meta.IItemMetaHandler;
import com.jcwhatever.nucleus.managed.items.meta.IItemMetaHandlers;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Manages {@link org.bukkit.inventory.ItemStack} meta data handlers.
 */
public final class InternalItemMetaHandlers implements IItemMetaHandlers {

    private final Map<String, IItemMetaHandler> _handlers = new HashMap<>(15);

    public InternalItemMetaHandlers() {
        register(new ItemColor());
        register(new ItemDisplayName());
        register(new ItemEnchantment());
        register(new ItemLore());
        register(new ItemBookAuthor());
        register(new ItemBookTitle());
        register(new ItemBookPage());
    }

    @Override
    @Nullable
    public IItemMetaHandler getHandler(String metaName) {
        return _handlers.get(metaName);
    }

    @Override
    public List<IItemMetaHandler> getHandlers() {
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
    protected InternalItemMetaHandlers register(IItemMetaHandler handler) {
        PreCon.notNull(handler);

        _handlers.put(handler.getMetaName(), handler);

        return this;
    }
}
