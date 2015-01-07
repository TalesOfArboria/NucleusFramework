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

package com.jcwhatever.nucleus.utils.text;

/**
 * Represents Minecraft color codes.
 */
public class TextColor extends TextFormat {

    public static final TextColor AQUA = new TextColor('b', "AQUA", "aqua");
    public static final TextColor BLACK = new TextColor('0', "BLACK", "black");
    public static final TextColor BLUE = new TextColor('9', "BLUE", "blue");
    public static final TextColor DARK_AQUA = new TextColor('3', "DARK_AQUA", "dark_aqua");
    public static final TextColor DARK_BLUE = new TextColor('1', "DARK_BLUE", "dark_blue");
    public static final TextColor DARK_GRAY = new TextColor('8', "DARK_GRAY", "dark_gray");
    public static final TextColor DARK_GREEN = new TextColor('2', "DARK_GREEN", "dark_green");
    public static final TextColor DARK_PURPLE = new TextColor('5', "DARK_PURPLE", "dark_purple");
    public static final TextColor DARK_RED = new TextColor('4', "DARK_RED", "dark_red");
    public static final TextColor GOLD = new TextColor('6', "GOLD", "gold");
    public static final TextColor GRAY = new TextColor('7', "GRAY", "gray");
    public static final TextColor GREEN = new TextColor('a', "GREEN", "green");
    public static final TextColor LIGHT_PURPLE = new TextColor('d', "LIGHT_PURPLE", "light_purple");
    public static final TextColor RED = new TextColor('c', "RED", "red");
    public static final TextColor WHITE = new TextColor('f', "WHITE", "white");
    public static final TextColor YELLOW = new TextColor('e', "YELLOW", "yellow");

    TextColor (char colorChar, String tagName, String minecraftName) {
        super(colorChar, tagName, minecraftName);
    }
}