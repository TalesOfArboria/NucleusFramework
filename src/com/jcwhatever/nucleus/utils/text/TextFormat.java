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

import com.jcwhatever.nucleus.collections.wrap.IteratorWrapper;
import com.jcwhatever.nucleus.utils.ArrayUtils;
import com.jcwhatever.nucleus.utils.CollectionUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.ThreadSingletons;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Represents Text formatting codes and provides related utilities.
 */
public class TextFormat {

    private static Map<String, TextFormat> _nameMap;
    private static Map<Character, TextFormat> _characterMap;
    private static String _formatChars = "";

    public static final char CHAR = '\u00A7';

    public static final TextFormat BOLD = new TextFormat('l', "BOLD", "bold");
    public static final TextFormat ITALIC = new TextFormat('o', "ITALIC", "italic");
    public static final TextFormat MAGIC = new TextFormat('k', "MAGIC", "obfuscated");
    public static final TextFormat STRIKETHROUGH = new TextFormat('m',"STRIKETHROUGH", "strikethrough");
    public static final TextFormat UNDERLINE = new TextFormat('n', "UNDERLINE", "underlined");
    public static final TextFormat RESET = new TextFormat('r', "RESET", "reset");


    private static final ThreadSingletons<StringBuilder> SMALL_BUFFERS = new ThreadSingletons<>(
            new ThreadSingletons.ISingletonFactory<StringBuilder>() {
                @Override
                public StringBuilder create(Thread thread) {
                    return new StringBuilder(6);
                }
            });

    private static final ThreadSingletons<StringBuilder> LARGE_BUFFERS = new ThreadSingletons<>(
            new ThreadSingletons.ISingletonFactory<StringBuilder>() {
                @Override
                public StringBuilder create(Thread thread) {
                    return new StringBuilder(0);
                }
            });

    private final char _formatChar;
    private final String _formatCode;
    private final String _tagName;
    private final String _minecraftName;

    TextFormat (char formatChar, String tagName, String minecraftName) {
        _formatChar = formatChar;
        _formatCode = String.valueOf(CHAR) + formatChar;
        _tagName = tagName;
        _minecraftName = minecraftName;

        if (_nameMap == null)
            _nameMap = new HashMap<>(25);

        _nameMap.put(tagName, this);
    }

    /**
     * Determine if the {@link TextFormat} has a
     * format code.
     *
     * <p>This exists in case text formats are added by Minecraft
     * that do not have format codes. (i.e 16-bit color)</p>
     */
    public boolean hasFormatCode() {
        return true;
    }

    /**
     * Determine if the {@link TextFormat} has a single character format
     * code.
     *
     * <p>This exists in case text formats are added by Minecraft
     * that do not have format codes. (i.e. 16-bit color)</p>
     */
    public boolean hasFormatChar() {
        return true;
    }

    /**
     * Get the {@link TextFormat} format code. If there is no
     * format code, an empty string is returned.
     */
    public String getFormatCode() {
        return _formatCode;
    }

    /**
     * Get the {@link TextFormat} format character. If there is no
     * format character, 0 is returned.
     */
    public char getFormatChar() {
        return _formatChar;
    }

    /**
     * Get the formats tag name which is used by
     * {@link TextFormatter} to identify formats inserted into
     * a string to be formatted.
     */
    public String getTagName() {
        return _tagName;
    }

    /**
     * Get the formats Minecraft name. This is the name of the
     * formats property or value used in Minecraft JSON strings.
     */
    public String getMinecraftName() {
        return _minecraftName;
    }

    @Override
    public String toString() {
        return _formatCode;
    }

    /**
     * Get the total number of available format codes.
     */
    public static int totalCodes() {
        return 22;
    }

