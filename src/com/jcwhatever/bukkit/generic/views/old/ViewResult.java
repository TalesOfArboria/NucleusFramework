/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.views.old;

import com.jcwhatever.bukkit.generic.views.data.ViewArguments;

import javax.annotation.Nullable;

/**
 * Meta data specifically for returning a result from a view instance
 * after it is closed.
 */
public class ViewResult extends ViewArguments {

    private ViewInstance _viewInstance;
    private boolean _isCancelled = false;

    /**
     * Constructor.
     *
     * @param viewInstance  The view instance the result is for.
     */
    public ViewResult(ViewInstance viewInstance) {

        _viewInstance = viewInstance;
    }

    /**
     * Get the view instance the result is for.
     */
    public ViewInstance getViewInstance () {

        return _viewInstance;
    }

    /**
     * Get instance meta used to initialized the
     * view instance the result is from.
     */
    @Nullable
    public ViewArguments getInstanceMeta () {

        return _viewInstance.getInstanceMeta();
    }

    /**
     * Determine if the result is cancelled.
     * Results should be disregarded if this returns true.
     */
    public boolean isCancelled () {

        return _isCancelled;
    }

    /**
     * Set the cancelled flag.
     *
     * @param isCancelled
     */
    public void setIsCancelled (boolean isCancelled) {

        _isCancelled = isCancelled;
    }

}
