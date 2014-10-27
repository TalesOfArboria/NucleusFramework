/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.items;

import com.jcwhatever.bukkit.generic.items.ItemStackHelper.DisplayNameResult;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Serializes {@code ItemStack}'s into a string.
 *
 * <p>
 *     Add item stacks to serialize them. The result can be obtained
 *     by calling the instances {@code toString} method or the results
 *     can be appended to a {@code StringBuilder} provided in the
 *     constructor.
 * </p>
 */
public class ItemStackSerializer {

    private static final Pattern PATTERN_TEXT_FILTER = Pattern.compile("[,:;]");
    private static final Pattern PATTERN_APPEND_LORE = Pattern.compile("[,\\|]");
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
    public ItemStackSerializer append(ItemStack stack) {
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

        buffy.append(';');
        if (stack.getAmount() != 1) {

            if (outputType == SerializerOutputType.COLOR)
                buffy.append(ChatColor.AQUA);

            buffy.append(stack.getAmount());
        }

        // enchantments
        buffy.append(';');
        appendEnchantments(buffy, stack);

        // displayname
        buffy.append(';');
        appendDisplayName(buffy, stack);

        // color
        buffy.append(';');
        appendColor(buffy, stack);

        buffy.append(';');
        appendLore(buffy, stack);

        while (buffy.length() > 0 && buffy.charAt(buffy.length() - 1) == ';') {
            buffy.setLength(buffy.length() - 1);
        }
    }


    private static void appendEnchantments(StringBuilder buffy, ItemStack stack) {
        Set<Enchantment> enchantments = stack.getEnchantments().keySet();
        if (enchantments.size() == 0)
            return;

        int i=0;
        int last = enchantments.size() - 1;
        for (Enchantment enchant: enchantments) {
            int level = stack.getEnchantmentLevel(enchant);

            appendEnchantment(buffy, enchant, level);
            if (i < last) {
                buffy.append(':');
            }

            i++;
        }
    }


    private static void appendEnchantment(StringBuilder buffy, Enchantment enchant, int level) {
        buffy.append(enchant.getName());
        buffy.append('-');
        buffy.append(level);
    }

    private static void appendDisplayName(StringBuilder buffy, ItemStack stack) {

        String stackDisplayName = ItemStackHelper.getDisplayName(stack, DisplayNameResult.OPTIONAL);
        if (stackDisplayName == null)
            return;

        Matcher matcher = PATTERN_TEXT_FILTER.matcher(stackDisplayName);
        stackDisplayName = matcher.replaceAll("");

        ItemStack checkStack = new ItemStack(stack.getType());

        if (stackDisplayName.equals(ItemStackHelper.getDisplayName(checkStack, DisplayNameResult.OPTIONAL)))
            return;

        buffy.append(stackDisplayName);
    }


    private static void appendColor(StringBuilder buffy, ItemStack stack) {
        Color color = ItemStackHelper.getColor(stack);
        if (color == null)
            return;

        buffy.append(Integer.toHexString(color.asRGB()));
    }

    private static void appendLore(StringBuilder buffy, ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null)
            return;

        List<String> lore = meta.getLore();
        if (lore == null)
            return;


        for (int i=0, last = lore.size() - 1; i < lore.size(); i++) {

            String line = lore.get(i);
            if (line == null)
                continue;

            Matcher matcher = PATTERN_APPEND_LORE.matcher(line);
            buffy.append(matcher.replaceAll(""));

            if (i < last) {
                buffy.append('|');
            }
        }
    }

}
