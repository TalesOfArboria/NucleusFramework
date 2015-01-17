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

package com.jcwhatever.nucleus.internal.nms;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.Nucleus.NmsHandlers;
import com.jcwhatever.nucleus.internal.nms.v1_8_R1.NmsActionBarHandler_v1_8_R1;
import com.jcwhatever.nucleus.internal.nms.v1_8_R1.NmsListHeaderFooterHandler_v1_8_R1;
import com.jcwhatever.nucleus.internal.nms.v1_8_R1.NmsTitleHandler_v1_8_R1;
import com.jcwhatever.nucleus.utils.nms.NmsManager;

/**
 * NucleusFramework's internal NMS manager.
 */
public final class InternalNmsManager extends NmsManager {

    public InternalNmsManager() {
        super(Nucleus.getPlugin());

        registerNmsHandler("v1_8_R1", NmsHandlers.TITLES.name(), NmsTitleHandler_v1_8_R1.class);
        registerNmsHandler("v1_8_R1", NmsHandlers.ACTION_BAR.name(), NmsActionBarHandler_v1_8_R1.class);
        registerNmsHandler("v1_8_R1", NmsHandlers.LIST_HEADER_FOOTER.name(), NmsListHeaderFooterHandler_v1_8_R1.class);
    }
}
