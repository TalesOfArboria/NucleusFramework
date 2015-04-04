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

import com.jcwhatever.nucleus.managed.items.meta.IItemMetaHandler;
import com.jcwhatever.nucleus.managed.items.meta.ItemMetaValue;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import com.jcwhatever.nucleus.utils.materials.Materials;

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles {@link org.bukkit.inventory.ItemStack} color meta, as a byte number [0-15],
 * the name of the color, or in the case of leather, the RGB hex value.
 *
 * @see InternalItemMetaHandlers
 */
class ItemColor implements IItemMetaHandler {

    @Override
    public String getMetaName() {
        return "color";
    }

    @Override
    public boolean canHandle(ItemStack itemStack) {
        PreCon.notNull(itemStack);

        return itemStack.getItemMeta() instanceof LeatherArmorMeta ||
                Materials.hasColorData(itemStack.getType());
    }

    @Override
    public boolean apply(ItemStack itemStack, ItemMetaValue meta) {
        PreCon.notNull(itemStack);
        PreCon.notNull(meta);

        if (!meta.getName().equals(getMetaName()))
            return false;

        // RGB Color
        if (meta.getRawData().startsWith("#")) {

            if (!(itemStack.getItemMeta() instanceof LeatherArmorMeta))
                return false;

            String rawColor = meta.getRawData().substring(1);


            int intColor;
            try {
                intColor = Integer.parseInt(rawColor, 16);
            }
            catch (NumberFormatException e) {
                e.printStackTrace();
                return false;
            }

            ItemStackUtils.setColor(itemStack, Color.fromRGB(intColor));

            return true; // finished
        }


        // Minecraft color

        byte code = -1;

        // try byte code first
        if (meta.getRawData().length() < 3) {

            try {
                code = Byte.parseByte(meta.getRawData());
            }
            catch (NumberFormatException ignore) {
                // do nothing
            }
        }

        // try color name second
        if (code == -1) {
            try {
                ByteColor color = ByteColor.valueOf(meta.getRawData().toUpperCase());
                code = color.getData();
            }
            catch (Exception ignore) {
                return false;
            }
        }

        itemStack.setDurability(code);

        return true;
    }

    @Override
    public List<ItemMetaValue> getMeta(ItemStack itemStack) {
        PreCon.notNull(itemStack);

        List<ItemMetaValue> result = new ArrayList<>(1);

        if (itemStack.getItemMeta() instanceof LeatherArmorMeta) {

            Color color = ItemStackUtils.getColor(itemStack);
            if (color == null)
                throw new AssertionError();

            String code = '#' + Integer.toHexString(color.asRGB());

            result.add(new ItemMetaValue(getMetaName(), code));

            return result;
        }

        if (Materials.hasColorData(itemStack.getType())) {
            result.add(new ItemMetaValue(getMetaName(), String.valueOf(itemStack.getDurability())));
        }

        return result;
    }

    private enum ByteColor {
        WHITE       (0),
        ORANGE      (1),
        MAGENTA     (2),
        LIGHT_BLUE  (3),
        YELLOW      (4),
        LIME        (5),
        PINK        (6),
        GRAY        (7),
        LIGHT_GRAY  (8),
        CYAN        (9),
        PURPLE      (10),
        BLUE        (11),
        BROWN       (12),
        GREEN       (13),
        RED         (14),
        BLACK       (15);

        private byte _data;

        ByteColor(int data) {
            _data = (byte)data;
        }

        public byte getData() {
            return _data;
        }
    }
}
