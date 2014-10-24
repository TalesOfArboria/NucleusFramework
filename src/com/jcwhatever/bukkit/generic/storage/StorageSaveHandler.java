package com.jcwhatever.bukkit.generic.storage;

/**
 * Handles tasks to be performed after
 * a data node is saved asynchronously.
 */
public abstract class StorageSaveHandler {
	
	IDataNode _dataNode;
	
	public abstract void onFinish(StorageSaveResult result);
}
