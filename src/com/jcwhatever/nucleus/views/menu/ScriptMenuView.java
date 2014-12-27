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

package com.jcwhatever.nucleus.views.menu;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.views.IViewFactory;
import com.jcwhatever.nucleus.views.ViewSession;
import com.jcwhatever.nucleus.views.data.ViewArguments;
import com.jcwhatever.nucleus.views.data.ViewCloseReason;
import com.jcwhatever.nucleus.views.data.ViewOpenReason;
import com.jcwhatever.nucleus.views.data.ViewResults;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

/**
 * A menu view used for scripts.
 */
public class ScriptMenuView extends MenuView {

    private int _totalSlots;

    protected ScriptMenuView(@Nullable String title, ViewSession session,
                             IViewFactory factory, ViewArguments arguments, int totalSlots) {
        super(title, session, factory, arguments, null);

        PreCon.isValid(totalSlots <= MAX_SLOTS, "Total slots cannot be greater than " + MAX_SLOTS);

        _totalSlots = totalSlots;
    }

    @Override
    protected List<MenuItem> createMenuItems() {
        return new ArrayList<>(0);
    }

    @Override
    protected void onItemSelect(MenuItem menuItem) {
        // do nothing
    }

    @Override
    protected void onShow(ViewOpenReason reason) {
        // do nothing
    }

    @Override
    protected void onClose(ViewCloseReason reason) {
        // do nothing
    }

    @Nullable
    @Override
    public ViewResults getResults() {
        return null;
    }

    @Override
    protected int getSlotsRequired() {
        int rows = (int) Math.ceil((double)_totalSlots / ROW_SIZE);
        return Math.max(rows * ROW_SIZE, ROW_SIZE);
    }

}
