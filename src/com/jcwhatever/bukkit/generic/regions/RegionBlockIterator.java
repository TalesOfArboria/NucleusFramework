package com.jcwhatever.bukkit.generic.regions;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Iterator;

/**
 * Iterates through all blocks in a region.
 */
public class RegionBlockIterator implements Iterator<Block> {

	private int _currentY;
	private int _currentX;
	private int _currentZ;
	private Block _current;

	private Region _region;

    /**
     * Constructor.
     *
     * @param region  The region to iterate.
     */
    public RegionBlockIterator (Region region) {
        _region = region;

        _currentY = region.getYStart();
        _currentX = region.getXStart();
        _currentZ = region.getZStart();
    }

    /**
     * Constructor.
     *
     * @param region  The region to iterate.
     */
    public RegionBlockIterator (ReadOnlyRegion region) {
        _region = region.getHandle();

        _currentY = region.getYStart();
        _currentX = region.getXStart();
        _currentZ = region.getZStart();
    }

    /**
     * Determine if there is a next block.
     */
	@Override
	public boolean hasNext() {
		if (_region.getWorld() == null)
			return false;
		
		if (_currentY > _region.getYEnd())
			return false;
		
		return true;
	}

    /**
     * Get the next block.
     */
	@Override
	public Block next() {
		
		if (!hasNext())
			return null;
		
		_current = _region.getWorld().getBlockAt(_currentX, _currentY, _currentZ);
		
		_currentZ++;
		
		if (_currentZ > _region.getZEnd()) {
			_currentZ = _region.getZStart();
			_currentX++;
			
			if (_currentX > _region.getXEnd()) {
				_currentX = _region.getXStart();
				
				_currentY++;
			}
		}
		
		return _current;
		
	}

    /**
     * Remove the current block. Sets the block material
     * to {@code AIR}.
     */
	@Override
	public void remove() {
		if (_current != null)
			_current.setType(Material.AIR);		
	}
}
