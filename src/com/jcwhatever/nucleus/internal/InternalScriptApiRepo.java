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

package com.jcwhatever.nucleus.internal;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.scripting.ScriptApiRepo;
import com.jcwhatever.nucleus.scripting.api.ScriptApiDepends;
import com.jcwhatever.nucleus.scripting.api.ScriptApiEconomy;
import com.jcwhatever.nucleus.scripting.api.ScriptApiEvents;
import com.jcwhatever.nucleus.scripting.api.ScriptApiInventory;
import com.jcwhatever.nucleus.scripting.api.ScriptApiItemBank;
import com.jcwhatever.nucleus.scripting.api.ScriptApiJail;
import com.jcwhatever.nucleus.scripting.api.ScriptApiMsg;
import com.jcwhatever.nucleus.scripting.api.ScriptApiPermissions;
import com.jcwhatever.nucleus.scripting.api.ScriptApiRand;
import com.jcwhatever.nucleus.scripting.api.ScriptApiScheduler;
import com.jcwhatever.nucleus.scripting.api.ScriptApiSounds;

/**
 * NucleusFramework's script api repository.
 */
public final class InternalScriptApiRepo extends ScriptApiRepo {

    /**
     * Private Constructor.
     */
    public InternalScriptApiRepo() {
        super();

        registerApiType(Nucleus.getPlugin(), ScriptApiEvents.class);
        registerApiType(Nucleus.getPlugin(), ScriptApiEconomy.class);
        registerApiType(Nucleus.getPlugin(), ScriptApiInventory.class);
        registerApiType(Nucleus.getPlugin(), ScriptApiItemBank.class);
        registerApiType(Nucleus.getPlugin(), ScriptApiJail.class);
        registerApiType(Nucleus.getPlugin(), ScriptApiMsg.class);
        registerApiType(Nucleus.getPlugin(), ScriptApiPermissions.class);
        registerApiType(Nucleus.getPlugin(), ScriptApiSounds.class);
        registerApiType(Nucleus.getPlugin(), ScriptApiDepends.class);
        registerApiType(Nucleus.getPlugin(), ScriptApiRand.class);
        registerApiType(Nucleus.getPlugin(), ScriptApiScheduler.class);
    }
}
