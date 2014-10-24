package com.jcwhatever.bukkit.generic.storage;

/**
 * Handles tasks to be performed after a
 * data node is loaded asynchronously.
 */
public abstract class StorageLoadHandler {
	
	IDataNode _dataNode;
	
	public abstract void onFinish(StorageLoadResult result);
}
