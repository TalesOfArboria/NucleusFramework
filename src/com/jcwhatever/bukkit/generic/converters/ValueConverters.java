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


package com.jcwhatever.bukkit.generic.converters;

public class ValueConverters {

    /**
     * Converts between a Potion and potion id.
     */
    public static final PotionIDConverter POTION_ID = new PotionIDConverter();

    /**
     * Convert between Minecraft material ID's and Bukkit Material enum.
     */
    public static final ItemMaterialIDConverter ITEM_MATERIAL_ID = new ItemMaterialIDConverter();

    /**
     * Convert between chat color codes that use the '&' character and valid chat color codes.
     */
    public static final AlternativeChatColorConverter ALT_CHAT_COLOR = new AlternativeChatColorConverter();

    /**
     * Converts a material name to a bukkit material
     */
    public static final ItemNameMaterialConverter ITEM_NAME_MATERIAL = new ItemNameMaterialConverter();

    /**
     * Converts between a Bukkit Material enum constant name as a string to MaterialData.
     * Also includes non Bukkit Material enum names.
     */
    public static final ItemNameMaterialDataConverter ITEM_NAME_MATERIALDATA = new ItemNameMaterialDataConverter();
}
