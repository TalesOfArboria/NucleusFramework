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

package com.jcwhatever.generic.utils.text;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

/**
 * Represents Minecraft formatting codes.
 */
public enum TextColor {
    AQUA           (true,  'b', "\\{AQUA}"),
    BLACK          (true,  '0', "\\{BLACK}"),
    BLUE           (true,  '9', "\\{BLUE}"),
    BOLD           (false, 'l', "\\{BOLD}"),
    DARK_AQUA      (true,  '3', "\\{DARK_AQUA}"),
    DARK_BLUE      (true,  '1', "\\{DARK_BLUE}"),
    DARK_GRAY      (true,  '8', "\\{DARK_GRAY}"),
    DARK_GREEN     (true,  '2', "\\{DARK_GREEN}"),
    DARK_PURPLE    (true,  '5', "\\{DARK_PURPLE}"),
    DARK_RED       (true,  '4', "\\{DARK_RED}"),
    GOLD           (true,  '6', "\\{GOLD}"),
    GRAY           (true,  '7', "\\{GRAY}"),
    GREEN          (true,  'a', "\\{GREEN}"),
    ITALIC         (false, 'o', "\\{ITALIC}"),
    LIGHT_PURPLE   (true,  'd', "\\{LIGHT_PURPLE}"),
    MAGIC          (false, 'k', "\\{MAGIC}"),
    RED            (true,  'c', "\\{RED}"),
    RESET          (false, 'r', "\\{RESET}"),
    STRIKETHROUGH  (false, 'm', "\\{STRIKETHROUGH}"),
    UNDERLINE      (false, 'n', "\\{UNDERLINE}"),
    WHITE          (true,  'f', "\\{WHITE}"),
    YELLOW         (true,  'e', "\\{YELLOW}");

    public static final char FORMAT_CHAR = '\u00A7';
    private static final Map<Character, TextColor> _characterMap = new HashMap<>(20);
    private static final Map<String, TextColor> _nameMap = new HashMap<>(20);

    static {
        for (TextColor color : values()) {
            _characterMap.put(color.getColorChar(), color);
            _nameMap.put(color.name(), color);
        }
    }

    private final Pattern _pattern;
    private final char _colorChar;
    private final String _colorCode;
    private final boolean _isColor;

    TextColor (boolean isColor, char colorChar, String tagPattern) {
        _pattern = Pattern.compile(tagPattern);
        _colorChar = colorChar;
        _colorCode = String.valueOf(FORMAT_CHAR) + colorChar;
        _isColor = isColor;
    }

    /**
     * Get the regex pattern used to format
     * color names into format codes.
     */
    public Pattern getPattern() {
        return _pattern;
    }

    /**
     * Get the character used in the format code.
     */
    public char getColorChar() {
        return _colorChar;
    }

    /**
     * Get the format code.
     */
    public String getColorCode() {
        return _colorCode;
    }

    /**
     * Determine if the {@code TextColor} represents
     * a color. (As apposed to a format)
     */
    public boolean isColor() {
        return _isColor;
    }

    @Override
    public String toString() {
        return _colorCode;
    }

    /**
     * Get a {@code TextColor} from a case insensitive
     * constant name.
     *
     * @param name  The constant name.
     *
     * @return  Null if the name is invalid.
     */
    @Nullable
    public static TextColor fromName(String name) {
        return _nameMap.get(name.toUpperCase());
    }

    /**
     * Remove format codes from a string.
     *
     * @param text  The text to remove format codes from.
     */
    public static String remove(String text) {

        StringBuilder sb = new StringBuilder(text.length());
        char[] chars = text.toCharArray();

        for (int i = 0, last = chars.length - 1; i < chars.length; i++) {
            char ch = chars[i];
            if (ch == FORMAT_CHAR && i != last) {
                char next = chars[i + 1];
                if (_characterMap.containsKey(next)) {
                    i += 1;
                    continue;
                }
            }

            sb.append(ch);
        }

        return sb.toString();
    }

    /**
     * Get the color and formats in effect at the
     * end of a string.
     *
     * @param text  The text to get the end color from.
     *
     * @return  The format codes.
     */
    public static String getEndColor(String text) {

        StringBuilder sb = new StringBuilder(15);
        char[] chars = text.toCharArray();

        for (int i = chars.length - 1; i > -1; i--) {

            char current = chars[i];
            if (current != FORMAT_CHAR || i >= chars.length - 1)
                continue; // finish block

            char next = chars[i + 1];

            TextColor color =  _characterMap.get(next);
            if (color == null)
                continue; // finish block

            sb.insert(0, color.getColorCode());

            if (color.isColor() || color == TextColor.RESET) {
                break;
            }

        }
        return sb.toString();
    }

    /**
     * Get the color and formats in effect at the
     * end of a string.
     *
     * @param stringBuilder  The {@code StringBuilder} to get the end color from.
     *
     * @return  The format codes.
     */
    public static String getEndColor(StringBuilder stringBuilder) {

        StringBuilder sb = new StringBuilder(15);

        int len = stringBuilder.length();

        for (int i = len - 1; i > -1; i--) {

            char current = stringBuilder.charAt(i);
            if (current != FORMAT_CHAR || i >= len - 1)
                continue; // finish block

            char next = stringBuilder.charAt(i + 1);

            TextColor color =  _characterMap.get(next);
            if (color == null)
                continue; // finish block

            sb.insert(0, color.getColorCode());

            if (color.isColor() || color == TextColor.RESET) {
                break;
            }

        }
        return sb.toString();
    }
}