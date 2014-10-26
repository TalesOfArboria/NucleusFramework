/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.regions;

import com.jcwhatever.bukkit.generic.performance.queued.QueueResult.Future;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import org.bukkit.Chunk;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An abstract implementation of a restorable region
 * with multiple named saved snapshots.
 *
 * <p>The primary snapshot is the snapshot with an empty name.</p>
 */
public abstract class MultiSnapshotRegion extends RestorableRegion {

	private String _currentSnapshot = "";

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     */
	protected MultiSnapshotRegion(Plugin plugin) {
		super(plugin);
	}

    /**
     * Constructor.
     *
     * @param plugin  The owning plugin.
     * @param name    The name of the region.
     */
	public MultiSnapshotRegion(Plugin plugin, String name) {
		super(plugin, name);
	}

    /**
     * Get the current snapshot state of the region.
     *
     * <p>This is only accurate after an initial snapshot restore.
     * When the region is first loaded, the current known snapshot defaults to the primary,
     * which is an empty string.</p>
     */
    public String getCurrentSnapshot() {
        return _currentSnapshot;
    }

    /**
     * Get the names of stored snapshots.
     *
     * <p>Snapshot names are retrieved by file name.</p>
     *
     * @throws IOException
     */
	public Set<String> getSnapshotNames() throws IOException {

        File folder = getRegionDataFolder();

		File[] files = folder.listFiles();
        Set<String> names = new HashSet<String>(files.length);
		
		for (File file : files) {
			
			String[] comp = TextUtils.PATTERN_DOT.split(file.getName());
			
			if (comp.length != 8)
				continue;
			
			names.add(comp[6]);
		}
		
		return names;
	}

    /**
     * Determine if the specified snapshot can be restored.
     *
     * @param snapshotName  The name of the snapshot.
     */
	@Override
	public boolean canRestore(String snapshotName) {
		return super.canRestore(snapshotName);
	}

    /**
     * Restore primary snapshot.
     *
     * @param buildMethod  The method used to restore.
     *
     * @throws IOException
     */
	@Override
	public Future restoreData(BuildMethod buildMethod) throws IOException {

		return super.restoreData(buildMethod).onComplete(new Runnable() {

			@Override
			public void run() {
				_currentSnapshot = "";				
			}
			
		});
	}

    /**
     * Restore the specified snapshot.
     *
     * @param buildMethod   The method used to restore.
     * @param snapshotName  The name of the snapshot.
     *
     * @throws IOException
     */
	@Override
	public Future restoreData(BuildMethod buildMethod, final String snapshotName) throws IOException {
		return super.restoreData(buildMethod, snapshotName).onComplete(new Runnable() {

			@Override
			public void run() {
				_currentSnapshot = snapshotName;				
			}
			
		});
	}

    /**
     * Save the regions current state to the specified snapshot.
     *
     * @param snapshotName  The name of the snapshot.
     *
     * @throws IOException
     */
	@Override
	public Future saveData(String snapshotName) throws IOException {
		return super.saveData(snapshotName);
	}

    /**
     * Delete the specified snapshots data.
     *
     * @param snapshotName  The name of the snapshot.
     *
     * @throws IOException
     */
	public void deleteData(String snapshotName) throws IOException {
		List<Chunk> chunks = this.getChunks();

		if (chunks.size() == 0) {
			return;
		}

		for (Chunk chunk : chunks) {
			getChunkFile(chunk, snapshotName, true); // deletes file
		}
	}

}
