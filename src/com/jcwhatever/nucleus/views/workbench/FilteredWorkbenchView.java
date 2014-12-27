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

package com.jcwhatever.nucleus.views.workbench;

import com.jcwhatever.nucleus.utils.items.ItemFilterManager;
import com.jcwhatever.nucleus.views.ViewSession;
import com.jcwhatever.nucleus.views.data.ViewArguments;
import com.jcwhatever.nucleus.views.data.ViewOpenReason;

import javax.annotation.Nullable;

/**
 * A workbench view that can allow or deny specific items to be crafted.
 */
public class FilteredWorkbenchView extends WorkbenchView {

    private final FilteredWorkbenchFactory _factory;
    private final ItemFilterManager _filter;

    /**
     * Constructor.
     *
     * @param session        The player view session.
     * @param factory        The factory that instantiated the view.
     * @param arguments      Meta arguments for the view. (FilteredWorkbenchView does not take
     *                       arguments but overriding implementations might)
     * @param filterManager  The filter manager used to allow or deny specific items.
     */
    public FilteredWorkbenchView(ViewSession session, FilteredWorkbenchFactory factory,
                                 ViewArguments arguments, ItemFilterManager filterManager) {
        super(session, factory, arguments);

        _filter = filterManager;
        _factory = factory;
    }

    /**
     * Get the views item filter manager.
     */
    @Nullable
    public ItemFilterManager getFilterManager() {
        return _filter;
    }

    @Override
    protected boolean openView(ViewOpenReason reason) {
        if (super.openView(reason)) {
            _factory.registerInventory(this);
            return true;
        }
        return false;
    }
}
