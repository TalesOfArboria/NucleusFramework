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

package com.jcwhatever.nucleus.views.triggers;

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.mixins.INamedInsensitive;
import com.jcwhatever.nucleus.storage.settings.ISettable;
import com.jcwhatever.nucleus.storage.settings.ISettingsManager;
import com.jcwhatever.nucleus.views.IViewFactory;

/**
 * Represents a type that triggers a view
 * to show to a player.
 *
 * <p>How the trigger is triggered is determined by the
 * implementation.</p>
 */
public interface IViewTrigger extends ISettable, INamedInsensitive, IDisposable {

    /**
     * Get the view that is triggered.
     */
    public IViewFactory getViewFactory();

    /**
     * Get the factory that created the trigger.
     */
    public IViewTriggerFactory getTriggerFactory();

    /**
     * Get the triggers settings manager.
     */
    @Override
    ISettingsManager getSettingsManager();

    /**
     * Release resources used by the view.
     */
    @Override
    void dispose();
}
