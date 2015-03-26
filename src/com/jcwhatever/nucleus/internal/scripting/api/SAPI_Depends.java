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


package com.jcwhatever.nucleus.internal.scripting.api;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.NucleusPlugin;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Scheduler;
import com.jcwhatever.nucleus.utils.scheduler.IScheduledTask;
import com.jcwhatever.nucleus.utils.scheduler.TaskHandler;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SAPI_Depends implements IDisposable {

    private List<DependsWrapper> _wrappers = new ArrayList<>(10);
    private IScheduledTask _task;
    private boolean _isDisposed;

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {

        if (_task != null)
            _task.cancel();

        _wrappers.clear();

        _isDisposed = true;
    }

    /**
     * Provide a runnable function to be run once the specified
     * plugin is loaded.
     *
     * @param pluginName The name of the plugin or multiple comma delimited names.
     * @param runnable   The function to run.
     */
    public void on(String pluginName, Runnable runnable) {
        if (runDepends(pluginName, runnable))
            return;

        DependsWrapper wrapper = new DependsWrapper(pluginName, runnable);
        _wrappers.add(wrapper);

        if (_task == null || _task.isCancelled())
            _task = Scheduler.runTaskRepeat(Nucleus.getPlugin(), 20, 20, new DependsChecker());
    }

    /**
     * Get a class by name.
     *
     * <p>Used when the class is not in the classpath and cannot be accessed
     * normally via scripts.</p>
     *
     * @param clazzName The name of the class to get.
     *
     * @return The class.
     */
    public Class<?> clazz(String clazzName) {
        PreCon.notNullOrEmpty(clazzName, "clazzName");

        try {
            return Class.forName(clazzName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the value of a static field.
     *
     * <p>Used when the class is not in the classpath and cannot be accessed
     * normally via scripts.</p>
     *
     * @param clazzName The name of the class the static field is in.
     * @param fieldName The name of the static field.
     *
     * @return The value of the field.
     */
    public Object staticField(String clazzName, String fieldName) {
        PreCon.notNullOrEmpty(clazzName, "clazzName");
        PreCon.notNullOrEmpty(fieldName, "fieldName");

        Class<?> clazz = clazz(clazzName);

        Field field;
        try {
            field = clazz.getField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        try {
            return field.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Attempt to run runnable if dependency plugin is loaded.
     */
    private boolean runDepends(String pluginNames, Runnable runnable) {

        String[] names = TextUtils.PATTERN_COMMA.split(pluginNames);

        for (String pluginName : names) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName.trim());
            if (plugin == null)
                return false;

            if (!plugin.isEnabled())
                return false;

            if (plugin instanceof NucleusPlugin && !((NucleusPlugin) plugin).isLoaded())
                return false;
        }

        try {
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private class DependsChecker extends TaskHandler {

        @Override
        public void run() {

            if (_wrappers.isEmpty()) {
                cancelTask();
                return;
            }

            Iterator<DependsWrapper> iterator = _wrappers.iterator();

            while (iterator.hasNext()) {

                DependsWrapper wrapper = iterator.next();

                if (wrapper.getTotalChecks() > 60 ||
                        runDepends(wrapper.getPluginName(), wrapper.getRunnable())) {

                    iterator.remove();
                } else {
                    wrapper.incrementCheck();
                }
            }
        }
    }

    private static class DependsWrapper {
        private final String _pluginName;
        private final Runnable _runnable;
        private int _totalChecks;

        DependsWrapper(String pluginName, Runnable runnable) {
            _pluginName = pluginName;
            _runnable = runnable;
        }

        public int getTotalChecks() {
            return _totalChecks;
        }

        public void incrementCheck() {
            _totalChecks++;
        }

        public String getPluginName() {
            return _pluginName;
        }

        public Runnable getRunnable() {
            return _runnable;
        }
    }
}

