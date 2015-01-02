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
import com.jcwhatever.nucleus.utils.text.TextColor.TextColorMap;

import java.util.Map.Entry;

/**
 * A {@code CircularString} that ignores color codes while
 * preserving the colors in output strings.
 *
 * <p>The length of the string may be dynamic after rotation when color
 * codes need to be inserted or removed in order to maintain the text color.</p>
 */
public class ColoredCircularString extends CircularString {

    private TextColorMap _colorMap;

    /**
     * Constructor.
     */
    public ColoredCircularString() {
        super();
    }

    /**
     * Constructor.
     *
     * @param characters  The initial string characters.
     */
    public ColoredCircularString(char[] characters) {
        PreCon.notNull(characters);

        _colorMap = TextColor.separate(characters);
        _string = _colorMap.getText().toCharArray();
    }

    /**
     * Constructor.
     *
     * @param string  The initial string.
     */
    public ColoredCircularString(String string) {
        PreCon.notNull(string);

        _colorMap = TextColor.separate(string);
        _string = _colorMap.getText().toCharArray();
    }

    /**
     * Set the string. The rotation is not changed.
     *
     * @param string  The string to set.
     */
    @Override
    public void setString(String string) {
        PreCon.notNull(string);

        _colorMap = TextColor.separate(string);
        _string = _colorMap.getText().toCharArray();
        _hasResult = false;
    }

    /**
     * Get a new {@code CircularString} whose characters are
     * a sub-sequence of characters from the current.
     *
     * @param start  The start index of the sequence relative to the current rotation.
     * @param end    The end index (+1) of the sequence relative to the current rotation.
     */
    @Override
    public ColoredCircularString subSequence(int start, int end) {

        CircularString result = super.subSequence(start, end);

        TextColorMap colorMap = new TextColorMap();

        for (int i = start; i < end; i++) {

            int colorIndex = getCorrectIndex(_rotation + i);

            if (i == start) {
                Entry<Integer, String> entry = _colorMap.floorEntry(colorIndex);
                if (entry != null)
                    colorMap.put(i - start, entry.getValue());
            }
            else {
                String color = _colorMap.get(colorIndex);
                if (color != null)
                    colorMap.put(i - start, color);
            }
        }

        ColoredCircularString coloredResult = new ColoredCircularString();
        coloredResult._string = result._string;
        coloredResult._colorMap = colorMap;

        colorMap.setText(new String(result._string));

        return coloredResult;
    }

    @Override
    protected void updateResult() {

        if (_hasResult)
            return;

        StringBuilder buffer = new StringBuilder(_string.length + (_colorMap.size() * 4));

        for (int i=0; i < _string.length; i++) {
            String color = _colorMap.get(i);
            if (color != null)
                buffer.append(color);

            buffer.append(_string[(_rotation + i) % _string.length]);
        }

        _result = buffer.toString().toCharArray();

        _hasResult = true;
    }
}
