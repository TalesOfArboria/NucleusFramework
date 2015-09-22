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

package com.jcwhatever.nucleus.utils.text.format;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextColor;
import com.jcwhatever.nucleus.utils.text.TextFormat;
import com.jcwhatever.nucleus.utils.text.components.SimpleChatComponent;
import com.jcwhatever.nucleus.utils.text.components.SimpleChatModifier;
import com.jcwhatever.nucleus.utils.text.components.IChatComponent;
import com.jcwhatever.nucleus.utils.text.components.IChatModifier;
import com.jcwhatever.nucleus.utils.text.dynamic.IDynamicText;
import com.jcwhatever.nucleus.utils.text.format.TextFormatterSettings.FormatPolicy;
import com.jcwhatever.nucleus.utils.text.format.args.IFormatterArg;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Replaces tags and unicode escapes in text.
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

    private static Map<String, ITagFormatter> _colors = new HashMap<>(22);

    static {

        Iterator<TextFormat> iterator = TextFormat.formatIterator();
        while (iterator.hasNext()) {

            final TextFormat format = iterator.next();

            _colors.put(format.getTagName(), new ITagFormatter() {

                @Override
                public String getTag() {
                    return format.getTagName();
                }

                @Override
                public void append(IFormatterAppendable output, String tag) {
                    setModifier(format, output.getModifier());
                }
            });
        }
    }

    private static ITagFormatter ERASER = new ITagFormatter() {
        @Override
        public String getTag() {
            return "#ERASER#";
        }

        @Override
        public void append(IFormatterAppendable output, String rawTag) {
            // do nothing
        }
    };

    private static void setModifier(TextFormat format, IChatModifier modifier) {
        switch (format.getTagName()) {
            case "BOLD":
                modifier.setBold(true);
                return;
            case "ITALIC":
                modifier.setItalic(true);
                return;
            case "MAGIC":
                modifier.setMagic(true);
                return;
            case "STRIKETHROUGH":
                modifier.setStrikeThrough(true);
                return;
            case "UNDERLINE":
                modifier.setUnderline(true);
                return;
            case "RESET":
                modifier.reset();
                return;
        }

        if (format instanceof TextColor) {
            modifier.setColor((TextColor)format);
        }
    }

    private final FormatContext _context = new FormatContext();
    private final StringBuilder _tagBuffer = new StringBuilder(25);
    private final Object _sync = new Object();
    private final Thread _homeThread;
    private final TextFormatterSettings _settings;

    /**
     * Constructor.
     *
     * @param settings  The formatter settings
     */
    public TextFormatter(TextFormatterSettings settings) {
        _settings = settings;
        _homeThread = Thread.currentThread();
    }

    /**
     * Format text.
     *
     * @param template  The template text.
     * @param params    The parameters to add.
     *
     * @return  The formatted string.
     */
    public TextFormatterResult format(String template, Object... params) {
        PreCon.notNull(template);

        return format(_settings, template, params);
    }

    /**
     * Format text.
     *
     * @param settings  Custom formatter settings to use.
     * @param template  The template text.
     * @param params    The parameters to add.
     *
     * @return  The formatted string.
     */
    public TextFormatterResult format(TextFormatterSettings settings, String template, Object... params) {
        PreCon.notNull(template);

        if (isHomeThread()) {
            _context.hardReset();
            return format(settings, _context, _tagBuffer, false, settings.getFormatMap(), template, params);
        }
        else {
            return format(settings, null, null, false, settings.getFormatMap(), template, params);
        }
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
    private TextFormatterResult format(TextFormatterSettings settings,
                                       @Nullable FormatContext context,
                                       @Nullable StringBuilder tagBuffer,
                                       boolean isFormattingArgs,
                                       Map<String, ITagFormatter> formatters, String template, Object... params) {

        if (!shouldFormat(settings, template)) {

            if (context != null) {
                context.append(template);
            }

            TextFormatterResult result = new TextFormatterResult(new SimpleChatComponent(template));
            if (!isFormattingArgs) {
                result.finishResult(settings);
            }

            return result;
        }

        if (context == null)
            context = new FormatContext(template.length());

        if (tagBuffer == null)
            tagBuffer = new StringBuilder(10);

        TextFormatterResult result = new TextFormatterResult();

        for (int i=0; i < template.length(); i++) {
            char ch = template.charAt(i);

            // handle format codes
            if (ch == TextFormat.CHAR && i < template.length() - 1
                    && TextFormat.isFormatChar(template.charAt(i + 1))) {

                TextFormat format = TextFormat.fromFormatChar(template.charAt(i + 1));
                assert format != null;

                if (format instanceof TextColor) {
                    result.setParsedColor(true);
                    if (context.getModifier().getColor() != null) {
                        context.reset();
                    }
                }

                setModifier(format, context.getModifier());

                i += 1;
                continue;
            }

            // check for tag opening
            if (ch == '{') {

                // parse tag
                String tag = parseTag(tagBuffer, template, i);

                // update index position
                i += tagBuffer.length();

                // template ended before tag was closed
                if (tag == null) {
                    context.append('{');
                    context.append(tagBuffer);
                }
                // tag parsed
                else {
                    i++; // add 1 for closing brace
                    appendReplacement(settings, result, context, tagBuffer, tag, params, formatters);
                }

            }
            else if (ch == '\n' || ch == '\r') {
                if (settings.getLineReturnPolicy() != FormatPolicy.REMOVE) {
                    context.newLine();
                }
            }
            else if (ch == '\\' && i < template.length() - 1) {
                i = processBackslash(settings, context, tagBuffer, template, i);
            }
            else {

                if (settings.isEscaped(ch))
                    context.append('\\');

                // append next character
                context.append(ch);
            }
        }

        if (context.isModified()) {
            context.reset();
        }

        result.appendAll(context.results);
        if (!isFormattingArgs) {
            result.finishResult(settings);
        }
        return result;
    }

    /**
     * Parse a unicode character from the string
     */
    private char parseUnicode(StringBuilder tagBuffer, String template, int currentIndex) {
        tagBuffer.setLength(0);

        for (int i=currentIndex + 1, readCount=0; i < template.length(); i++, readCount++) {

            if (readCount == 4) {
                break;
            }
            else {
                char ch = template.charAt(i);
                if ("01234567890abcdefABCDEF".indexOf(ch) == -1)
                    return 0;
                tagBuffer.append(ch);
            }
        }

        if (tagBuffer.length() == 4) {

            try {
                return (char) Integer.parseInt(tagBuffer.toString(), 16);
            } catch (NumberFormatException ignore) {
                return 0;
            }
        }

        return 0;
    }

    /*
     * Parse a single tag from the template
     */
    private String parseTag(StringBuilder tagBuffer, String template, int currentIndex) {

        tagBuffer.setLength(0);

        for (int i=currentIndex + 1; i < template.length(); i++) {

            char ch = template.charAt(i);

            if (ch == '}') {
                return tagBuffer.toString();
            }
            else {
                tagBuffer.append(ch);
            }
        }

        return null;
    }

    /*
     * Append replacement text for a tag
     */
    private void appendReplacement(TextFormatterSettings settings,
                                   TextFormatterResult result,
                                   FormatContext textBuffer,
                                   StringBuilder tagBuffer,
                                   String tag, Object[] params, Map<String, ITagFormatter> formatters) {

        boolean isNumber = !tag.isEmpty();

        tagBuffer.setLength(0);

        // parse out tag from comment section
        for (int i=0; i < tag.length(); i++) {

            char ch = tag.charAt(i);

            // done at comment character
            if (ch == ':') {
                break;
            }
            // append next tag character
            else {
                tagBuffer.append(ch);

                // check if the character is a number
                if (isNumber && !Character.isDigit(ch)) {
                    isNumber = false;
                }
            }
        }

        String parsedTag = tagBuffer.toString();

        if (isNumber) {
            int index = Integer.parseInt(parsedTag);

            // make sure number is in the range of the provided parameters.
            if (params.length <= index) {
                reappendTag(textBuffer, tag);
            }
            // replace number with parameter argument.
            else {

                Object param = params[index];

                if (param instanceof IDynamicText) {
                    param = ((IDynamicText) param).nextText();
                }
                else if (param instanceof IFormatterArg) {
                    IChatModifier modifier = new SimpleChatModifier(textBuffer.getModifier());
                    if (textBuffer.isModified()) {
                        textBuffer.reset();
                    }
                    ((IFormatterArg) param).getComponents(textBuffer.results);
                    textBuffer.reset(modifier);
                    return;
                }
                else if (param instanceof IChatComponent) {
                    IChatModifier modifier = new SimpleChatModifier(textBuffer.getModifier());
                    if (textBuffer.isModified()) {
                        textBuffer.reset();
                    }
                    textBuffer.results.add((IChatComponent)param);
                    textBuffer.reset(modifier);
                    return;
                }

                String toAppend = String.valueOf(param);

                // append parameter argument
                if (settings.isArgsFormatted()) {
                    IChatModifier modifier = new SimpleChatModifier(textBuffer.getModifier());
                    TextFormatterResult argResult = format(settings, textBuffer, null, true, formatters, toAppend);
                    //result.appendAll(argResult);

                    if ((argResult.isParsed() && argResult.parsedColor())
                            || (!argResult.isParsed() && toAppend.indexOf(TextFormat.CHAR) != -1)) {

                        // make sure colors from inserted text do not continue
                        // into template text
                        textBuffer.reset(modifier);
                    }
                }
                else {
                    boolean hasColorCode = toAppend.indexOf(TextFormat.CHAR) != -1;
                    IChatModifier modifier = hasColorCode
                            ? new SimpleChatModifier(textBuffer.getModifier())
                            : null;

                    textBuffer.append(toAppend);

                    if (hasColorCode) {
                        // make sure colors from inserted text do not continue
                        // into template text
                        textBuffer.reset(modifier);
                    }
                }
            }
        }
        else {

            // check for custom formatter
            ITagFormatter formatter = settings.getTagPolicy() == FormatPolicy.IGNORE
                    ? null
                    : getFormatter(parsedTag, formatters);

            if (formatter == null && settings.getColorPolicy() != FormatPolicy.IGNORE) {

                // check for color formatter
                formatter = getFormatter(parsedTag, _colors);

                if (formatter != null) {

                    // remove color tag if color policy is remove
                    if (settings.getColorPolicy() == FormatPolicy.REMOVE) {
                        formatter = ERASER;
                    }
                    else {
                        result.setParsedColor(true);
                    }
                }
            }
            // remove tag if tag policy is remove
            else if (formatter != null && settings.getTagPolicy() == FormatPolicy.REMOVE) {
                formatter = ERASER;
            }

            if (formatter != null) {

                if (shouldResetAfterTag(formatter.getTag()))
                    textBuffer.reset();

                // formatter appends replacement text to format buffer
                formatter.append(textBuffer, tag);
            }
            else {
                // no formatter, append tag to result buffer
                reappendTag(textBuffer, tag);
            }
        }
    }

    private boolean shouldResetAfterTag(String tag) {
        TextFormat format = TextColor.fromName(tag);
        return format instanceof TextColor;
    }

    /**
     * Process an escape character. Returns the new index location.
     */
    private int processBackslash(TextFormatterSettings settings,
                                 FormatContext textBuffer,
                                 StringBuilder tagBuffer,
                                 String template, int index) {

        // make sure the backslash isn't escaped
        int s = index;
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
            return index;

        // look at next character
        char next = template.charAt(index + 1);

        // handle new line character
        if ((next == 'n' || next == 'r') && settings.getLineReturnPolicy() != FormatPolicy.IGNORE) {

            if (settings.getLineReturnPolicy() != FormatPolicy.REMOVE) {
                textBuffer.newLine();
            }
            return index + 1;
        }
        // handle unicode
        else if (next == 'u' && settings.getUnicodePolicy() != FormatPolicy.IGNORE) {

            index++;
            char unicode = parseUnicode(tagBuffer, template, index);
            if (unicode == 0) {
                // append non unicode text
                textBuffer.append("\\u");
                textBuffer.incrementCharCount(2);
            }
            else {
                if (settings.getUnicodePolicy() != FormatPolicy.REMOVE) {
                    textBuffer.append(unicode);
                    textBuffer.incrementCharCount(1);
                }

                index+= Math.min(4, template.length() - index);
            }

        }
        // unused backslash
        else {
            textBuffer.append('\\');
        }

        return index;
    }

    /**
     * Get a color formatter for the parsed tag.
     */
    @Nullable
    private ITagFormatter getFormatter(String parsedTag, Map<String, ITagFormatter> formatters) {
        synchronized (_sync) {
            return formatters.get(parsedTag);
        }
    }

    /*
     * Append raw tag to string builder
     */
    private void reappendTag(FormatContext context, String tag) {
        context.append('{');
        context.append(tag);
        context.append('}');
        context.incrementCharCount(2 + tag.length());
    }

    /*
     * Determine if the current thread is the thread
     * the formatter was instantiated on.
     */
    private boolean isHomeThread() {
        return Thread.currentThread().equals(_homeThread);
    }

    /**
     * Determine if a text template should be formatted.
     */
    private boolean shouldFormat(TextFormatterSettings settings, String template) {
        if (template.isEmpty())
            return false;

        if (template.indexOf(TextFormat.CHAR) != -1)
            return true;

        if (settings.getLineReturnPolicy() != FormatPolicy.IGNORE) {
            if (template.indexOf('\n') != -1)
                return true;
        }

        if (settings.getEscaped().length == 0 && template.indexOf('{') == -1 && template.indexOf('\\') == -1)
            return false;

        return true;
    }
}

