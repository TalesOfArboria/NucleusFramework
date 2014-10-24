package com.jcwhatever.bukkit.generic.storage;

/**
 * A result passed to the method executed by
 * the {@code StorageSaveHandler} when a data node
 * save is complete.
 */
public class StorageSaveResult {
	
	private boolean _isSaved = false;
	private IDataNode _dataNode;

    /**
     * Constructor.
     *
     * @param isSaved      True if the data node was saved successfully.
     * @param saveHandler  The handler.
     */
	StorageSaveResult(boolean isSaved, StorageSaveHandler saveHandler) {
		_isSaved = isSaved;
		_dataNode = saveHandler._dataNode;		
	}

    /**
     * Determine if the data node was saved successfully.
     */
	public boolean isSaved() {
		return _isSaved;
	}

    /**
     * Get the data node.
     */
	public IDataNode getDataNode() {
		return _dataNode;
	}
	
}
