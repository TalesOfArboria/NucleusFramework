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

package com.jcwhatever.nucleus.providers.economy;

import com.jcwhatever.nucleus.utils.PreCon;
import javax.annotation.Nullable;

/**
 * Thrown when a transaction fails.
 */
public class TransactionFailException extends Exception {

    private final IAccount _account;
    private final String _message;

    /**
     * Constructor.
     *
     * @param account  The account the transaction was processing when it failed.
     * @param message  The failure message.
     */
    public TransactionFailException (@Nullable IAccount account, String message) {
        PreCon.notNull(message);

        _account = account;
        _message = message;
    }

    /**
     * Get the account where the transaction failure occurred.
     */
    @Nullable
    public IAccount getFailedAccount() {
        return _account;
    }

    /**
     * Get the transaction failure message.
     */
    @Override
    public String getMessage() {
        return _message;
    }
}
