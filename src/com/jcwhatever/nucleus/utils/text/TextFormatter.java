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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Replaces tags in text.
 *
 * <p>Tags consist of enclosing curly braces with tag text inside.</p>
 *
 * <p>Numbers are used to mark the index of the parameter that should be
 * placed where the tag is. ie {0} is parameter 1 (index 0)</p>
 *
 * <p>{@link TextColor} constant names are automatically added as tags and
 * replaced with the equivalent color code. ie {RED} is replaced with the
 * Minecraft color code for red.</p>
 *
 * <p>Tags that don't match a parameter index or other defined tag formatter
 * are ignored and added as is.</p>
 *
 * <p>Comments can be added to tags by inserting a colon. Useful for including
 * the purpose of the format tag for documentation purposes.
 * ie {0: This part of the tag is a comment and is ignored}</p>
 *
 */
public class TextFormatter {

    private static Map<String, ITagFormatter> _colors = new HashMap<>(TextColor.values().length);

    static {
        for (final TextColor color : TextColor.values()) {
            _colors.put(color.name(), new ITagFormatter() {

                @Override
                public String getTag() {
                    return color.name();
                }

                @Override
                public void append(StringBuilder sb, String tag) {
                    sb.append(color.getColorCode());
                }
            });
        }
    }

    private final Map<String, ITagFormatter> _formatters = new HashMap<>(20);
    private final StringBuilder _textBuffer = new StringBuilder(100);
    private final StringBuilder _tagBuffer = new StringBuilder(25);

    /**
     * Constructor.
     */
    public TextFormatter(){}

    /**
     * Constructor.
     *
     * @param formatters  A collection of formatters to include.
     */
    public TextFormatter(Collection<? extends ITagFormatter> formatters) {
        for (ITagFormatter formatter : formatters) {
            _formatters.put(formatter.getTag(), formatter);
        }
    }

    /**
     * Constructor.
     *
     * @param formatters  The formatters to include.
     */
    public TextFormatter(ITagFormatter... formatters) {
        for (ITagFormatter formatter : formatters) {
            _formatters.put(formatter.getTag(), formatter);
        }
    }

    /**
     * Get a default tag formatter by case sensitive tag.
     *
     * @param tag  The tag text.
     */
    public ITagFormatter getFormatter(String tag) {
        return _formatters.get(tag);
    }

    /**
     * Get all default tag formatters.
     */
    public List<ITagFormatter> getFormatters() {
        return new ArrayList<>(_formatters.values());
    }

    /**
     * Remove a default tag formatter.
     *
     * @param tag  The tag to remove.
     */
    public void removeFormatter(String tag) {
        _formatters.remove(tag);
    }

    /**
     * Add a default tag formatter.
     *
     * @param formatter  The tag formatter to add.
     */
    public void addFormatter(ITagFormatter formatter) {
        _formatters.put(formatter.getTag(), formatter);
    }

    /**
     * Format text.
     *
     * @param template  The template text.
     * @param params    The parameters to add.
     *
     * @return  The formatted string.
     */
    public String format(String template, Object... params) {
        PreCon.notNull(template);

        return format(_formatters, template, params);
    }

    /**
     * Format text using a custom set of formatters.
     *
     * @param formatters  The formatter map to use.
     * @param template    The template text.
     * @param params      The parameters to add.
     *
     * @return  The formatted string.
     */
    public String format(Map<String, ITagFormatter> formatters, String template, Object... params) {
        PreCon.notNull(template);

        if (template.indexOf('{') == -1 && template.indexOf('\\') == -1)
            return template;

        _textBuffer.setLength(0);

        for (int i=0; i < template.length(); i++) {
            char ch = template.charAt(i);

            // check for tag opening
            if (ch == '{') {

                // parse tag
                String tag = parseTag(template, i);

                // update index position
                i += _tagBuffer.length();

                // template ended before tag was closed
                if (tag == null) {
                    _textBuffer.append('{');
                    _textBuffer.append(_tagBuffer);
                }
                // tag parsed
                else {
                    i++; // add 1 for closing brace
                    appendReplacement(_textBuffer, tag, params, formatters);
                }

            }
            else if (ch == '\\' && i < template.length() - 1) {

                // make sure the backslash isn't escaped
                int s = i;
                int bsCount = 0;
                while (s != 0) {
                    if (template.charAt(s - 1) == '\\') {
                        bsCount++;
                    }
                    else {
                        break;
                    }
                    s--;
                }
                if (bsCount % 2 != 0)
                    continue;

                // look at next character
                char next = template.charAt(i + 1);

                // handle new line character
                if (next == 'n' || next == 'r') {

                    _textBuffer.append('\n');
                    i++;
                }
                // handle unicode
                else if (next == 'u') {

                    i++;
                    char unicode = parseUnicode(template, i);
                    if (unicode == 0) {
                        // append non unicode text
                        _textBuffer.append("\\u");
                    }
                    else {
                        _textBuffer.append(unicode);
                        i+= Math.min(4, template.length() - i);
                    }
                }
                // unused backslash
                else {
                    _textBuffer.append(ch);
                }
            }
            else {
                // append next character
                _textBuffer.append(ch);
            }
        }

        return _textBuffer.toString();
    }

