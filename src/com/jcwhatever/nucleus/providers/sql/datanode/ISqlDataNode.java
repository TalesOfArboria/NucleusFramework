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

package com.jcwhatever.nucleus.providers.sql.datanode;

import com.jcwhatever.nucleus.storage.IDataNode;

/**
 * Sql based data node.
 *
 * <p>The Sql based data node has nodes that are pre assigned to table row values.
 * It will accept new nodes but only pre assigned nodes are used to update the
 * database the data node was created for.</p>
 *
 * <p>The {@link #load()} and {@link #loadAsync()} methods are not used for any sql
 * data purposes. Data is pre loaded before creating the data node.</p>
 *
 * <p>The {@link #save()} and {@link #saveSync()} methods will cause the values of
 * any nodes assigned to a row column which are dirty/changed, to be updated in the
 * database.</p>
 */
public interface ISqlDataNode extends IDataNode {
}
