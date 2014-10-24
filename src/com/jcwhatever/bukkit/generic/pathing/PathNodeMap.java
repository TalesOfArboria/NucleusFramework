package com.jcwhatever.bukkit.generic.pathing;

import java.util.ArrayList;
import java.util.List;

/**
 * Map specifically designed for internal use.
 * 
 * @author JC The Pants
 *
 */
class PathNodeMap {
	
	private NodeEntry[][] _nodes;
	private int _size = 0;
	private NodeEntry _first;
	private NodeEntry _last;
	private List<PathNode> _cachedValues;
	
	public PathNodeMap(int size) {
		_nodes = new NodeEntry[size][];
	}
	
	public PathNodeMap() {
		_nodes = new NodeEntry[200][];
	}
	
	public void put(PathNode node) {
		putMap(node);
	}
	
	
	public PathNode remove(String id) {
		
		return removeMap(id);
	}
	
	
	public PathNode get(String id) {
		return getMap(id);
	}
	
	
	
	public boolean contains(String id) {
		return get(id) != null;
	}
	
	public void clear() {
		if (_size == 0)
			return;
		
		for (int i=0; i < _nodes.length; i++) {
			_nodes[i] = null;
		}
		
		NodeEntry current = _first;
		
		while (current != null) {
			current.prev = null;
			NodeEntry next = current.next;
			current.next = null;
			current = next;
		}
		
		_cachedValues = null;
		_first = null;
		_last = null;
		_size = 0;
	}
	
	public int size() {
		return _size;
	}
	
	
	private int getIndex(String key) {
		return Math.abs(key.hashCode()) % _nodes.length;
	}
	
	
	private NodeEntry[] expandArray(NodeEntry[] array) {
		int size = array.length * 2;
		NodeEntry[] newArray = new NodeEntry[size];
		for (int i=0; i < array.length; i++) {
			newArray[i] = array[i];
		}
		return newArray;
	}
	
	private boolean putMap(PathNode node) {
		int index = getIndex(node.id);
		
		NodeEntry[] array = _nodes[index];
		
		if (array == null) {
			array = new NodeEntry[5];
			_nodes[index] = array;
		}
		
		for (int i=0; i < array.length; i++) {
			boolean hasKey = false;
			NodeEntry entry = array[i];
			
			if (entry == null || (hasKey = entry.id.equals(node.id))) {
				if (hasKey) {
				    
				    if (entry != null)
				        entry.node = node;
				    
					return true;
				}
				else {
					entry = new NodeEntry(node);
					array[i] = entry;
					addNodeEntry(entry);
					_size++;					
				}
				_cachedValues = null;
				return hasKey;
			}
		}
		
		_nodes[index] = expandArray(array);
		return putMap(node);
	}
	
	
	private void addNodeEntry(NodeEntry entry) {
		if (_first == null) {
			_first = entry;
			_last = entry;
			return; 
	    }
		
		entry.prev = _last;
		_last.next = entry;
		_last = entry;
	}
	
	
		
	private PathNode removeMap(String id) {
		int index = getIndex(id);
		
		NodeEntry[] array = _nodes[index];
		
		if (array == null)
			return null;
		
		for (int i=0; i < array.length; i++) {
			NodeEntry entry = array[i];
			if (entry != null && entry.id.equals(id)) {
				 array[i] = null;
				 removeNodeEntry(entry);
				 _cachedValues = null;
				_size--;
				return entry.node;
			}
		}
		
		return null;
	}
	
	private void removeNodeEntry(NodeEntry entry) {
		
		NodeEntry prev = entry.prev;
		NodeEntry next = entry.next;
		
		if (prev != null) {
			prev.next = next;
		}
		else {
			_first = next;
		}
		
		if (next != null) {
			next.prev  = prev;
		}
		else {
			_last = prev;
		}
		
		entry.prev = null;
		entry.next = null;
	}
	
	private PathNode getMap(String id) {
		int index = getIndex(id);
		
		NodeEntry[] array = _nodes[index];
		
		if (array == null)
			return null;
		
		for (int i=0; i < array.length; i++) {
			NodeEntry entry = array[i];
			if (entry != null && entry.id.equals(id)) {
				 return entry.node;
			}
		}
		
		return null;
	}
	
	public List<PathNode> values() {
		
		if (_cachedValues != null)
			return new ArrayList<PathNode>(_cachedValues);
		
		
		List<PathNode> results = new ArrayList<PathNode>();
		
		if (_first == null)
			return results;
		
		NodeEntry current = _first;
		
		while (current != null) {
			results.add(current.node);
			current = current.next;
		}
		
		_cachedValues = new ArrayList<PathNode>(results);
		
		
		return results;
	}
	
	
	class NodeEntry {
		public final String id;
		
		public PathNode node;
		public NodeEntry prev;
		public NodeEntry next;
		
		public NodeEntry (PathNode node) {
			this.node = node;
			this.id = node.id;
		}
	}
	
	
}