    /**
     * Parse a unicode character from the string
     */
    private char parseUnicode(String template, int currentIndex) {
        _tagBuffer.setLength(0);

        for (int i=currentIndex + 1, readCount=0; i < template.length(); i++, readCount++) {

            if (readCount == 4) {
                break;
            }
            else {
                char ch = template.charAt(i);
                if ("01234567890abcdefABCDEF".indexOf(ch) == -1)
                    return 0;
                _tagBuffer.append(ch);
            }
        }

        if (_tagBuffer.length() == 4) {

            try {
                return (char) Integer.parseInt(_tagBuffer.toString(), 16);
            } catch (NumberFormatException ignore) {
                return 0;
            }
        }

        return 0;
    }

    /*
     * Parse a single tag from the template
     */
    private String parseTag(String template, int currentIndex) {

        _tagBuffer.setLength(0);

        for (int i=currentIndex + 1; i < template.length(); i++) {

            char ch = template.charAt(i);

            if (ch == '}') {
                return _tagBuffer.toString();
            }
            else {
                _tagBuffer.append(ch);
            }
        }

        return null;
    }

    /*
     * Append replacement text for a tag
     */
    private void appendReplacement(StringBuilder sb, String tag, Object[] params, Map<String, ITagFormatter> formatters) {

        boolean isNumber = !tag.isEmpty();

        _tagBuffer.setLength(0);

        // parse out tag from comment section
        for (int i=0; i < tag.length(); i++) {

            char ch = tag.charAt(i);

            // done at comment character
            if (ch == ':') {
                break;
            }
            // append next tag character
            else {
                _tagBuffer.append(ch);

                // check if the character is a number
                if (isNumber && !Character.isDigit(ch)) {
                    isNumber = false;
                }
            }
        }

        String parsedTag = _tagBuffer.toString();

        if (isNumber) {
            int index = Integer.parseInt(parsedTag);

            // make sure number is in the range of the provided parameters.
            if (params.length <= index) {
                reappendTag(sb, tag);
            }
            // replace number with parameter argument.
            else {

                String toAppend = String.valueOf(params[index]);
                String lastColors = null;

                // make sure colors from inserted text do not continue
                // into template text
                if (toAppend.indexOf(TextColor.FORMAT_CHAR) != -1) {
                    lastColors = TextColor.getEndColor(sb);
                }

                // append parameter argument
                sb.append(params[index]);

                // append template color
                if (lastColors != null && !lastColors.isEmpty()) {
                    sb.append(lastColors);
                }
            }
        }
        else {

            // check for custom formatter
            ITagFormatter formatter = formatters.get(parsedTag);
            if (formatter == null) {
                // check for color formatter
                formatter = _colors.get(parsedTag);
            }

            if (formatter != null) {
                // formatter appends replacement text to format buffer
                formatter.append(sb, tag);
            }
            else {
                // no formatter, append tag to result buffer
                reappendTag(sb, tag);
            }
        }
    }

    /*
     * Append raw tag to string builder
     */
    private void reappendTag(StringBuilder sb, String tag) {
        sb.append('{');
        sb.append(tag);
        sb.append('}');
    }

    /**
     * Defines a format tag.
     */
    public static interface ITagFormatter {

        /**
         * Get the format tag.
         */
        String getTag();

        /**
         * Append replacement text into the provided
         * string builder. The parsed tag is provided for reference.
         *
         * @param sb      The string builder to append to.
         * @param rawTag  The tag that was parsed.
         */
        void append(StringBuilder sb, String rawTag);
    }
}

