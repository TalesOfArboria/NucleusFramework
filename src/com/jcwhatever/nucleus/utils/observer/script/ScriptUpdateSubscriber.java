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

package com.jcwhatever.nucleus.utils.observer.script;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.update.UpdateSubscriber;

/**
 * An {@link UpdateSubscriber} for use with scripts.
 *
 * <p>A script API method should accept an {@link IScriptUpdateSubscriber}
 * argument which can then be passed into the constructor of {@link ScriptUpdateSubscriber}.
 * The script subscriber can then be used as an {@link UpdateSubscriber} on behalf
 * of a script function.</p>
 */
public class ScriptUpdateSubscriber<A> extends UpdateSubscriber<A> {

    private final IScriptUpdateSubscriber<A> _scriptSubscriber;

    /**
     * Constructor.
     *
     * @param subscriber  The subscriber passed in from a script.
     */
    public ScriptUpdateSubscriber(IScriptUpdateSubscriber<A> subscriber) {
        PreCon.notNull(subscriber);

        _scriptSubscriber = subscriber;
    }

    @Override
    public void on(A argument) {
        _scriptSubscriber.on(argument);
    }
}
