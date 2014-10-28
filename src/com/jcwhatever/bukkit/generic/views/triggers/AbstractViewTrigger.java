/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.views.triggers;

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.settings.ISettingsManager;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.generic.storage.settings.SettingsManager;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.views.IView;
import com.jcwhatever.bukkit.generic.views.ViewManager;

/**
 * Abstract implementation of a view trigger.
 */
public abstract class AbstractViewTrigger implements IViewTrigger {

    private IView _view;
    private IDataNode _dataNode;
    private ViewManager _viewManager;
    private SettingsManager _settingsManager;

    @Override
    public final void init(IView view, ViewManager viewManager, IDataNode dataNode) {
        PreCon.notNull(view);
        PreCon.notNull(viewManager);
        PreCon.notNull(dataNode);

        _view = view;
        _dataNode = dataNode;
        _viewManager = viewManager;

        _settingsManager = new SettingsManager(dataNode, getPossibleSettings());
        _settingsManager.addOnSettingsChanged(new Runnable() {

            @Override
            public void run() {
                onLoadSettings(_dataNode);
            }

        }, true);

        onInit(view, viewManager, dataNode);
    }

    @Override
    public final IView getView() {
        return _view;
    }

    @Override
    public final ViewManager getViewManager() {
        return _viewManager;
    }

    @Override
    public final ISettingsManager getSettingsManager() {
        return _settingsManager;
    }

    @Override
    public abstract void dispose();

    /**
     * Called after the view trigger is initialized.
     *
     * @param view         The view that is triggered.
     * @param viewManager  The owning view manager.
     * @param dataNode     The triggers data node.
     */
    protected abstract void onInit(IView view, ViewManager viewManager, IDataNode dataNode);

    /**
     * Called when the settings are reloaded.
     *
     * @param dataNode  The triggers data node.
     */
    protected abstract void onLoadSettings(IDataNode dataNode);

    /**
     * Called to retrieve the implementations setting definitions.
     */
    protected abstract SettingDefinitions getPossibleSettings();

}
