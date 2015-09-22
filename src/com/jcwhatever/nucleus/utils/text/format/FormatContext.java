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

import com.jcwhatever.nucleus.utils.text.components.SimpleChatComponent;
import com.jcwhatever.nucleus.utils.text.components.SimpleChatModifier;
import com.jcwhatever.nucleus.utils.text.components.IChatComponent;
import com.jcwhatever.nucleus.utils.text.components.IChatMessage;
import com.jcwhatever.nucleus.utils.text.components.IChatModifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * 
 */
public class FormatContext implements IChatComponent, IFormatterAppendable {

    StringBuilder componentBuffer;
    IChatModifier modifier = new SimpleChatModifier();
    boolean isModified;
    List<IChatComponent> results = new ArrayList<>(20);
    int lineLen = 0;

    FormatContext() {
        componentBuffer = new StringBuilder(100);
    }

    FormatContext(int len) {
        componentBuffer = new StringBuilder(len);
    }

    void newLine() {
        IChatModifier currModifier = modifier;
        reset(modifier);
        results.add(new FormatNewLine());
        lineLen = 0;
        reset(new SimpleChatModifier(currModifier)); // reset using same modifier
    }

    void prependNewLine() {
        if (results.isEmpty())
            results.add(new FormatNewLine());
        else
            results.add(results.size() - 1, new FormatNewLine());
    }

    void incrementCharCount(int amount) {
        lineLen += amount;
    }

    void reset() {
        reset(new SimpleChatModifier());
    }

    void reset(IChatModifier modifier) {
        if (isModified()) {
            results.add(new SimpleChatComponent(componentBuffer.toString(), this.modifier));
        }
        componentBuffer.setLength(0);
        this.modifier = modifier;
    }

    void hardReset() {
        reset();
        results.clear();
        lineLen = 0;
    }

    boolean isModified() {
        return isModified || modifier.isModified();
    }

    @Override
    public void append(Object object) {
        isModified = true;

        if (object instanceof IChatMessage) {
            reset();
            ((IChatMessage) object).getComponents(results);
            return;
        }

        componentBuffer.append(object);
    }

    public void appendPrefix(IChatMessage message) {
        IChatModifier currMod = modifier;
        reset();
        message.getComponents(results);
        reset(currMod);
    }

    @Override
    public String getText() {
        return componentBuffer.toString();
    }

    @Override
    public void getText(Appendable output) {
        try {
            output.append(componentBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getFormatted() {
        return modifier.getFormatted() + getText();
    }

    @Override
    public void getFormatted(Appendable output) {
        modifier.getFormatted(output);
        getText(output);
    }

    @Override
    public IChatModifier getModifier() {
        return modifier;
    }

    @Override
    public void setModifier(IChatModifier modifier) {
        this.modifier = modifier;
    }
}
