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

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.views.factory.IViewFactory;
import com.jcwhatever.nucleus.views.factory.IViewFactoryStorage;

import javax.annotation.Nullable;

/**
 * Abstract implementation of a view trigger.
 */
public abstract class ViewTrigger implements IViewTrigger {

    private final String _name;
    private final String _searchName;
    private final String _viewFactoryName;
    private final IViewTriggerFactory _triggerFactory;
    private final IViewFactoryStorage _viewStorage;

    private IViewFactory _viewFactory;

    private boolean _isDisposed;

    /**
     * Constructor.
     *
     * @param name                The name of the trigger.
     * @param viewFactoryName     The name of the target view factory.
     * @param viewStorage         The view storage that contains the target view factory.
     * @param triggerFactory      The factory that created the trigger.
     */
    protected ViewTrigger(String name,
                          String viewFactoryName,
                          IViewFactoryStorage viewStorage,
                          IViewTriggerFactory triggerFactory) {

        PreCon.notNullOrEmpty(name);
        PreCon.notNull(triggerFactory);
        PreCon.notNull(viewStorage);
        PreCon.notNullOrEmpty(viewFactoryName);

        _name = name;
        _searchName = name.toLowerCase();
        _triggerFactory = triggerFactory;
        _viewStorage = viewStorage;
        _viewFactoryName = viewFactoryName;
    }

    /**
     * Get the view triggers name.
     */
    @Override
    public String getName() {
        return _name;
    }

    /**
     * Get the view triggers name in lowercase.
     */
    @Override
    public String getSearchName() {
        return _searchName;
    }

    /**
     * Get the triggers target view factory.
     *
     * @return  Null if the view factory is not found.
     */
    @Override
    @Nullable
    public IViewFactory getViewFactory() {
        if (_viewFactory == null) {
            _viewFactory = _viewStorage.getViewFactory(_viewFactoryName);
        }
        return _viewFactory;
    }

    /**
     * Get the trigger factory that created the trigger.
     */
    @Override
    public IViewTriggerFactory getTriggerFactory() {
        return _triggerFactory;
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        _isDisposed = true;
    }
}
