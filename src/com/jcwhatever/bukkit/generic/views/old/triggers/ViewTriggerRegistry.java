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


package com.jcwhatever.bukkit.generic.views.old.triggers;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import javax.annotation.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Central view trigger registry.
 * <p>
 *     Register view trigger types here if other plugins should be able to use them.
 * </p>
 */
public class ViewTriggerRegistry {

    private ViewTriggerRegistry() {}

    private static Map<String, Class<? extends IViewTrigger>> _triggerMap = new HashMap<>(15);

    static {
        registerType(BlockTypeTrigger.class);
    }

    /**
     * Register a view trigger type.
     *
     * @param triggerClass  The trigger type class.
     *
     * @return  True if the type was registered.
     */
    public static boolean registerType(Class<? extends IViewTrigger> triggerClass) {
        PreCon.notNull(triggerClass);
        _triggerMap.put(triggerClass.getSimpleName().toLowerCase(), triggerClass);
        return true;
    }

    /**
     * Get the names of all registered triggers.
     * <p>
     *     The names of the triggers are the triggers class
     *     simple name in lowercase.
     * </p>
     */
    public static Set<String> getTriggerNames() {
        return new HashSet<>(_triggerMap.keySet());
    }

    /**
     * Get a view trigger type by name.
     * <p>
     *     The names of the triggers are the triggers class
     *     simple name in lowercase.
     * </p>
     *
     * @param name  The name of the trigger.
     *
     * @return  Null if not found.
     */
    @Nullable
    public static Class<? extends IViewTrigger> getTrigger(String name) {
        PreCon.notNullOrEmpty(name);

        return _triggerMap.get(name.toLowerCase());
    }

}
