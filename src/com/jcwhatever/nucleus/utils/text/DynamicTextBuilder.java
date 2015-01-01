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
import java.util.List;

/**
 * Generates a dynamic text composite by appending text and {@Code IDynamicText}.
 */
public class DynamicTextBuilder {

    private List<Object> _components = new ArrayList<>(5);
    private int _interval = 0;

    /**
     * Append non-dynamic text.
     *
     * @param text  The text to append.
     * @param args  Optional format arguments.
     *
     * @return  Self for chaining.
     */
    public DynamicTextBuilder append(Object text, Object... args) {
        PreCon.notNull(text);
        PreCon.notNull(args);

        _components.add(TextUtils.format(text, args));

        return this;
    }

    /**
     * Append dynamic text generator.
     *
     * @param dynamicText  The dynamic text.
     *
     * @return  Self for chaining.
     */
    public DynamicTextBuilder append(IDynamicText dynamicText) {
        PreCon.notNull(dynamicText);

        _components.add(dynamicText);

        return this;
    }

    /**
     * Set the preferred interval the text is refreshed at.
     *
     * <p>Overrides the interval of any {@code IDynamicText}
     * objects that are appended.</p>
     *
     * @param ticks  The refresh interval in ticks.
     *
     * @return  Self for chaining.
     */
    public DynamicTextBuilder refreshRate(int ticks) {
        _interval = ticks;
        return this;
    }

    /**
     * Build a new {@code IDynamicText} instance.
     */
    public IDynamicText build() {

        StringBuilder buffer = new StringBuilder(_components.size() * 15);
        List<IDynamicText> dynText = new ArrayList<>(_components.size());

        int dynCount = 0;

        for (Object component : _components) {
            if (component instanceof IDynamicText) {
                buffer.append('{')
                        .append(dynCount)
                        .append('}');

                dynText.add((IDynamicText) component);
                dynCount++;
            } else {
                buffer.append(component);
            }
        }

        IDynamicText[] dyn = dynText.toArray(new IDynamicText[dynText.size()]);

        return new DynamicTextComposite(buffer.toString(), _interval, dyn);
    }
}
