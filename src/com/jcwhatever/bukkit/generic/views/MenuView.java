package com.jcwhatever.bukkit.generic.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;

public class MenuView extends AbstractMenuView {

	protected Inventory _menu;
	
	protected Map<String, MenuItem> _itemMap = new HashMap<String, MenuItem>();
	protected Map<Integer, MenuItem> _slotMap = new HashMap<Integer, MenuItem>();


	@Override
	protected void onInit() {
		//do nothing
	}
	
	
	public MenuItem getMenuItem(String name) {
		PreCon.notNullOrEmpty(name);

		name = name.toLowerCase();

		return _itemMap.get(name);
	}

	public List<MenuItem> getMenuItems() {
		return new ArrayList<MenuItem>(_itemMap.values());
	}


	public boolean addMenuItem(String name, ItemStack item, String title, String description) {
		PreCon.notNullOrEmpty(name);
		PreCon.notNull(item);

		name = name.toLowerCase();

		MenuItem menuItem = _itemMap.get(name);
		if (menuItem != null)
			return false;

		IDataNode node = null;

		if (_dataNode != null) {
			node = _dataNode.getNode("items." + name);
			node.set("item", item);
			node.set("title", title);
			node.set("description", description);
			node.saveAsync(null);
		}

		menuItem = new MenuItem(-1, name, this, node);

		_itemMap.put(name, menuItem);

		buildInventory();

		return true;
	}

	public boolean removeMenuItem(String name) {
		PreCon.notNullOrEmpty(name);

		name = name.toLowerCase();

		MenuItem menuItem = _itemMap.remove(name);
		if (menuItem == null)
			return false;

		if (_dataNode != null) {
			_dataNode.remove("items." + name);
			_dataNode.saveAsync(null);
		}

		buildInventory();

		return true;
	}

	@Override
	protected void onLoadSettings(IDataNode menuNode) {

		Set<String> itemNames = menuNode.getSubNodeNames("items");
		if (itemNames != null && !itemNames.isEmpty()) {

			for (String itemName : itemNames) {
				IDataNode itemNode = menuNode.getNode("items." + itemName);
				MenuItem item = new MenuItem(-1, itemName, this, itemNode);
				_itemMap.put(itemName.toLowerCase(), item);
			}
		}

		buildInventory();
	}

	protected void buildInventory() {

		List<MenuItem> menuItems = new ArrayList<MenuItem>(_itemMap.values());		

		double itemSize = menuItems.size();
		int rows = (int)Math.ceil(itemSize / 9);

		int slots = rows * 9;

		_menu = Bukkit.createInventory(null, slots, getDefaultTitle());

		int size = Math.min(menuItems.size(), slots);


		_slotMap.clear();
		for (int i=0; i < size; i++) {
			menuItems.get(i).setSlot(i);
			_menu.setItem(i, menuItems.get(i).getItemStack());
			_slotMap.put(i, menuItems.get(i));
		}
	}

	@Override
	protected ViewInstance onCreateInstance(Player p, ViewInstance previous, ViewMeta sessionMeta, ViewMeta instanceMeta) {
		MenuInstance menuInstance = new MenuInstance(this, previous, p, sessionMeta, instanceMeta);
		return menuInstance;
	}

}
