package com.jcwhatever.bukkit.generic.collections;

import com.jcwhatever.bukkit.generic.utils.Rand;

import java.util.ArrayList;
import java.util.Collection;

public class WeightedList<T> extends ArrayList<Weighted<T>> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6415721364402179704L;
	
	private int _sumOfWeight = 0;
	
	public int getSumOfWeight() {
		return _sumOfWeight;
	}
	
	@SuppressWarnings("unchecked")
	public T getRandom() {
		Weighted<T> result = (Weighted<T>) Rand.weighted(this);
		if (result != null)
			return result.getItem();
		return null;
	}
	
	
		
	public WeightedList<T> add(T item, int weight) {
		Weighted<T> weighted = new Weighted<T>(item, weight);
		add(weighted);
		return this;
	}
	
	@Override
	public boolean add(Weighted<T> weighted) {
		if (super.add(weighted)) {
			_sumOfWeight += weighted.getWeight();
			return true;
		}
		return false;
		
	}
	
	@Override
	public void add(int index, Weighted<T> weighted) {
		super.add(index, weighted);
		_sumOfWeight += weighted.getWeight();
	}
	
	@Override
	public boolean addAll(Collection<? extends Weighted<T>> items) {
		if (super.addAll(items)) {
			for (Weighted<T> item : items) {
				_sumOfWeight += item.getWeight();
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends Weighted<T>> items) {
		if (super.addAll(index, items)) {
			for (Weighted<T> item : items) {
				_sumOfWeight += item.getWeight();
			}
			return true;
		}
		return false;
	}
	
	@Override
	public Weighted<T> remove(int index) {
		Weighted<T> removed = super.remove(index);
		if (removed != null)
			_sumOfWeight -= removed.getWeight();
		return removed;
	}
	
	@Override
	public boolean remove(Object obj) {
		if (super.remove(obj)) {
			if (obj instanceof Weighted<?>) {
				Weighted<?> item = (Weighted<?>)obj;
				_sumOfWeight -= item.getWeight();
			}
			return true;
		}
		return false;
	}
	
	public boolean removeAll(T[] items) {
		for (T item : items) {
			remove(item);
		}
		return true;
	}
	
	
	@Override
	public boolean removeAll(Collection<?> items) {
		return false;		
	}
	
	

}
