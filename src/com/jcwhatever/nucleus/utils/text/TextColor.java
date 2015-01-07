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

import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
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
    private static final StringBuilder _largeBuffer = new StringBuilder(30);
    private static final StringBuilder _smallBuffer = new StringBuilder(6);

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
     * @param charArray  The text to remove format codes from.
     */
    public static String remove(char[] charArray) {
        PreCon.notNull(charArray);

        return remove(new CharArraySequence(charArray));
    }

    /**
     * Remove format codes from a {@code CharSequence}.
     *
     * @param charSequence  The text to remove format codes from.
     */
    public static String remove(CharSequence charSequence) {
        PreCon.notNull(charSequence);

        StringBuilder sb;

        if (Bukkit.isPrimaryThread()) {
            sb = _largeBuffer;
            sb.setLength(0);
        }
        else {
            sb = new StringBuilder(charSequence.length());
        }

        int len = charSequence.length();

        for (int i = 0, last = len - 1; i < len; i++) {

            char ch = charSequence.charAt(i);

            if (ch == FORMAT_CHAR && i != last) {

                char next = charSequence.charAt(i + 1);

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
     * Removes colors from the supplied {@code char[]} and
     * stores them in a {@code TextColorMap} keyed to the index location
     * of the color in the resulting colorless string.
     *
     * <p>The resulting colorless string can be retrieved from the
     * {@code TextColorMap} by invoking the {@code #getText} method.</p>
     *
     * @param charArray  The {@code CharSequence}.
     */
    public static TextColorMap separate(char[] charArray) {
        PreCon.notNull(charArray);

        return separate(new CharArraySequence(charArray));
    }

    /**
     * Removes colors from the supplied {@code CharSequence} and
     * stores them in a {@code TextColorMap} keyed to the index location
     * of the color in the resulting colorless string.
     *
     * <p>The resulting colorless string can be retrieved from the
     * {@code TextColorMap} by invoking the {@code #getText} method.</p>
     *
     * @param charSequence  The {@code CharSequence}.
     */
    public static TextColorMap separate(CharSequence charSequence) {
        PreCon.notNull(charSequence);

        StringBuilder sb;
        StringBuilder colorBuffer;
        int len = charSequence.length();

        if (Bukkit.isPrimaryThread()) {
            sb = _largeBuffer;
            sb.setLength(0);
            sb.ensureCapacity(len);

            colorBuffer = _smallBuffer;
            colorBuffer.setLength(0);
        }
        else {
            sb = new StringBuilder(len);
            colorBuffer = new StringBuilder(6);
        }

        TextColorMap colorMap = new TextColorMap();

        int virtualIndex = 0;

        for (int i = 0, last = len - 1; i < len; i++) {
            char ch = 0;

            while (i < last) {

                char next;

                if ((ch = charSequence.charAt(i)) == FORMAT_CHAR
                        && _characterMap.containsKey(next = charSequence.charAt(i + 1))) {

                    colorBuffer.append(FORMAT_CHAR);
                    colorBuffer.append(next);

                    i = Math.min(i + 2, last);

                    if (i == last)
                        ch = 0;
                }
                else {
                    break;
                }
            }

            if (colorBuffer.length() != 0) {
                colorMap.put(virtualIndex, colorBuffer.toString());
                colorBuffer.setLength(0);
            }

            virtualIndex++;
            if (ch != 0)
                sb.append(ch);
        }

        colorMap.setText(sb.toString());
        return colorMap;
    }

    /**
     * Determine if a character is a valid formatting
     * character.
     *
     * @param ch  The character to check.
     */
    public static boolean isFormatChar(char ch) {
        return _characterMap.containsKey(ch);
    }

    /**
     * Get the {@code TextColor} that represents the
     * format code character.
     *
     * @param ch  The format character to check.
     *
     * @return The {@code TextColor} or null if the character is not
     * a recognized format code character.
     */
    @Nullable
    public static TextColor fromFormatChar(char ch) {
        return _characterMap.get(ch);
    }

    /**
     * Get the color and formats in effect at the
     * beginning of a string.
     *
     * @param charArray  The text to get the end color from.
     *
     * @return  The format codes.
     */
    public static String getColorAt(int index, char[] charArray) {
        PreCon.notNull(charArray);

        return getEndColor(new CharArraySequence(charArray), index);
    }

    /**
     * Get the color and formats in effect at the
     * beginning of a {@code CharSequence}.
     *
     * @param charSequence  The text to get the end color from.
     *
     * @return  The format codes.
     */
    public static String getColorAt(int index, CharSequence charSequence) {
        PreCon.positiveNumber(index, "index");
        PreCon.lessThan(index, charSequence.length(), "index");
        PreCon.notNull(charSequence);

        return getEndColor(charSequence, index);
    }

    /**
     * Get the color and formats in effect at the end of a {@code char[]}.
     *
     * @param charArray  The {@code char[]} to get the end color from.
     *
     * @return  The format codes.
     */
    public static String getEndColor(final char[] charArray) {
        PreCon.notNull(charArray);

        return getEndColor(new CharArraySequence(charArray), charArray.length);
    }

    /**
     * Get the color and formats in effect at the end of a {@code CharSequence}.
     *
     * @param charSequence  The {@code CharSequence} to get the end color from.
     *
     * @return  The format codes.
     */
    public static String getEndColor(CharSequence charSequence) {
        PreCon.notNull(charSequence);

        return getEndColor(charSequence, charSequence.length());
    }

    /**
     * Get the color and formats in effect at the end of a {@code CharSequence}.
     */
    static String getEndColor(CharSequence charSequence, int len) {
        PreCon.notNull(charSequence);

        if (len == 0)
            return "";

        StringBuilder sb = Bukkit.isPrimaryThread() ? _smallBuffer : new StringBuilder(6);
        sb.setLength(0);

        for (int i = len - 1; i > -1; i--) {

            char current = charSequence.charAt(i);
            if (current != FORMAT_CHAR || i >= len - 1)
                continue; // finish block

            char next = charSequence.charAt(i + 1);

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
     * Stores color codes and their index location within a string.
     */
    public static class TextColorMap extends TreeMap<Integer, String> {

        private String _text;

        /**
         * The text of the color map.
         */
        public String getText() {
            return _text;
        }

        /**
         * Set the text of the color map.
         *
         * @param text  The text.
         */
        protected void setText(String text) {
            _text = text;
        }
    }
}