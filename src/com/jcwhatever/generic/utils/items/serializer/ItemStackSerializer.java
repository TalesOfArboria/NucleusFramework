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

package com.jcwhatever.generic.utils.items.serializer;

import com.jcwhatever.generic.utils.items.serializer.metahandlers.ItemMetaObject;
import com.jcwhatever.generic.utils.items.serializer.metahandlers.IMetaHandler;
import com.jcwhatever.generic.utils.items.serializer.metahandlers.ItemMetaHandlerManager;
import com.jcwhatever.generic.utils.text.TextUtils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Serializes {@code ItemStack}'s into a string.
 *
 * <p>
 *     Add item stacks to serialize them. The result can be obtained
 *     by calling the instances {@code toString} method or the results
 *     can be appended to a {@code StringBuilder} provided in the
 *     constructor.
 * </p>
 * <p>
 *     Output Format:
 *     MaterialName1[:ByteData][;Amount][{ metaName1: "metaValue1", metaName2: "metaValue2" }], MaterialName2...
 * </p>
 */
public class ItemStackSerializer {

    private StringBuilder _buffy;
    private int _itemsAppended = 0;
    private SerializerOutputType _outputType = SerializerOutputType.RAW;

    /**
     * Used to specify if item stacks serialized to string
     * should have color formatting inserted.
     */
    public enum SerializerOutputType {
        RAW,
        COLOR
    }

    /**
     * Constructor.
     *
     * <p>
     *     Raw output type.
     * </p>
     *
     * @param size  The initial buffer size
     */
    public ItemStackSerializer(int size) {
        _buffy = new StringBuilder(size);
    }

    /**
     * Constructor.
     *
     * @param size        The initial buffer size
     * @param outputType  The output type.
     */
    public ItemStackSerializer(int size, SerializerOutputType outputType) {
        _buffy = new StringBuilder(size);
        _outputType = outputType;
    }

    /**
     * Constructor.
     *
     * <p>
     *     Raw output type.
     * </p>
     *
     * @param buffy  The {@code StringBuilder} to append the results to.
     */
    public ItemStackSerializer(StringBuilder buffy) {
        _buffy = buffy;
    }

    /**
     * Constructor.
     *
     * @param buffy       The The {@code StringBuilder} to append the results to.
     * @param outputType  The output type.
     */
    public ItemStackSerializer(StringBuilder buffy, SerializerOutputType outputType) {
        _buffy = buffy;
        _outputType = outputType;
    }

    /**
     * Serialize an {@code ItemStack} and append the results to
     * the current results.
     *
     * @param stack  The {@code ItemStack} to serialize.
     *
     * @return Self
     */
    public <T extends ItemStack> ItemStackSerializer append(T stack) {
        if (_itemsAppended > 0)
            _buffy.append(", ");

        appendItemStackString(_buffy, stack, _outputType);
        _itemsAppended++;
        return this;
    }

    /**
     * Serialize a collection of {@code ItemStack}'s and append the results
     * to the current results.
     *
     * @param stacks  The {@code ItemStack}'s to serialize.
     *
     * @return  Self
     */
    public ItemStackSerializer appendAll(Collection<? extends ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            append(stack);
        }
        return this;
    }

    /**
     * Serialize an array of {@code ItemStack}'s and append the results
     * to the current results.
     *
     * @param stacks  The array of {@code ItemStack}'s to serialize.
     *
     * @param <T>  The ItemStack type
     *
     * @return  Self
     */
    public <T extends ItemStack> ItemStackSerializer appendAll(T[] stacks) {
        for (ItemStack stack : stacks) {
            append(stack);
        }
        return this;
    }

    /**
     * Get the number of {@code ItemStacks} serialized.
     */
    public int getTotalItems() {
        return _itemsAppended;
    }

    /**
     * Return the serialized result.
     */
    @Override
    public String toString() {
        return _buffy.toString();
    }

    /*
     * Serialize an item stack to a string and append to buffer.
     */
    private static void appendItemStackString(StringBuilder buffy, ItemStack stack, SerializerOutputType outputType) {

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

        List<IMetaHandler> handlers = ItemMetaHandlerManager.getHandlers();

        List<ItemMetaObject> metaObjects = new ArrayList<>(10);

        for (IMetaHandler handler : handlers) {
            metaObjects.addAll(handler.getMeta(stack));
        }

        if (!metaObjects.isEmpty()) {
            buffy.append('{');

            for (int i=0, last = metaObjects.size() - 1; i < metaObjects.size(); i++) {

                ItemMetaObject meta = metaObjects.get(i);

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
