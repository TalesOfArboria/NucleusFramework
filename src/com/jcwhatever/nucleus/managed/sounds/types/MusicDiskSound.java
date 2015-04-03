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


package com.jcwhatever.nucleus.managed.sounds.types;

import com.jcwhatever.nucleus.storage.IDataNode;

/**
 * A resource sound for a music disc.
 */
public class MusicDiskSound extends ResourceSound {

    private int _diskId;

    /**
     * Constructor.
     *
     * @param dataNode  The resource sound data node.
     */
    public MusicDiskSound(IDataNode dataNode) {
        super(dataNode);
    }

    /**
     * Get the disk id.
     */
    public final int getDiskId() {
        return _diskId;
    }

    /*
     * Get the name of the disk from the id.
     */
    @Override
    protected String loadName(IDataNode dataNode) {

        _diskId = dataNode.getInteger("disk-id");

        switch (_diskId) {
            case 2256:
                return "records.13";

            case 2257:
                return "records.cat";

            case 2258:
                return "records.blocks";

            case 2259:
                return "records.chirp";

            case 2260:
                return "records.far";

            case 2261:
                return "records.mall";

            case 2262:
                return "records.mellohi";

            case 2263:
                return "records.stal";

            case 2264:
                return "records.strad";

            case 2265:
                return "records.ward";

            case 2266:
                return "records.11";

            case 2267:
                return "records.wait";

            default:
                throw new RuntimeException("Invalid record ID detected in resource sounds.");
        }
    }
}
