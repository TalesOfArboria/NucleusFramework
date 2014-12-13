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


package com.jcwhatever.bukkit.generic.scripting.api;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.GenericsPlugin;
import com.jcwhatever.bukkit.generic.scheduler.ScheduledTask;
import com.jcwhatever.bukkit.generic.scheduler.TaskHandler;
import com.jcwhatever.bukkit.generic.scripting.IEvaluatedScript;
import com.jcwhatever.bukkit.generic.scripting.ScriptApiInfo;
import com.jcwhatever.bukkit.generic.utils.Scheduler;
import com.jcwhatever.bukkit.generic.utils.text.TextUtils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@ScriptApiInfo(
        variableName = "depends",
        description = "Handle script plugin dependencies.")
public class ScriptApiDepends extends GenericsScriptApi {

    /**
     * Constructor.
     *
     * @param plugin The owning plugin
     */
    public ScriptApiDepends(Plugin plugin) {
        super(plugin);
    }

    @Override
    public IScriptApiObject getApiObject(IEvaluatedScript script) {
        return new ApiObject();
    }

    public static class ApiObject implements IScriptApiObject {

        private List<DependsWrapper> _wrappers = new ArrayList<>(10);
        private ScheduledTask _task;
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
         * @param pluginName  The name of the plugin or multiple comma delimited names.
         * @param runnable    The function to run.
         */
        public void on(String pluginName, Runnable runnable) {
            if (runDepends(pluginName, runnable))
                return;

            DependsWrapper wrapper = new DependsWrapper(pluginName, runnable);
            _wrappers.add(wrapper);

            if (_task == null || _task.isCancelled())
                _task = Scheduler.runTaskRepeat(GenericsLib.getPlugin(), 20, 20, new DependsChecker());
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

                if (plugin instanceof GenericsPlugin && !((GenericsPlugin) plugin).isLoaded())
                    return false;
            }

            try {
                runnable.run();
            }
            catch (Exception e) {
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
                    }
                    else {
                        wrapper.incrementCheck();
                    }
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
