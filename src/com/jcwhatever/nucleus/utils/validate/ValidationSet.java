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

package com.jcwhatever.nucleus.utils.validate;

import com.jcwhatever.nucleus.collections.wrap.SetWrapper;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Used for validation using a set of elements.
 */
public class ValidationSet<E> extends SetWrapper<E> implements IValidator<E> {

    protected final Set<E> _set;
    protected ValidationPolicy _policy = ValidationPolicy.WHITELIST;

    /**
     * Constructor.
     */
    public ValidationSet() {
        this(10);
    }

    /**
     * Constructor.
     *
     * @param capacity  The initial capacity.
     */
    public ValidationSet(int capacity) {
        _set = new HashSet<>(capacity);
    }

    /**
     * Constructor.
     *
     * @param collection  The initial collection.
     */
    public ValidationSet(Collection<? extends E> collection) {
        _set = new HashSet<>(collection);
    }

    /**
     * Get the validation policy.
     */
    public ValidationPolicy getPolicy() {
        return _policy;
    }

    /**
     * Set the validation policy.
     *
     * @param policy  The policy.
     */
    public void setPolicy(ValidationPolicy policy) {
        PreCon.notNull(policy);

        _policy = policy;
    }

    @Override
    public boolean isValid(E element) {
        PreCon.notNull(element);

        boolean result = false;

        if (_policy == ValidationPolicy.BLACKLIST)
            result = !_set.contains(element);

        else if (_policy == ValidationPolicy.WHITELIST)
            result = _set.contains(element);

        return result;
    }

    @Override
    protected Set<E> set() {
        return _set;
    }

    public enum ValidationPolicy {
        WHITELIST,
        BLACKLIST
    }
}
