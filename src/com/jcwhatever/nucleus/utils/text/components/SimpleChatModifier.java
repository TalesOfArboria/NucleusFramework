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

package com.jcwhatever.nucleus.utils.text.components;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextColor;
import com.jcwhatever.nucleus.utils.text.TextFormat;

import javax.annotation.Nullable;
import java.io.IOException;

/**
 * Simple implementation of {@link IChatModifier}.
 */
public class SimpleChatModifier implements IChatModifier {

    private boolean _isBold;
    private boolean _isItalic;
    private boolean _isStrikeThrough;
    private boolean _isUnderlined;
    private boolean _isMagic;
    private boolean _isReset;
    private TextColor _color;
    private IChatClickable _clickable;
    private IChatHoverable _hoverable;

    /**
     * Constructor.
     *
     * <p>Creates empty modifier.</p>
     */
    public SimpleChatModifier() {}

    /**
     * Constructor.
     *
     * @param modifier  The modifier to copy settings from.
     */
    public SimpleChatModifier(IChatModifier modifier) {
        _isBold = modifier.isBold();
        _isItalic = modifier.isItalic();
        _isStrikeThrough = modifier.isStrikeThrough();
        _isUnderlined = modifier.isUnderlined();
        _isMagic = modifier.isMagic();
        _color = modifier.getColor();
        _clickable = modifier.getClickable();
        _hoverable = modifier.getHoverable();
    }

    @Override
    public boolean isModified() {
        return _color != null || _isBold || _isItalic || _isStrikeThrough
                || _isUnderlined || _isMagic || _clickable != null || _hoverable != null || _isReset;
    }

    @Override
    public boolean isBold() {
        return _isBold;
    }

    @Override
    public void setBold(boolean isBold) {
        _isReset = false;
        _isBold = isBold;
    }

    @Override
    public boolean isItalic() {
        return _isItalic;
    }

    @Override
    public void setItalic(boolean isItalic) {
        _isReset = false;
        _isItalic = isItalic;
    }

    @Override
    public boolean isStrikeThrough() {
        return _isStrikeThrough;
    }

    @Override
    public void setStrikeThrough(boolean isStrikeThrough) {
        _isReset = false;
        _isStrikeThrough = isStrikeThrough;
    }

    @Override
    public boolean isUnderlined() {
        return _isUnderlined;
    }

    @Override
    public void setUnderline(boolean isUnderlined) {
        _isReset = false;
        _isUnderlined = isUnderlined;
    }

    @Override
    public boolean isMagic() {
        return _isMagic;
    }

    @Override
    public void setMagic(boolean isRandom) {
        _isReset = false;
        _isMagic = isRandom;
    }

    @Override
    public boolean isReset() {
        return _isReset;
    }

    @Override
    public void reset() {
        _isBold = false;
        _isItalic = false;
        _isStrikeThrough = false;
        _isUnderlined = false;
        _isMagic = false;
        _color = null;
        _isReset = true;
    }

    @Override
    @Nullable
    public TextColor getColor() {
        return _color;
    }

    @Override
    public void setColor(@Nullable TextColor color) {
        _isReset = false;
        _color = color;
    }

    @Nullable
    @Override
    public IChatClickable getClickable() {
        return _clickable;
    }

    @Override
    public void setClickable(@Nullable IChatClickable clickable) {
        _clickable = clickable;
    }

    @Nullable
    @Override
    public IChatHoverable getHoverable() {
        return _hoverable;
    }

    @Override
    public void setHoverable(@Nullable IChatHoverable hoverable) {
        _hoverable = hoverable;
    }

    @Override
    public String getFormatted() {
        StringBuilder sb = new StringBuilder(8);
        getFormatted(sb);
        return sb.toString();
    }

    @Override
    public void getFormatted(Appendable output) {
        PreCon.notNull(output);

        try {
            if (_isReset) {
                output.append(TextFormat.RESET.getFormatCode());
                return;
            }
            if (_color != null) {
                output.append(_color.getFormatCode());
            }
            if (_isBold) {
                output.append(TextFormat.BOLD.getFormatCode());
            }
            if (_isItalic) {
                output.append(TextFormat.ITALIC.getFormatCode());
            }
            if (_isStrikeThrough) {
                output.append(TextFormat.STRIKETHROUGH.getFormatCode());
            }
            if (_isUnderlined) {
                output.append(TextFormat.UNDERLINE.getFormatCode());
            }
            if (_isMagic) {
                output.append(TextFormat.MAGIC.getFormatCode());
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return getFormatted();
    }
}
