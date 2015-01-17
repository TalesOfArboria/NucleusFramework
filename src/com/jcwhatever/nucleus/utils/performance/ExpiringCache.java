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

package com.jcwhatever.nucleus.utils.performance;

import com.jcwhatever.nucleus.utils.TimeScale;
import com.jcwhatever.nucleus.utils.DateUtils;

import java.util.Date;
import javax.annotation.Nullable;

/*
 * 
 */
public abstract class ExpiringCache {

    private int _lifespan;
    private TimeScale _timeScale;
    private Date _expires;

    /**
     * Constructor.
     *
     * @param lifespan   The cached value lifespan.
     * @param timeScale  The lifespan time scale.
     */
    protected ExpiringCache(int lifespan, TimeScale timeScale) {
        _lifespan = lifespan;
        _timeScale = timeScale;
    }

    /**
     * Get the lifespan. Values of 0 or less indicate
     * the cache value does not expire.
     */
    public int getLifespan() {
        return _lifespan;
    }

    /**
     * Get the lifespan timescale.
     */
    public TimeScale getTimeScale() {
        return _timeScale;
    }

    /**
     * Determine if the cache is expired.
     */
    public boolean isExpired() {
        return _expires != null && _expires.compareTo(new Date()) <= 0;
    }

    /**
     * Expire the cache.
     */
    public void expireNow() {
        _expires = null;
    }

    /**
     * Resets the expiration. Returns the new
     * expiration date.
     *
     * @return Null if the lifespan is unlimited.
     */
    @Nullable
    public Date resetExpires() {
        return resetExpires(_lifespan);
    }

    /**
     * Resets the expiration. Returns the new
     * expiration date.
     *
     * @param lifespan  Reset using a different lifespan value than default.
     *
     * @return Null if the lifespan is unlimited.
     */
    @Nullable
    public Date resetExpires(int lifespan) {

        if (lifespan > 0) {

            switch (_timeScale) {
                case MILLISECONDS:
                    _expires = DateUtils.addMilliseconds(new Date(), lifespan);
                    break;

                case SECONDS:
                    _expires = DateUtils.addSeconds(new Date(), lifespan);
                    break;

                case TICKS:
                    _expires = DateUtils.addMilliseconds(new Date(), lifespan * 50);
                    break;

                default:
                    throw new AssertionError();
            }
            return _expires;
        }
        return null;
    }

}
