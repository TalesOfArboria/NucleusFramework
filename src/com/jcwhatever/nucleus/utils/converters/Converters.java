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


package com.jcwhatever.nucleus.utils.converters;

public class Converters {

    /**
     * Converts between a Potion and potion id.
     */
    public static final PotionIDConverter POTION_ID = new PotionIDConverter();

    /**
     * Converts between a Potion and potion id.
     */
    public static final PotionConverter POTION = new PotionConverter();

    /**
     * Convert between Minecraft material ID's and Bukkit Material enum.
     */
    public static final MaterialIDConverter MATERIAL_ID = new MaterialIDConverter();

    /**
     * Convert between Minecraft material ID's and Bukkit Material enum.
     */
    public static final MaterialConverter MATERIAL = new MaterialConverter();

    /**
     * Convert between Minecraft material ID's and Bukkit Material enum.
     */
    public static final MaterialDataConverter MATERIAL_DATA = new MaterialDataConverter();

    /**
     * Convert chat color codes that use the '&' character to valid chat color codes.
     */
    public static final AltColorConverter ALT_COLOR = new AltColorConverter();

    /**
     * Convert between chat color codes that use the valid chat color codes to '&'.
     */
    public static final DeAltColorConverter DE_ALT_COLOR = new DeAltColorConverter();
}
