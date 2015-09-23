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

package com.jcwhatever.nucleus.utils.text.format.args;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.nucleus.utils.text.components.IChatComponent;
import com.jcwhatever.nucleus.utils.text.components.IChatMessage;

import java.util.Collection;
import java.util.List;

/**
 * A text argument.
 */
public class TextArg implements IFormatterArg {

    private final IChatMessage _message;

    /**
     * Constructor.
     *
     * @param text       The text.
     * @param modifiers  Optional modifiers.
     */
    public TextArg(CharSequence text, IFormatterArgModifier... modifiers) {
        PreCon.notNull(text);
        PreCon.notNull(modifiers);

        _message = TextUtils.TEXT_FORMATTER.format(text);

        List<IChatComponent> components = _message.getComponents();
        for (IFormatterArgModifier modifier : modifiers) {
            for (IChatComponent component : components) {
                modifier.applyTo(component);
            }
        }
    }

    @Override
    public List<IChatComponent> getComponents() {
        return _message.getComponents();
    }

    @Override
    public <T extends Collection<IChatComponent>> T getComponents(T output) {
        return _message.getComponents(output);
    }
}
