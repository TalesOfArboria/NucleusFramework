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

import com.jcwhatever.nucleus.utils.ArrayUtils;
import com.jcwhatever.nucleus.utils.text.TextFormat;
import com.jcwhatever.nucleus.utils.text.components.IChatMessage;

import java.util.Map;

/*
 * 
 */
public class ParseContext {

    final TextFormatterSettings settings;
    final CharSequence template;
    final FormatResultBuffer buffer;
    final StringBuilder tagBuffer;
    final Map<String, ITagFormatter> formatters;
    final Object[] params;
    final TextFormatterResult result = new TextFormatterResult();
    final TextParser parser;

    ParseContext(ParseContext context, CharSequence template) {
        this.settings = context.settings;
        this.formatters = context.formatters;
        this.tagBuffer = context.tagBuffer;
        this.buffer = context.buffer;
        this.template = template;
        this.parser = new TextParser(template);
        this.params = ArrayUtils.EMPTY_OBJECT_ARRAY;
    }

    ParseContext(TextFormatterSettings settings,
                 FormatResultBuffer buffer,
                 CharSequence template, Object[] params) {

        this.settings = settings;
        this.buffer = buffer;
        this.formatters = settings.getFormatMap();
        this.template = template;
        this.params = params;
        this.tagBuffer = new StringBuilder(10);
        this.parser = new TextParser(template);
    }

    /**
     * Determine if the template should be formatted.
     */
    public boolean shouldFormat() {

        if (template instanceof IChatMessage)
            return false;

        if (template.length() == 0)
            return false;

        String str = template.toString();

        if (str.indexOf(TextFormat.CHAR) != -1)
            return true;

        if (settings.getLineReturnPolicy() != TextFormatterSettings.FormatPolicy.IGNORE) {
            if (str.indexOf('\n') != -1)
                return true;
        }

        if (settings.getEscaped().length == 0 && str.indexOf('{') == -1 && str.indexOf('\\') == -1)
            return false;

        return true;
    }
}
