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
import com.jcwhatever.nucleus.utils.performance.pool.IPoolElementFactory;
import com.jcwhatever.nucleus.utils.performance.pool.IPoolRecycleHandler;
import com.jcwhatever.nucleus.utils.performance.pool.SimplePool;
import com.jcwhatever.nucleus.utils.text.TextColor;
import com.jcwhatever.nucleus.utils.text.TextFormat;
import com.jcwhatever.nucleus.utils.text.components.IChatComponent;
import com.jcwhatever.nucleus.utils.text.components.IChatMessage;
import com.jcwhatever.nucleus.utils.text.components.IChatModifier;
import com.jcwhatever.nucleus.utils.text.components.SimpleChatComponent;
import com.jcwhatever.nucleus.utils.text.components.SimpleChatModifier;
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
                    setModifier(format, output);
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

    private static void setModifier(TextFormat format,IFormatterAppendable appendable) {

        IChatModifier modifier = appendable.getModifier();

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
                if (appendable instanceof FormatResultBuffer) {
                    ((FormatResultBuffer) appendable).reset();
                }
                appendable.getModifier().reset();
                return;
        }

        if (format instanceof TextColor) {
            modifier.setColor((TextColor)format);
        }
    }

    private final Thread _homeThread;
    private final TextFormatterSettings _settings;
    private final SimplePool<FormatResultBuffer> _bufferPool = new SimplePool<FormatResultBuffer>(10,
            new IPoolElementFactory<FormatResultBuffer>() {
                @Override
                public FormatResultBuffer create() {
                    return new FormatResultBuffer();
                }
            },
            new IPoolRecycleHandler<FormatResultBuffer>() {
                @Override
                public void onRecycle(FormatResultBuffer buffer) {
                    buffer.hardReset();
                }
            });

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
    public ITextFormatterResult format(CharSequence template, Object... params) {
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
    public ITextFormatterResult format(TextFormatterSettings settings, CharSequence template, Object... params) {
        PreCon.notNull(template);

        if (isHomeThread()) {
            FormatResultBuffer buffer = _bufferPool.retrieve();
            ITextFormatterResult result =  format(new ParseContext(settings, buffer, template, params), false);
            _bufferPool.recycle(buffer);
            return result;
        }
        else {
            return format(new ParseContext(settings, new FormatResultBuffer(), template, params), false);
        }
    }

    /**
     * Format text using a custom set of formatters.
     *
     * @param context  The parsing context.
     * @param isFormattingArgs  True if formatting arguments, otherwise false.
     *
     * @return  The format result.
     */
    private ITextFormatterResult format(ParseContext context, boolean isFormattingArgs) {

        if (context.template instanceof ITextFormatterResult
                && context.params.length == 0) {
            return (ITextFormatterResult) context.template;
        }

        if (!context.shouldFormat()) {

            if (isFormattingArgs) {
                context.buffer.append(context.template);
            }
            else {
                context.result.append(new SimpleChatComponent(context.template));
                context.result.finishResult(context.settings);
            }

            return context.result;
        }

        FormatResultBuffer buffer = context.buffer;
        StringBuilder tagBuffer = context.tagBuffer;
        TextFormatterSettings settings = context.settings;
        TextFormatterResult result = context.result;
        TextParser parser = context.parser;

        while (!parser.isFinished()) {
            char ch = parser.next();
            if (ch == 0)
                break;

            // handle format codes
            if (ch == TextFormat.CHAR
                    && TextFormat.isFormatChar(parser.peek(1))) {

                appendFormatCode(context);

                parser.skip(1);
                continue;
            }

            // check for tag opening
            if (ch == '{') {

                // parse tag
                String tag = parseTag(context);

                // update index position
                parser.skip(tagBuffer.length());

                // template ended before tag was closed
                if (tag == null) {
                    buffer.append('{');
                    buffer.append(tagBuffer);
                }
                // tag parsed
                else {
                    parser.skip(1); // add 1 for closing brace
                    appendReplacement(context, tag);
                }

            }
            else if (ch == '\n' || ch == '\r') {
                if (settings.getLineReturnPolicy() != FormatPolicy.REMOVE) {
                    buffer.newLine();
                }
            }
            else if (ch == '\\' && parser.peek(1) != 0) {
                processBackslash(context);
            }
            else {

                if (settings.isEscaped(ch))
                    buffer.append('\\');

                // append next character
                buffer.append(ch);
            }
        }

        if (buffer.isModified()) {
            buffer.reset();
        }

        result.appendAll(buffer.results);
        if (!isFormattingArgs) {
            result.finishResult(settings);
        }
        return result;
    }

    private void appendFormatCode(ParseContext context) {

        FormatResultBuffer buffer = context.buffer;

        TextFormat format = TextFormat.fromFormatChar(context.parser.peek(1));
        assert format != null;

        if (format instanceof TextColor) {
            context.result.setParsedColor(true);
            if (buffer.getModifier().getColor() != null) {
                buffer.reset();
            }
        }

        setModifier(format, buffer);
    }

    /**
     * Parse a unicode character from the string
     */
    private char parseUnicode(ParseContext context) {

        StringBuilder tagBuffer = context.tagBuffer;
        TextParser parser = context.parser;

        tagBuffer.setLength(0);

        int readCount = 0;
        while (parser.current() != 0) {
            if (readCount == 4) {
                break;
            }
            else {
                char ch = parser.peek(readCount + 1);
                if ("01234567890abcdefABCDEF".indexOf(ch) == -1)
                    return 0;
                tagBuffer.append(ch);
            }
            readCount++;
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
    private String parseTag(ParseContext context) {

        StringBuilder tagBuffer = context.tagBuffer;
        TextParser parser = context.parser;

        tagBuffer.setLength(0);

        int i = 1;
        while (parser.current() != 0) {

            char ch = context.parser.peek(i);
            if (ch == 0)
                return null;

            if (ch == '}') {
                return tagBuffer.toString();
            }
            else {
                tagBuffer.append(ch);
            }
            i++;
        }

        return null;
    }

    /*
     * Append replacement text for a tag
     */
    private void appendReplacement(ParseContext context, String tag) {

        TextFormatterSettings settings = context.settings;
        TextFormatterResult result = context.result;
        FormatResultBuffer buffer = context.buffer;
        StringBuilder tagBuffer = context.tagBuffer;
        Object[] params = context.params;
        Map<String, ITagFormatter> formatters = context.formatters;

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
                reappendTag(buffer, tag);
            }
            // replace number with parameter argument.
            else {

                Object param = params[index];

                if (param instanceof IDynamicText) {
                    param = ((IDynamicText) param).nextText();
                }
                else if (param instanceof IFormatterArg) {
                    IChatModifier modifier = new SimpleChatModifier(buffer.getModifier());
                    if (buffer.isModified()) {
                        buffer.reset();
                    }
                    ((IFormatterArg) param).getComponents(buffer.results);
                    buffer.reset(modifier);
                    return;
                }
                else if (param instanceof IChatComponent) {
                    IChatModifier modifier = new SimpleChatModifier(buffer.getModifier());
                    if (buffer.isModified()) {
                        buffer.reset();
                    }
                    buffer.results.add((IChatComponent) param);
                    buffer.reset(modifier);
                    return;
                }
                else if (param instanceof IChatMessage) {
                    IChatModifier modifier = new SimpleChatModifier(buffer.getModifier());
                    if (buffer.isModified()) {
                        buffer.reset();
                    }
                    ((IChatMessage) param).getComponents(buffer.results);
                    buffer.reset(modifier);
                    return;
                }

                String toAppend = String.valueOf(param);

                // append parameter argument
                if (settings.isArgsFormatted()) {
                    IChatModifier modifier = new SimpleChatModifier(buffer.getModifier());
                    ITextFormatterResult argResult = format(new ParseContext(context, toAppend), true);
                    //result.appendAll(argResult);

                    if ((argResult.isParsed() && argResult.isColorParsed())
                            || (!argResult.isParsed() && toAppend.indexOf(TextFormat.CHAR) != -1)) {

                        // make sure colors from inserted text do not continue
                        // into template text
                        buffer.reset(modifier);
                    }
                }
                else {
                    boolean hasColorCode = toAppend.indexOf(TextFormat.CHAR) != -1;
                    IChatModifier modifier = hasColorCode
                            ? new SimpleChatModifier(buffer.getModifier())
                            : null;

                    buffer.append(toAppend);

                    if (hasColorCode) {
                        // make sure colors from inserted text do not continue
                        // into template text
                        buffer.reset(modifier);
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
                    buffer.reset();

                // formatter appends replacement text to format buffer
                formatter.append(buffer, tag);
            }
            else {
                // no formatter, append tag to result buffer
                reappendTag(buffer, tag);
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
    private void processBackslash(ParseContext context) {

        TextFormatterSettings settings = context.settings;
        FormatResultBuffer buffer = context.buffer;
        TextParser parser = context.parser;

        // make sure the backslash isn't escaped
        int s = 0;
        int bsCount = 0;
        while (parser.current() != 0) {
            if (parser.peek(s - 1) == '\\') {
                bsCount++;
            }
            else {
                break;
            }
            s--;
        }
        if (bsCount % 2 != 0)
            return;

        // look at next character
        char next = parser.peek(1);

        // handle new line character
        if ((next == 'n' || next == 'r') && settings.getLineReturnPolicy() != FormatPolicy.IGNORE) {

            if (settings.getLineReturnPolicy() != FormatPolicy.REMOVE) {
                buffer.newLine();
            }

            parser.skip(1);
        }
        // handle unicode
        else if (next == 'u' && settings.getUnicodePolicy() != FormatPolicy.IGNORE) {

            parser.skip(1);
            char unicode = parseUnicode(context);
            if (unicode == 0) {
                // append non unicode text
                buffer.append("\\u");
                buffer.incrementCharCount(2);
            }
            else {
                if (settings.getUnicodePolicy() != FormatPolicy.REMOVE) {
                    buffer.append(unicode);
                    buffer.incrementCharCount(1);
                }

                parser.skip(4);
            }

        }
        // unused backslash
        else {
            buffer.append('\\');
        }
    }

    /**
     * Get a color formatter for the parsed tag.
     */
    @Nullable
    private ITagFormatter getFormatter(String parsedTag, Map<String, ITagFormatter> formatters) {
        return formatters.get(parsedTag);
    }

    /*
     * Append raw tag to string builder
     */
    private void reappendTag(FormatResultBuffer context, String tag) {
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
}

