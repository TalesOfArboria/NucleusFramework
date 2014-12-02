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


package com.jcwhatever.bukkit.generic.utils;

import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.language.Localized;
import com.jcwhatever.bukkit.generic.utils.TextUtils.FormatPattern.FormatEntry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

/**
 * Static methods to aid in string related tasks
 */
public class TextUtils {

    private TextUtils() {}

    @Localizable static final String _FORMAT_TEMPLATE_RAW = "{0}";
    @Localizable static final String _FORMAT_TEMPLATE_HEADER = "{AQUA}{0}";
    @Localizable static final String _FORMAT_TEMPLATE_SUB_HEADER = "{LIGHT_PURPLE}{ITALIC}{0}:";
    @Localizable static final String _FORMAT_TEMPLATE_ITEM = "{YELLOW}{0}";
    @Localizable static final String _FORMAT_TEMPLATE_DEFINITION = "{GOLD}{0}{AQUA} - {GRAY}{1}";
    @Localizable static final String _FORMAT_TEMPLATE_ITEM_DESCRIPTION = "{YELLOW}{0}{AQUA} - {GRAY}{1}";

    public static final Pattern PATTERN_DOT = Pattern.compile("\\.");
    public static final Pattern PATTERN_COMMA = Pattern.compile(",");
    public static final Pattern PATTERN_COLON = Pattern.compile(":");
    public static final Pattern PATTERN_SEMI_COLON = Pattern.compile(";");
    public static final Pattern PATTERN_DASH = Pattern.compile("-");
    public static final Pattern PATTERN_SPACE = Pattern.compile(" ");
    public static final Pattern PATTERN_EQUALS = Pattern.compile("=");
    public static final Pattern PATTERN_PERCENT = Pattern.compile("%");
    public static final Pattern PATTERN_PIPE = Pattern.compile("\\|");
    public static final Pattern PATTERN_NUMBERS = Pattern.compile("\\d+");
    public static final Pattern PATTERN_DECIMAL_NUMBERS = Pattern.compile("\\d+(\\.\\d+)");
    public static final Pattern PATTERN_NAMES = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_]*$");
    public static final Pattern PATTERN_FORMAT_PARAM = Pattern.compile("(?<=(\\{[0-9]}))");
    public static final Pattern PATTERN_FILEPATH_SLASH = Pattern.compile("[\\/\\\\]");
    public static final Pattern PATTERN_UNDERSCORE = Pattern.compile("_");
    public static final Pattern PATTERN_DOUBLE_QUOTE = Pattern.compile("\"");
    public static final Pattern PATTERN_SINGLE_QUOTE = Pattern.compile("'");

    private static Pattern PATTERN_PLUGIN_CHECK     = Pattern.compile("\\{plugin-");
    private static Pattern PATTERN_PLUGIN_VERSION   = Pattern.compile("\\{plugin-version}");
    private static Pattern PATTERN_PLUGIN_NAME      = Pattern.compile("\\{plugin-name}");
    private static Pattern PATTERN_PLUGIN_FULL_NAME = Pattern.compile("\\{plugin-full-name}");
    private static Pattern PATTERN_PLUGIN_AUTHOR    = Pattern.compile("\\{plugin-author}");
    private static Pattern PATTERN_PLUGIN_COMMAND   = Pattern.compile("\\{plugin-command}");

    private static final Pattern[] PATTERNS_FORMAT_PARAM = {
            Pattern.compile("\\{0}"),
            Pattern.compile("\\{1}"),
            Pattern.compile("\\{2}"),
            Pattern.compile("\\{3}"),
            Pattern.compile("\\{4}"),
            Pattern.compile("\\{5}"),
            Pattern.compile("\\{6}"),
            Pattern.compile("\\{7}"),
            Pattern.compile("\\{8}"),
            Pattern.compile("\\{9}")
    };

    public enum FormatTemplate {
        /**
         * No formatting.
         */
        RAW                   (_FORMAT_TEMPLATE_RAW),
        /**
         * A header in a list of items.
         */
        HEADER                (_FORMAT_TEMPLATE_HEADER),
        /**
         * A sub header in a list of items.
         */
        SUB_HEADER            (_FORMAT_TEMPLATE_SUB_HEADER),
        /**
         * A single item in a list.
         */
        LIST_ITEM             (_FORMAT_TEMPLATE_ITEM),
        /**
         * A single item in a list with a description.
         */
        LIST_ITEM_DESCRIPTION (_FORMAT_TEMPLATE_ITEM_DESCRIPTION),

        /**
         * A definition for an item that
         * generally does not change (i.e commands)
         */
        CONSTANT_DEFINITION   (_FORMAT_TEMPLATE_DEFINITION);

        private final String _template;

        FormatTemplate(String template) {
            _template = template;
        }

        @Override
        @Localized
        public String toString() {
            return Lang.get(_template);
        }
    }

    /**
     * Determine if a string is valid for use as a name.
     * The string must begin with a letter and use only
     * alphanumeric characters and underscores.
     *
     * <p>The string must also be at least 1 character in length
     * and be no more than 16 characters long.</p>
     *
     * @param name  The name to check
     */
    public static boolean isValidName(String name) {
        return isValidName(name, 16);
    }

    /**
     * Determine if a string is valid for use as a name.
     * The string must begin with a letter and use only
     * alphanumeric characters and underscores.
     *
     * <p>The string must also be at least 1 character in length.</p>
     *
     * @param name    The name to check
     * @param maxLen  The max length of the name
     */
    public static boolean isValidName(@Nullable String name, int maxLen) {
        PreCon.greaterThanZero(maxLen);

        return name != null && (maxLen == -1 || name.length() <= maxLen) &&
                name.length() > 0 && PATTERN_NAMES.matcher(name).matches();
    }

    /**
     * Pad the right side of a string with the specified characters
     *
     * @param s        The String to pad
     * @param length   The number of characters to pad
     * @param pad      The character to path with
     */
    public static String padRight(String s, int length, char pad) {
        PreCon.notNull(s);
        PreCon.positiveNumber(length);

        StringBuilder buffy = new StringBuilder(s.length() + length);
        buffy.append(s);
        for (int i = 0; i < length; ++i) {
            buffy.append(pad);
        }
        return buffy.toString();
    }

    /**
     * Pad the right side of a string with spaces.
     *
     * @param s       The String to pad
     * @param length  The number of spaces to pad with
     */
    public static String padRight(String s, int length) {
        return TextUtils.padRight(s, length, ' ');
    }

    /**
     * Pad the left side of a string with the specified characters
     *
     * @param s       The String to pad
     * @param length  The number of characters to pad
     * @param pad     The character to path with
     */
    public static String padLeft(String s, int length, char pad) {
        PreCon.notNull(s);
        PreCon.positiveNumber(length);

        StringBuilder buffy = new StringBuilder(s.length() + length);
        for (int i = 0; i < length; i++) {
            buffy.append(pad);
        }
        buffy.append(s);
        return buffy.toString();
    }

    /**
     * Pad left side of specified string with spaces
     *
     * @param s       The string to pad
     * @param length  The number of spaces to pad with
     */
    public static String padLeft(String s, int length) {
        return TextUtils.padLeft(s, length, ' ');
    }

    /**
     * Reduce the number of characters in a string by removing
     * characters from the end.
     *
     * <p>Returns input string if input string length is
     * less than or equal to 16 characters.</p>
     *
     * @param s       The string to truncate
     * @param length  The new length of the string.
     */
    public static String truncate(String s, int length) {
        PreCon.notNull(s);
        PreCon.positiveNumber(length);
        PreCon.lessThan(length, s.length());

        if (s.length() > length)
            return s.substring(0, length - 1);

        return s;
    }

    /**
     * Reduce the number of characters in a string to 16.
     * Returns input string if input string length is
     * less than or equal to 16 characters.
     *
     * @param s  The string to truncate
     */
    public static String truncate(String s) {
        return TextUtils.truncate(s, 15);
    }

    /**
     * Converts supplied string to camel casing.
     *
     * @param s  the string to convert
     */
    public static String camelCase(String s) {
        PreCon.notNull(s);

        if (s.length() < 2) {
            return s.toLowerCase();
        }

        String[] words = PATTERN_SPACE.split(s);
        StringBuilder resultSB = new StringBuilder(s.length() + 20);

        for (int i=0; i < words.length; i++) {
            String firstLetter = String.valueOf(words[i].charAt(0));

            resultSB.append(i == 0 ? firstLetter.toLowerCase() : firstLetter.toUpperCase());
            resultSB.append(words[i].substring(1).toLowerCase());
        }

        return resultSB.toString();
    }

    /**
     * Converts the casing of the supplied string
     * for use as a title.
     *
     * @param s  The string to modify
     */
    public static String titleCase(String s) {
        PreCon.notNull(s);

        if (s.length() < 2) {
            return s;
        }

        String[] words = PATTERN_SPACE.split(s);
        StringBuilder resultSB = new StringBuilder(s.length() + 10);

        for (int i=0; i < words.length; i++) {

            if (i != 0)
                resultSB.append(' ');

            String word = words[i];
            if (word.length() <= 3) {
                resultSB.append(word);
            } else {

                String firstLetter = word.substring(0, 1).toUpperCase();
                resultSB.append(firstLetter);
                resultSB.append(word.subSequence(1, word.length()));
            }
        }

        return resultSB.toString();
    }

    /**
     * Concatenates a collection into a single string using the
     * specified separator string.
     *
     * @param collection  The collection to concatenate
     * @param separator   The string to insert between elements
     */
    public static String concat(Collection<?> collection, String separator) {
        //noinspection ConstantConditions
        return concat(collection, separator, "");
    }

    /**
     * Concatenates a collection into a single string using the
     * specified separator string.
     *
     * @param collection  The collection to concatenate
     * @param separator   The string to insert between elements
     * @param emptyValue  The string to return if the collection is null or empty
     */
    @Nullable
    public static String concat(Collection<?> collection, String separator, String emptyValue) {
        if (collection == null || collection.isEmpty()) {
            return emptyValue;
        }

        if (separator == null)
            separator = "";

        StringBuilder buffy = new StringBuilder(collection.size() * 25);
        for (Object o : collection) {
            buffy.append(separator);
            buffy.append(o.toString());
        }
        return buffy.substring(separator.length());
    }

    /**
     * Concatenates an array in a single string using the
     * specified separator string.
     *
     * @param strArray   The array to concatenate
     * @param separator  The string to insert between elements
     */
    public static <T> String concat(T[] strArray, @Nullable String separator) {
        //noinspection ConstantConditions
        return concat(0, strArray.length, strArray, separator, "");
    }

    /**
     * Concatenates an array into a single string using the
     * specified separator string.
     *
     * @param strArray    The array to concatenate
     * @param separator   The string to insert between elements
     * @param emptyValue  The string to return if the array is null or empty
     */
    @Nullable
    public static <T> String concat(T[] strArray, @Nullable String separator, @Nullable String emptyValue) {
        return concat(0, strArray.length, strArray, separator, emptyValue);
    }

    /**
     * Concatenates an array into a single string using the
     * specified separator string.
     *
     * @param startIndex  The index to start concatenating at
     * @param strArray    The array to concatenate
     * @param separator   The separator to insert between elements
     */
    public static <T> String concat(int startIndex, T[] strArray, @Nullable String separator) {
        //noinspection ConstantConditions
        return concat(startIndex, strArray.length, strArray, separator, "");
    }

    /**
     * Concatenates an array into a single string using the
     * specified separator string.
     *
     * @param startIndex  The index to start concatenating at
     * @param strArray    The array to concatenate
     * @param separator   The separator to insert between elements
     * @param emptyValue  The value to return if the array is empty or null
     */
    @Nullable
    public static <T> String concat(int startIndex, T[] strArray, @Nullable String separator, @Nullable String emptyValue) {
        return concat(startIndex, strArray.length, strArray, separator, emptyValue);
    }


    /**
     * Concatenates an array into a single string using the
     * specified separator string.
     *
     * @param startIndex  The index to start concatenating at
     * @param endIndex    The index to stop concatenating at
     * @param strArray    The array to concatenate
     */
    public static <T> String concat(int startIndex, int endIndex, T[] strArray) {
        //noinspection ConstantConditions
        return concat(startIndex, endIndex, strArray, null, "");
    }


    /**
     * Concatenates an array into a single string using the
     * specified separator string.
     *
     * @param startIndex  The index to start concatenating at
     * @param endIndex    The index to stop concatenating at
     * @param strArray    The array to concatenate
     * @param separator   The separator to insert between elements
     */
    public static <T> String concat(int startIndex, int endIndex, T[] strArray, @Nullable String separator) {
        //noinspection ConstantConditions
        return concat(startIndex, endIndex, strArray, separator, "");
    }


    /**
     * Concatenates an array into a single string using the
     * specified separator string.
     *
     * @param startIndex  The index to start concatenating at
     * @param endIndex    The index to stop concatenating at
     * @param strArray    The array to concatenate
     */
    @Nullable
    public static <T> String concat(int startIndex, int endIndex, T[] strArray,
                                    @Nullable String separator, @Nullable String emptyValue) {
        PreCon.notNull(strArray);

        if (strArray.length == 0 || startIndex == endIndex)
            return emptyValue;

        if (separator == null)
            separator = "";

        StringBuilder buffy = new StringBuilder((endIndex - startIndex) * 25);
        boolean isEnum = strArray[0] instanceof Enum<?>;
        for (int i = startIndex; i < endIndex; i++) {
            T str = strArray[i];
            if (str == null)
                continue;

            buffy.append(separator);
            if (isEnum) {
                Enum<?> en = (Enum<?>)str;
                buffy.append(en.name());
            }
            else {
                buffy.append(str);
            }
        }
        return buffy.substring(separator.length());
    }

    /**
     * Splits a string into multiple string based on max
     * character length specified for a line.
     *
     * @param str                   The string to split/paginate
     * @param maxLineLen            The max length of a line
     * @param excludeColorsFromLen  True to exclude color characters from length calculations
     */
    public static List<String> paginateString(String str, int maxLineLen, boolean excludeColorsFromLen) {
        return paginateString(str, null, maxLineLen, excludeColorsFromLen);
    }

    /**
     * Splits a string into multiple string based on max
     * character length specified for a line.
     *
     * @param str                   The string to split/paginate
     * @param linePrefix            The prefix to append to each line
     * @param maxLineLen            The max length of a line
     * @param excludeColorsFromLen  True to exclude color characters from length calculations
     */
    public static List<String> paginateString(String str, @Nullable String linePrefix,
                                              int maxLineLen, boolean excludeColorsFromLen) {

        if (linePrefix != null)
            str = str.replace(linePrefix, "");
        else
            linePrefix = "";

        String[] words = PATTERN_SPACE.split(str);

        List<String> results = new ArrayList<String>(str.length() / maxLineLen);

        String format;
        StringBuilder line = new StringBuilder(maxLineLen);
        line.append(linePrefix);

        int prefixSize = linePrefix.length();

        boolean wordAddedToLine = false;

        for (int i=0; i < words.length; i++) {

            int lineLength = excludeColorsFromLen
                    ? TextColor.remove(line.toString()).length()
                    : line.length();

            int wordLength = excludeColorsFromLen
                    ? TextColor.remove(words[i]).length()
                    : words[i].length();

            if (lineLength + wordLength + 1 <= maxLineLen ||
                    prefixSize + wordLength >= maxLineLen) { // append to current line

                if (wordAddedToLine)
                    line.append(' ');

                line.append(words[i]);
                wordAddedToLine = true;
            }
            else { // create new line

                if (i != 0) {
                    i = i - 1;
                }

                String finishedLine = line.toString();
                format = ChatColor.getLastColors(finishedLine);
                results.add(finishedLine);

                line.setLength(0);
                line.append(linePrefix);
                line.append(format);

                wordAddedToLine = false;
            }
        }

        // make sure last line is added.
        // get left behind if number of words runs out
        // and there is still room for more.
        if (wordAddedToLine) {
            results.add(line.toString());
        }

        return results;
    }

    /**
     * Remove null items from an array and return
     * as a {@code String[]}
     *
     * @param array  Array to trim
     */
    public static <T> String[] trimArray(T[] array) {

        List<String> items = new ArrayList<String>(array.length);

        for (T item : array) {
            if (item == null)
                continue;

            items.add(String.valueOf(item));
        }

        return items.toArray(new String[items.size()]);
    }

    /**
     * Remove null items from an array and return
     * as a {@code String[]}
     *
     * @param array  Array to trim
     */
    public static <T> String[] trimArray(T[] array, T nullValue) {

        List<String> items = new ArrayList<String>(array.length);

        for (T item : array) {
            if (item == null && nullValue != null) {
                items.add(String.valueOf(nullValue));
            }

            if (item == null)
                continue;

            items.add(String.valueOf(item));
        }

        return items.toArray(new String[items.size()]);
    }


    /**
     * Replace null values in an array with the specified
     * value and return as a {@code String[]}
     *
     * @param array      Array to fill null values
     * @param nullValue  The value to insert in place of null values
     */
    public static <T> String[] fillArray(T[] array, T nullValue) {
        PreCon.notNull(array);
        PreCon.notNull(nullValue);

        List<String> items = new ArrayList<String>(array.length);

        for (T item : array) {
            if (item == null) {
                items.add(String.valueOf(nullValue));
            }

            if (item == null)
                continue;

            items.add(String.valueOf(item));
        }

        return items.toArray(new String[items.size()]);
    }

    /**
     * Format a location into a human readable string.
     *
     * @param loc       The location to format
     * @param addColor  True to add color
     */
    public static String formatLocation(Location loc, boolean addColor) {
        if (loc == null)
            return "null";

        DecimalFormat format = new DecimalFormat("###.##");
        //noinspection IfMayBeConditional
        if (addColor) {
            return ChatColor.LIGHT_PURPLE + "X:" + ChatColor.YELLOW + format.format(loc.getX()) +
                    ChatColor.WHITE + ", " + ChatColor.LIGHT_PURPLE + "Y:" + ChatColor.YELLOW + format.format(loc.getY()) +
                    ChatColor.WHITE + ", " + ChatColor.LIGHT_PURPLE + "Z:" + ChatColor.YELLOW + format.format(loc.getZ()) +
                    ChatColor.WHITE + ", " + ChatColor.LIGHT_PURPLE + "WORLD:" + ChatColor.YELLOW + loc.getWorld().getName();
        } else {
            return "X:" + format.format(loc.getX()) +
                    ", Y:" + format.format(loc.getY()) +
                    ", Z:" + format.format(loc.getZ()) +
                    ", WORLD:" + loc.getWorld().getName();
        }
    }

    /**
     * Determine if specified character is an english vowel.
     *
     * @param ch  The character to check
     */
    public static boolean isVowel(char ch) {
        return ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u' ||
                ch == 'A' || ch == 'E' || ch == 'I' || ch == 'O' || ch == 'U';
    }

    public static String getIndefiniteArticle(String following, boolean lowercase) {
        PreCon.notNull(following);
        PreCon.notNull(following);

        following = following.trim();

        if (following.isEmpty())
            return following;

        return getIndefiniteArticle(following.charAt(0), lowercase);
    }

    public static String getIndefiniteArticle(char following, boolean lowercase) {
        if (lowercase) {
            return isVowel(following) ? "an" : "a";
        }

        return isVowel(following) ? "An" : "A";
    }

    /**
     * Format text string by replacing placeholders with the information
     * specified.
     *
     * @param msg      The message to format plugin info into.
     * @param formats  Formatting information.
     */
    public static String formatCustom(String msg, FormatEntry... formats) {
        PreCon.notNull(msg);
        PreCon.notNull(formats);

        if (formats.length == 0)
            return msg;

        for (FormatEntry entry : formats) {
            Matcher matcher = entry.getPattern().matcher(msg);
            msg = matcher.replaceAll(entry.getReplaceValue());
        }

        return msg;
    }

    /**
     * Format text string by replacing placeholders with the information
     * about the specified plugin.
     *
     * <p>Placeholders: {plugin-version}, {plugin-name}, {plugin-full-name}, {plugin-author}, {plugin-command}</p>
     *
     * @param plugin  The plugin
     * @param msg     The message to format plugin info into.
     */
    public static String formatPluginInfo(Plugin plugin, String msg) {

        Matcher checkMatcher = PATTERN_PLUGIN_CHECK.matcher(msg);
        if (!checkMatcher.find())
            return msg;

        if (plugin != null) {

            Matcher matcher = PATTERN_PLUGIN_VERSION.matcher(msg);
            if (matcher.find())
                msg = matcher.replaceAll(plugin.getDescription().getVersion());

            matcher = PATTERN_PLUGIN_NAME.matcher(msg);
            if (matcher.find())
                msg = matcher.replaceAll(plugin.getName());

            matcher = PATTERN_PLUGIN_AUTHOR.matcher(msg);
            if (matcher.find())
                msg = matcher.replaceAll(TextUtils.concat(plugin.getDescription().getAuthors(), ", "));

            PluginDescriptionFile description = plugin.getDescription();
            if (description != null) {

                matcher = PATTERN_PLUGIN_FULL_NAME.matcher(msg);
                if (matcher.find())
                    msg = matcher.replaceAll(plugin.getDescription().getFullName());

                matcher = PATTERN_PLUGIN_COMMAND.matcher(msg);
                if (matcher.find()) {

                    Map<String, Map<String, Object>> commands = description.getCommands();
                    if (commands != null) {

                        Set<String> commandKeys = commands.keySet();

                        // pull any command
                        for (String cmd : commandKeys) {

                            if (!msg.isEmpty()) {
                                msg = matcher.replaceAll(cmd);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return msg;
    }


    /**
     * Format text string by replacing placeholders (i.e {0})
     * with the string representation of the objects provided as parameters.
     *
     * <p>The index order of the objects as provided is mapped to the number
     * inside the placeholder. </p>
     *
     * <p>Placeholders can only be indexed to 0-9</p>
     *
     * <p>Color can be specified using {@link TextColor} enum names in curly brackets.
     * (i.e. {GREEN})</p>
     *
     * @param template  An object whose toString method yields the message template.
     * @param params    The object to format into the message.
     */
    public static String format(Object template, Object... params) {
        return format(template.toString(), params);
    }

    /**
     * Format text string by replacing placeholders (i.e {0})
     * with the string representation of the objects provided as parameters.
     *
     * <p>The index order of the objects as provided is mapped to the number
     * inside the placeholder. </p>
     *
     * <p>Placeholders can only be indexed from 0-9</p>
     *
     * <p>Color can be specified using {@link TextColor} names in curly brackets.
     * (i.e. {GREEN})</p>
     *
     * @param msg     The message to format.
     * @param params  The object to format into the message.
     */
    public static String format(String msg, Object... params) {
        PreCon.notNull(msg);
        PreCon.notNull(params);

        if (msg.indexOf('{') == -1)
            return msg; // finished

        // replace color tags
        for (TextColor color : TextColor.values()) {
            Matcher matcher = color.getPattern().matcher(msg);
            if (matcher.find()) {
                msg = matcher.replaceAll(color.getColorCode());
            }
        }

        if (params.length == 0)
            return msg; // finished

        boolean hasSplitForColoredParams = false;

        // Insert format parameters
        for (int i = 0; i < params.length && i < 10; ++i) {

            Pattern pattern = PATTERNS_FORMAT_PARAM[i];
            Matcher matcher = pattern.matcher(msg);
            if (matcher.find()) {

                String replaceWith = params[i] != null ? params[i].toString() : "";

                boolean splitForColors = !hasSplitForColoredParams &&
                        !replaceWith.isEmpty() &&
                        replaceWith.indexOf(TextColor.FORMAT_CHAR) != -1;

                // add ending colors after format if parameters
                // value contains colors
                if (splitForColors) {

                    String[] components = PATTERN_FORMAT_PARAM.split(msg);

                    String previousColor = "";

                    for (int j = 0; j < components.length; j++) {

                        components[j] = previousColor + components[j];

                        previousColor = TextColor.getEndColor(components[j]);
                    }

                    matcher = pattern.matcher(TextUtils.concat(components, ""));

                    hasSplitForColoredParams = true;
                }

                msg = matcher.replaceAll(replaceWith);
            }

        }

        return msg;
    }

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

        private static final char FORMAT_CHAR = '\u00A7';
        private static final Map<Character, TextColor> _characterMap = new HashMap<>(20);

        static {
            for (TextColor color : values()) {
                _characterMap.put(color.getColorChar(), color);
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

        public Pattern getPattern() {
            return _pattern;
        }

        public char getColorChar() {
            return _colorChar;
        }

        public String getColorCode() {
            return _colorCode;
        }

        public boolean isColor() {
            return _isColor;
        }

        @Override
        public String toString() {
            return _colorCode;
        }

        public static String remove(String input) {

            StringBuilder sb = new StringBuilder(input.length());
            char[] chars = input.toCharArray();

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

        public static String getEndColor(String input) {

            StringBuilder sb = new StringBuilder(15);
            char[] chars = input.toCharArray();

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

    }

    /**
     * Used to hold custom text formatting information
     */
    public static class FormatPattern {
        private final String _key;
        private final Pattern _pattern;

        public FormatPattern (String regex) {
            _key = regex;
            _pattern = Pattern.compile(regex);
        }

        public String getReplaceKey() {
            return _key;
        }

        public Pattern getPattern() {
            return _pattern;
        }

        public FormatEntry getEntry(String replaceValue) {
            return new FormatEntry(replaceValue);
        }

        public class FormatEntry {

            private final String _replaceValue;

            FormatEntry(String replaceValue) {
                _replaceValue = replaceValue;
            }

            public Pattern getPattern() {
                return _pattern;
            }

            public String getReplaceValue() {
                return _replaceValue;
            }
        }
    }
}

