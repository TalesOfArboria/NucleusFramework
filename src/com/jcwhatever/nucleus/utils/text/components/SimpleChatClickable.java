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

/**
 * Simple implementation of {@link IChatClickable}.
 */
public class SimpleChatClickable implements IChatClickable {

    private final ClickAction _action;
    private final String _argument;

    /**
     * Constructor.
     *
     * @param action    The click action.
     * @param argument  The click action argument.
     */
    public SimpleChatClickable(ClickAction action, String argument) {
        PreCon.notNull(action);
        PreCon.notNull(argument);

        _action = action;
        _argument = argument;
    }

    @Override
    public ClickAction getAction() {
        return _action;
    }

    @Override
    public String getArgument() {
        return _argument;
    }
}
