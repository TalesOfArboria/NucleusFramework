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

package com.jcwhatever.nucleus.internal.managed.resourcepacks;

import com.jcwhatever.nucleus.managed.resourcepacks.IResourcePack;
import com.jcwhatever.nucleus.managed.resourcepacks.sounds.types.IVoiceSound;
import com.jcwhatever.nucleus.managed.sounds.Transcript;
import com.jcwhatever.nucleus.storage.IDataNode;

/**
 * Internal implementation of {@link IVoiceSound}.
 */
class SoundVoice extends SoundResource implements IVoiceSound{

    private final Transcript _transcript;

    /**
     * Constructor.
     *
     * @param resourcePack  The resource pack the sound belongs to.
     * @param dataNode      The resource sounds data node.
     */
    SoundVoice(IResourcePack resourcePack, IDataNode dataNode) {
        super(resourcePack, dataNode);

        String transcript = dataNode.getString("transcript", "");
        _transcript = new Transcript(transcript);
    }

    /**
     * Get the sounds {@link Transcript} object.
     */
    @Override
    public final Transcript getTranscript() {
        return _transcript;
    }
}
