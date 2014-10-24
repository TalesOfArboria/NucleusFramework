package com.jcwhatever.bukkit.generic.storage;

/**
 * A result passed to the method executed by
 * the {@code StorageLoadHandler} when a data node
 * load is complete.
 */
public class StorageLoadResult {

	private boolean _isLoaded = false;
	private IDataNode _dataNode;

    /**
     * Constructor.
     *
     * @param isLoaded     True if the node was successfully loaded.
     * @param loadHandler  The handler.
     */
	StorageLoadResult(boolean isLoaded, StorageLoadHandler loadHandler) {
		_isLoaded = isLoaded;
		_dataNode = loadHandler._dataNode;		
	}

    /**
     * Determine if the data node was successfully loaded.
     */
	public boolean isLoaded() {
		return _isLoaded;
	}

    /**
     * Get the data node.
     */
	public IDataNode getDataNode() {
		return _dataNode;
	}
	
}
