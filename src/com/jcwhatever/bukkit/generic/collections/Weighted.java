package com.jcwhatever.bukkit.generic.collections;

public class Weighted<T> {
	
	private T _item;
	private int _weight;
	
	public Weighted(T item, int weight) {
		_item = item;
		_weight = weight;
	}
	
	public T getItem() {
		return _item;
	}
	
	public int getWeight() {
		return _weight;
	}
	
}
