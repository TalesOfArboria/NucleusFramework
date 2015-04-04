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

package com.jcwhatever.nucleus.internal.items.serializer;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.items.meta.InternalItemMetaHandlers;
import com.jcwhatever.nucleus.managed.items.meta.IItemMetaHandler;
import com.jcwhatever.nucleus.managed.items.meta.IItemMetaHandlers;
import com.jcwhatever.nucleus.managed.items.meta.ItemMetaValue;
import com.jcwhatever.nucleus.managed.items.serializer.IItemStackSerializer;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import javax.annotation.Nullable;

/**
 * Serializes {@link org.bukkit.inventory.ItemStack}'s into a string.
 *
 * <p>Add {@link ItemStack}'s to serialize them into the buffer. The result can be obtained
 * by invoking the instances {@link #toString} method or the results can be appended to a
 * {@link java.lang.StringBuilder} provided in the constructor.</p>
 * <p>
 *     Output Format:<br />
 *     MaterialName1[:ByteData][;Amount][{ metaName1: "metaValue1", metaName2: "metaValue2" }],
 *     MaterialName2...
 * </p>
 *
 * @see ItemDeserializer
 * @see InternalItemMetaHandlers
 */
class ItemSerializer implements IItemStackSerializer {

    private final StringBuilder _buffer;
    private final IItemMetaHandlers _metaHandlers;
    private int _itemsAppended = 0;
    private SerializerOutputType _outputType = SerializerOutputType.RAW;

    /**
     * Used to specify if items serialized to string should have color
     * formatting inserted.
     */
    public enum SerializerOutputType {
        RAW,
        COLOR
    }

    /**
     * Constructor.
     *
     * <p>Raw output type.</p>
     *
     * @param size  The initial buffer size
     */
    public ItemSerializer(int size) {
        this(size, SerializerOutputType.RAW);
    }

    /**
     * Constructor.
     *
     * <p>Raw output type.</p>
     *
     * @param buffer  The {@link java.lang.StringBuilder} to append the results to.
     */
    public ItemSerializer(StringBuilder buffer) {
        this(buffer, SerializerOutputType.RAW);
    }

    /**
     * Constructor.
     *
     * @param size          The initial buffer size.
     * @param outputType    The output type.
     */
    public ItemSerializer(int size, SerializerOutputType outputType) {
        PreCon.notNull(outputType);

        _buffer = new StringBuilder(size);
        _metaHandlers = Nucleus.getItemMetaHandlers();
        _outputType = outputType;
    }

    /**
     * Constructor.
     *
     * @param buffer        The {@link java.lang.StringBuilder} to append the results to.
     * @param outputType    The output type.
     */
    public ItemSerializer(StringBuilder buffer, SerializerOutputType outputType) {
        PreCon.notNull(buffer);
        PreCon.notNull(outputType);

        _buffer = buffer;
        _metaHandlers = Nucleus.getItemMetaHandlers();
        _outputType = outputType;
    }

    @Override
    public int size() {
        return _itemsAppended;
    }

    @Override
    public ItemSerializer append(@Nullable ItemStack stack) {

        if (_itemsAppended > 0)
            _buffer.append(", ");

        appendItemStackString(_buffer, stack, _outputType);
        _itemsAppended++;

        return this;
    }

    @Override
    public ItemSerializer appendAll(Collection<? extends ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            append(stack);
        }
        return this;
    }

    @Override
    public <T extends ItemStack> ItemSerializer appendAll(T[] stacks) {
        for (ItemStack stack : stacks) {
            append(stack);
        }
        return this;
    }

    @Override
    public String toString() {
        return _buffer.toString();
    }

    /*
     * Serialize an item stack to a string and append to buffer.
     */
    private void appendItemStackString(StringBuilder buffy, @Nullable ItemStack stack,
                                       SerializerOutputType outputType) {

        if (stack == null) {
            stack = new ItemStack(Material.AIR, -1);
        }

        // material name
        if (outputType == SerializerOutputType.COLOR)
            buffy.append(ChatColor.GREEN);

        buffy.append(stack.getType().name());

        // material data
        short data = stack.getData().getData();
        if (data != 0) {
            if (outputType == SerializerOutputType.COLOR)
                buffy.append(ChatColor.DARK_GRAY);
            buffy.append(':');

            if (outputType == SerializerOutputType.COLOR)
                buffy.append(ChatColor.YELLOW);
            buffy.append(data);

            if (outputType == SerializerOutputType.COLOR)
                buffy.append(ChatColor.GRAY);
        }
        else if (outputType == SerializerOutputType.COLOR) {
            buffy.append(ChatColor.GRAY);
        }

        // quantity

        if (stack.getAmount() != 1) {
            buffy.append(';');

            if (outputType == SerializerOutputType.COLOR)
                buffy.append(ChatColor.AQUA);

            buffy.append(stack.getAmount());
        }

        Collection<IItemMetaHandler> handlers = _metaHandlers.getHandlers();

        List<ItemMetaValue> metaObjects = new ArrayList<>(10);

        for (IItemMetaHandler handler : handlers) {
            metaObjects.addAll(handler.getMeta(stack));
        }

        if (!metaObjects.isEmpty()) {
            buffy.append('{');

            for (int i=0, last = metaObjects.size() - 1; i < metaObjects.size(); i++) {

                ItemMetaValue meta = metaObjects.get(i);

                buffy.append(meta.getName());

                buffy.append(':');
                buffy.append('"');

                Matcher matcher = TextUtils.PATTERN_DOUBLE_QUOTE.matcher(meta.getRawData());
                buffy.append(matcher.replaceAll("\\\""));

                buffy.append('"');

                if (i < last) {
                    buffy.append(',');
                }
            }

            buffy.append('}');
        }
    }
}

