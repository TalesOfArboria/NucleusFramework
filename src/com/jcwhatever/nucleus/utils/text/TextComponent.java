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
import com.jcwhatever.nucleus.utils.text.TextFormat.TextFormats;

import java.util.List;
import javax.annotation.Nullable;

/**
 * A segment of text that begins with a color/format.
 */
public class TextComponent {

    private final TextColor _textColor;
    private boolean _isBold;
    private boolean _isItalic;
    private boolean _isMagic;
    private boolean _isReset;
    private boolean _isStrikethrough;
    private boolean _isUnderline;

    private final String _text;
    private final Object _sync = new Object();
    private final int _hash;
    private String _formatted;

    /**
     * Constructor.
     *
     * @param text     The text of the component.
     * @param color    Optional text color.
     * @param formats  Optional text formats.
     */
    TextComponent(String text, @Nullable TextColor color, TextFormat... formats) {
        PreCon.notNull(text);

        _textColor = color;
        _text = text;

        int hash = text.hashCode();

        if (color != null) {
            hash = color.hashCode();
        }

        for (TextFormat format : formats) {

            if (format == null)
                break;

            if (format == TextFormat.BOLD)
                _isBold = true;
            else if (format == TextFormat.ITALIC)
                _isItalic = true;
            else if (format == TextFormat.MAGIC)
                _isMagic = true;
            else if (format == TextFormat.RESET)
                _isReset = true;
            else if (format == TextFormat.STRIKETHROUGH)
                _isStrikethrough = true;
            else if (format == TextFormat.UNDERLINE)
                _isUnderline = true;
            else
                break;

            hash ^= format.hashCode();
        }

        _hash = hash;
    }

    /**
     * Constructor.
     *
     * @param text     The text of the component.
     * @param formats  Optional text formats.
     */
    TextComponent(String text, @Nullable TextFormats formats) {
        PreCon.notNull(text);

        _text = text;

        int hash = text.hashCode();

        TextColor color = null;

        if (formats != null) {

            List<TextFormat> formatList = formats.getFormats();

            for (TextFormat format : formatList) {

                if (format instanceof TextColor) {
                    color = (TextColor) format;
                }
                if (format == TextFormat.BOLD)
                    _isBold = true;
                else if (format == TextFormat.ITALIC)
                    _isItalic = true;
                else if (format == TextFormat.MAGIC)
                    _isMagic = true;
                else if (format == TextFormat.RESET)
                    _isReset = true;
                else if (format == TextFormat.STRIKETHROUGH)
                    _isStrikethrough = true;
                else if (format == TextFormat.UNDERLINE)
                    _isUnderline = true;
                else
                    continue;

                hash ^= format.hashCode();
            }
        }

        _textColor = color;
        _hash = hash;
    }

    /**
     * Get the text color.
     */
    @Nullable
    public TextColor getColor() {
        return _textColor;
    }

    /**
     * Determine if the text is bold.
     */
    public boolean isBold() {
        return _isBold;
    }

    /**
     * Determine if the text is italic.
     */
    public boolean isItalic() {
        return _isItalic;
    }

    /**
     * Determine if the text is magic/obfuscated.
     */
    public boolean isMagic() {
        return _isMagic;
    }

    /**
     * Determine if the text formatting is reset.
     */
    public boolean isReset() {
        return _isReset;
    }

    /**
     * Determine if the text is striked.
     */
    public boolean isStrikethrough() {
        return _isStrikethrough;
    }

    /**
     * Determine if the text is underline.
     */
    public boolean isUnderline() {
        return _isUnderline;
    }

    /**
     * Get the raw text.
     */
    public String getText() {
        return _text;
    }

    /**
     * Get the formatted text.
     */
    public String getFormatted() {
        if (_formatted == null) {
            synchronized (_sync) {
                if (_formatted != null) {
                    return _formatted;
                }
                _formatted = "";

                if (isReset()) {
                    _formatted += TextFormat.RESET.getFormatCode();
                }
                else {

                    if (_textColor != null)
                        _formatted += _textColor.getFormatCode();

                    if (isBold())
                        _formatted += TextFormat.BOLD.getFormatCode();

                    if (isItalic())
                        _formatted += TextFormat.ITALIC.getFormatCode();

                    if (isMagic())
                        _formatted += TextFormat.MAGIC.getFormatCode();

                    if (isStrikethrough())
                        _formatted += TextFormat.STRIKETHROUGH.getFormatCode();

                    if (isUnderline())
                        _formatted += TextFormat.UNDERLINE.getFormatCode();
                }

                _formatted += TextUtils.format(_text);
            }
        }

        return _formatted;
    }

    @Override
    public int hashCode() {
        return _hash;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    @Override
    public String toString() {
        return getFormatted();
    }
}
