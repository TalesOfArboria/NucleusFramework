package com.jcwhatever.bukkit.generic.storage;

/**
 * Handles a batch of operations to be
 * performed on a data node.
 */
public abstract class BatchOperation {
	
	public abstract void run(IDataNode config);
	
	public void onFinish() {}
	
}