    /**
     * Get a {@link TextFormat} from a case tag name.
     *
     * @param name  The tag name.
     *
     * @return  Null if the name is invalid.
     */
    @Nullable
    public static TextFormat fromName(String name) {
        PreCon.notNull(name);

        assert _nameMap != null;
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
     * Remove format codes from a {@link java.lang.CharSequence}.
     *
     * @param charSequence  The text to remove format codes from.
     */
    public static String remove(CharSequence charSequence) {
        PreCon.notNull(charSequence);

        int len = charSequence.length();
        StringBuilder sb = getLargeBuffer(len);

        loadCharacterMap();

        for (int i = 0, last = len - 1; i < len; i++) {

            char ch = charSequence.charAt(i);

            if (ch == CHAR && i != last) {

                char next = charSequence.charAt(i + 1);

                if (_formatChars.indexOf(next) != -1) {
                    i += 1;
                    continue;
                }
            }

            sb.append(ch);
        }

        return sb.toString();
    }

    /**
     * Removes formats from the supplied {@link char[]} and
     * stores them in a {@link TextFormatMap} keyed to the index location
     * of the format in the resulting format-less string.
     *
     * <p>The resulting format-less string can be retrieved from the
     * {@link TextFormatMap} by invoking the {@link TextFormatMap#getText} method.</p>
     *
     * @param charArray  The {@link java.lang.CharSequence}.
     */
    public static TextFormatMap separate(char[] charArray) {
        PreCon.notNull(charArray);

        return separate(new CharArraySequence(charArray));
    }

    /**
     * Removes formats from the supplied {@link java.lang.CharSequence} and
     * stores them in a {@link TextFormatMap} keyed to the index location
     * of the format in the resulting format-less string.
     *
     * <p>The resulting format-less string can be retrieved from the
     * {@link TextFormatMap} by invoking the {@link TextFormatMap#getText} method.</p>
     *
     * @param charSequence  The {@link CharSequence}.
     */
    public static TextFormatMap separate(CharSequence charSequence) {
        PreCon.notNull(charSequence);

        int len = charSequence.length();
        StringBuilder sb = getLargeBuffer(len);
        StringBuilder formatBuffer = getSmallBuffer();

        TextFormatMap formatMap = new TextFormatMap();
        loadCharacterMap();

        int virtualIndex = 0;

        for (int i = 0, last = len - 1; i < len; i++) {
            char ch = 0;

            while (i < last) {

                char next;

                if ((ch = charSequence.charAt(i)) == CHAR
                        && _formatChars.indexOf(next = charSequence.charAt(i + 1)) != -1) {

                    formatBuffer.append(CHAR);
                    formatBuffer.append(next);

                    i = Math.min(i + 2, last);

                    if (i == last)
                        ch = 0;
                }
                else {
                    break;
                }
            }

            if (formatBuffer.length() != 0) {
                formatMap.put(virtualIndex, formatBuffer.toString());
                formatBuffer.setLength(0);
            }

            virtualIndex++;
            if (ch != 0)
                sb.append(ch);
        }

        formatMap.setText(sb.toString());
        return formatMap;
    }

    /**
     * Determine if a character is a valid formatting
     * character.
     *
     * @param ch  The character to check.
     */
    public static boolean isFormatChar(char ch) {
        loadCharacterMap();
        return _formatChars.indexOf(ch) != -1;
    }

    /**
     * Get the {@link TextFormat} that represents the
     * format code character.
     *
     * @param ch  The format character to check.
     *
     * @return The {@link TextFormat} or null if the character is not
     * a recognized format code character.
     */
    @Nullable
    public static TextFormat fromFormatChar(char ch) {
        return loadCharacterMap().get(ch);
    }

    /**
     * Get the formats in effect at the
     * beginning of a string.
     *
     * @param charArray  The text to get the end format from.
     *
     * @return  The format codes.
     */
    public static TextFormats getFormatAt(int index, char[] charArray) {
        PreCon.notNull(charArray);

        return getEndFormat(new CharArraySequence(charArray), index);
    }

    /**
     * Get the formats in effect at the
     * beginning of a {@link java.lang.CharSequence}.
     *
     * @param charSequence  The text to get the end format from.
     *
     * @return  The format codes.
     */
    public static TextFormats getFormatAt(int index, CharSequence charSequence) {
        PreCon.positiveNumber(index, "index");
        PreCon.lessThan(index, charSequence.length(), "index");
        PreCon.notNull(charSequence);

        return getEndFormat(charSequence, index);
    }

    /**
     * Get the formats in effect at the end of a {@link char[]}.
     *
     * @param charArray  The {@link char[]} to get the end format from.
     *
     * @return  The format codes.
     */
    public static TextFormats getEndFormat(final char[] charArray) {
        PreCon.notNull(charArray);

        return getEndFormat(new CharArraySequence(charArray), charArray.length);
    }

    /**
     * Get the formats in effect at the end of a {@link CharSequence}.
     *
     * @param charSequence  The {@link CharSequence} to get the end format from.
     *
     * @return  The format codes.
     */
    public static TextFormats getEndFormat(CharSequence charSequence) {
        PreCon.notNull(charSequence);

        return getEndFormat(charSequence, charSequence.length());
    }

    /**
     * Get the formats in effect at the end of a {@link CharSequence}.
     */
    static TextFormats getEndFormat(CharSequence charSequence, int len) {
        PreCon.notNull(charSequence);

        if (len == 0)
            return new TextFormats("", null);

        StringBuilder sb = getSmallBuffer();

        List<TextFormat> formats = new ArrayList<>(2);
        Map<Character, TextFormat> characterMap = loadCharacterMap();

        for (int i = len - 1; i > -1; i--) {

            char current = charSequence.charAt(i);
            if (current != CHAR || i >= len - 1)
                continue; // finish block

            char next = charSequence.charAt(i + 1);

            TextFormat format =  characterMap.get(next);
            if (format == null)
                continue; // finish block

            sb.insert(0, format.getFormatCode());
            formats.add(format);

            if (format instanceof TextColor || format == TextFormat.RESET) {
                break;
            }
        }
        return new TextFormats(sb.toString(), formats);
    }

    /**
     * Translate all format characters in a character sequence that are followed
     * by valid format characters into '&' character.
     *
     * @param charSequence  The character sequence.
     *
     * @return  The translated text.
     */
    public static String translateFormatChars(CharSequence charSequence) {
        PreCon.notNull(charSequence);

        if (charSequence.length() == 0)
            return "";

        int len = charSequence.length();
        StringBuilder sb = getLargeBuffer(charSequence.length());
        loadCharacterMap();

        for (int i=0, last = len - 1; i < len; i++) {

            char ch = charSequence.charAt(i);

            if (i < last && ch == CHAR &&
                    _formatChars.indexOf(charSequence.charAt(i + 1)) != -1) {
                sb.append('&');
            }
            else {
                sb.append(ch);
            }
        }

        return sb.toString();
    }

    /**
     * Translate all format characters in a character sequence that are followed
     * by valid format characters into '&' character.
     *
     * @param charSequence  The character sequence.
     *
     * @return  The translated text.
     */
    public static String untranslateFormatChars(CharSequence charSequence) {
        PreCon.notNull(charSequence);

        if (charSequence.length() == 0)
            return "";

        int len = charSequence.length();
        StringBuilder sb = getLargeBuffer(len);
        loadCharacterMap();

        for (int i=0, last = len - 1; i < len; i++) {

            char ch = charSequence.charAt(i);

            if (i < last && ch == '&' &&
                    _formatChars.indexOf(charSequence.charAt(i + 1)) != -1) {
                sb.append(CHAR);
            }
            else {
                sb.append(ch);
            }
        }

        return sb.toString();
    }

    /**
     * Translate all format codes into format tags (i.e. '{RED}').
     *
     * @param charSequence  The character sequence.
     *
     * @return  The translated text.
     */
    public static String translateCodes(CharSequence charSequence) {
        PreCon.notNull(charSequence);

        if (charSequence.length() == 0)
            return "";

        int len = charSequence.length();
        StringBuilder sb = getLargeBuffer(len + 100);

        for (int i=0, last = len - 1; i < len; i++) {

            char ch = charSequence.charAt(i);

            if (ch == CHAR && i < last) {

                TextFormat format = loadCharacterMap().get(charSequence.charAt(i + 1));
                if (format != null) {

                    sb.append('{');
                    sb.append(format._tagName);
                    sb.append('}');
                    i++;
                    continue;
                }
            }

            sb.append(ch);
        }

        return sb.toString();
    }

    /**
     * Get an iterator for the {@link TextFormat}'s with a format code.
     */
    public static Iterator<TextFormat> formatIterator() {
        return new IteratorWrapper<TextFormat>() {

            Iterator<TextFormat> iterator = loadCharacterMap().values().iterator();

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            protected Iterator<TextFormat> iterator() {
                return iterator;
            }
        };
    }

    // get small string builder buffer
    private static StringBuilder getSmallBuffer() {
        StringBuilder sb = SMALL_BUFFERS.get();
        sb.setLength(0);
        return sb;
    }

    // get large string builder buffer
    private static StringBuilder getLargeBuffer(int length) {

        if (length > 1024)
            return new StringBuilder(length);

        StringBuilder sb = LARGE_BUFFERS.get();

        sb.setLength(0);
        sb.ensureCapacity(length);

        return sb;
    }

    /**
     * Stores color codes and their index location within a string.
     */
    public static class TextFormatMap extends TreeMap<Integer, String> {

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

    /**
     * Holds a collection of {@link TextFormat}'s.
     *
     * <p>Calling the {@link #toString} method returns the
     * format codes as a string.</p>
     */
    public static class TextFormats {
        private final List<TextFormat> formats;
        private final Object _sync = new Object();
        private String string;

        TextFormats(CharSequence string, @Nullable List<TextFormat> formats) {
            PreCon.notNull(string);

            this.formats = formats != null
                    ? formats
                    : CollectionUtils.unmodifiableList(new ArrayList<TextFormat>(0));
            this.string = string.toString();
        }

        /**
         * Constructor.
         *
         * @param formats  The {@link TextFormat}'s.
         */
        public TextFormats(TextFormat... formats) {
            PreCon.notNull(formats);

            this.formats = ArrayUtils.asList(formats);
        }

        /**
         * Constructor.
         *
         * @param formats  The collection of {@link TextFormat}'s.
         */
        public TextFormats(Collection<TextFormat> formats) {
            PreCon.notNull(formats);

            this.formats = new ArrayList<>(formats);
        }

        /**
         * Constructor.
         *
         * @param formatChars  The format characters.
         */
        public TextFormats(char... formatChars) {
            PreCon.notNull(formatChars);

            TextFormat[] formats = new TextFormat[formatChars.length];
            StringBuilder buffer = new StringBuilder(formatChars.length * 2);

            for (int i=0; i < formatChars.length; i++) {
                formats[i] = fromFormatChar(formatChars[i]);
                if (formats[i] == null) {
                    throw new IllegalArgumentException("'" + formatChars[i] +
                            "' is not a recognized format character.");
                }

                buffer.append(CHAR);
                buffer.append(formatChars[i]);
            }

            this.string = buffer.toString();
            this.formats = ArrayUtils.asList(formats);
        }

        /**
         * Get the list of text formats.
         */
        public List<TextFormat> getFormats() {
            return CollectionUtils.unmodifiableList(formats);
        }

        @Override
        public String toString() {
            if (string == null) {
                synchronized (_sync) {
                    if (string != null)
                        return string;

                    StringBuilder buffer = new StringBuilder(formats.size() * 2);

                    for (TextFormat format : formats) {
                        if (format.hasFormatCode())
                            buffer.append(format.getFormatCode());
                    }

                    string = buffer.toString();
                }
            }
            return string;
        }
    }

    private static Map<Character, TextFormat> loadCharacterMap() {

        if (_characterMap == null) {
            _characterMap = new HashMap<>(25);

            register(TextColor.AQUA);
            register(TextColor.BLACK);
            register(TextColor.BLUE);
            register(TextColor.DARK_AQUA);
            register(TextColor.DARK_BLUE);
            register(TextColor.DARK_GRAY);
            register(TextColor.DARK_GREEN);
            register(TextColor.DARK_PURPLE);
            register(TextColor.DARK_RED);
            register(TextColor.GOLD);
            register(TextColor.GRAY);
            register(TextColor.GREEN);
            register(TextColor.LIGHT_PURPLE);
            register(TextColor.RED);
            register(TextColor.WHITE);
            register(TextColor.YELLOW);

            register(TextFormat.BOLD);
            register(TextFormat.ITALIC);
            register(TextFormat.STRIKETHROUGH);
            register(TextFormat.UNDERLINE);
            register(TextFormat.RESET);
            register(TextFormat.MAGIC);
        }

        return _characterMap;
    }

    private static void register(TextFormat format) {

        if (!format.hasFormatCode())
            return;

        if (_characterMap == null)
            _characterMap = new HashMap<>(30);

        _characterMap.put(format.getFormatChar(), format);
        _formatChars += format.getFormatChar();
    }
}
