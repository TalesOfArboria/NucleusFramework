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

package com.jcwhatever.nucleus.internal.managed.nms;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.internal.managed.nms.v1_8_R1.NmsActionBarHandler_v1_8_R1;
import com.jcwhatever.nucleus.internal.managed.nms.v1_8_R1.NmsListHeaderFooterHandler_v1_8_R1;
import com.jcwhatever.nucleus.internal.managed.nms.v1_8_R1.NmsSoundEffectHandler_v1_8_R1;
import com.jcwhatever.nucleus.internal.managed.nms.v1_8_R1.NmsTitleHandler_v1_8_R1;
import com.jcwhatever.nucleus.internal.managed.nms.v1_8_R2.NmsActionBarHandler_v1_8_R2;
import com.jcwhatever.nucleus.internal.managed.nms.v1_8_R2.NmsListHeaderFooterHandler_v1_8_R2;
import com.jcwhatever.nucleus.internal.managed.nms.v1_8_R2.NmsParticleEffectHandler_v1_8_R2;
import com.jcwhatever.nucleus.internal.managed.nms.v1_8_R2.NmsSoundEffectHandler_v1_8_R2;
import com.jcwhatever.nucleus.internal.managed.nms.v1_8_R2.NmsTitleHandler_v1_8_R2;
import com.jcwhatever.nucleus.utils.nms.NmsManager;

/**
 * NucleusFramework's internal NMS manager.
 */
public final class InternalNmsManager extends NmsManager {

    /**
     * The name of the internal action bar handler.
     */
    public static final String ACTION_BAR = "ACTION_BAR";

    /**
     * The name of the internal list header/footer handler.
     */
    public static final String LIST_HEADER_FOOTER = "LIST_HEADER_FOOTER";

    /**
     * The name of the internal particle effect handler.
     */
    public static final String PARTICLE_EFFECT = "PARTICLE_EFFECT";

    /**
     * The name of the internal titles handler.
     */
    public static final String TITLES = "TITLES";

    /**
     * The name of the internal sound effect handler.
     */
    public static final String SOUND_EFFECT = "SOUND_EFFECT";

    public InternalNmsManager() {
        super(Nucleus.getPlugin());

        registerHandler("v1_8_R1", TITLES, NmsTitleHandler_v1_8_R1.class);
        registerHandler("v1_8_R1", ACTION_BAR, NmsActionBarHandler_v1_8_R1.class);
        registerHandler("v1_8_R1", LIST_HEADER_FOOTER, NmsListHeaderFooterHandler_v1_8_R1.class);
        registerHandler("v1_8_R1", SOUND_EFFECT, NmsSoundEffectHandler_v1_8_R1.class);

        registerHandler("v1_8_R2", TITLES, NmsTitleHandler_v1_8_R2.class);
        registerHandler("v1_8_R2", ACTION_BAR, NmsActionBarHandler_v1_8_R2.class);
        registerHandler("v1_8_R2", LIST_HEADER_FOOTER, NmsListHeaderFooterHandler_v1_8_R2.class);
        registerHandler("v1_8_R2", SOUND_EFFECT, NmsSoundEffectHandler_v1_8_R2.class);
        registerHandler("v1_8_R2", PARTICLE_EFFECT, NmsParticleEffectHandler_v1_8_R2.class);
    }
}
