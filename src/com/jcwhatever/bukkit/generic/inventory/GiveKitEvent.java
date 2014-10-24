package com.jcwhatever.bukkit.generic.inventory;

import com.jcwhatever.bukkit.generic.extended.ArmorType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class GiveKitEvent extends Event {
    private static final HandlerList _handlers = new HandlerList();
    private ItemStack _helmet;
    private ItemStack _chestplate;
    private ItemStack _leggings;
    private ItemStack _boots;
    private List<ItemStack> _items;
    private boolean _cancelled = false;
    private Player _p;
    private Kit _kit;


    public GiveKitEvent(Player p, Kit kit) {
        _p = p;
        _kit = kit;
        
        if (kit.getHelmet() != null) {
            _helmet = kit.getHelmet().clone();
        }
        if (kit.getChestplate() != null) {
            _chestplate = kit.getChestplate().clone();
        }
        if (kit.getLeggings() != null) {
            _leggings = kit.getLeggings().clone();
        }
        if (kit.getBoots() != null) {
            _boots = kit.getBoots().clone();
        }
        
        _items = new ArrayList<ItemStack>(6 * 9);
                
        for (ItemStack item : kit.getItems()) {
            _items.add(item.clone());
        }
    }

    public UUID getPlayerId() {
        return _p.getUniqueId();
    }
    
    public Player getPlayer() {
    	return _p;
    }

    public String getName() {
        return _kit.getName();
    }

    public String getSearchName() {
        return _kit.getSearchName();
    }

    public ItemStack getHelmet() {
        return _helmet;
    }

    public ItemStack getChestplate() {
        return _chestplate;
    }

    public ItemStack getLeggings() {
        return _leggings;
    }

    public ItemStack getBoots() {
        return _boots;
    }

    public List<ItemStack> getItems() {
        return _items;
    }

    public ItemStack[] getItemArray() {
        ItemStack[] array = new ItemStack[_items.size()];
        for (int i = 0; i < _items.size(); ++i) {
            array[i] = _items.get(i);
        }
        return array;
    }

    public ItemStack[] getArmorArray() {
        ArrayList<ItemStack> armorList = new ArrayList<ItemStack>(5);
        if (_helmet != null) {
            armorList.add(_helmet);
        }
        if (_chestplate != null) {
            armorList.add(_chestplate);
        }
        if (_leggings != null) {
            armorList.add(_leggings);
        }
        if (_boots != null) {
            armorList.add(_boots);
        }
        ItemStack[] array = new ItemStack[armorList.size()];
        for (int i = 0; i < armorList.size(); ++i) {
            array[i] = armorList.get(i);
        }
        return array;
    }

    public void setHelmet(ItemStack helmet) {
        _helmet = helmet;
    }

    public void setChestplate(ItemStack chestplate) {
        _chestplate = chestplate;
    }

    public void setLeggings(ItemStack leggings) {
        _leggings = leggings;
    }

    public void setBoots(ItemStack boots) {
        _boots = boots;
    }

    public void addItem(ItemStack item) {
        if (ArmorType.getType(item) == ArmorType.NOT_ARMOR) {
            _items.add(item);
        } else {
            addArmor(item);
        }
    }

    public void addItems(Collection<ItemStack> items) {
        if (items == null) {
            return;
        }
        for (ItemStack item : items) {
            addItem(item);
        }
    }

    public void addArmor(Collection<ItemStack> armor) {
        if (armor == null) {
            return;
        }
        for (ItemStack item : armor) {
            addArmor(item);
        }
    }

    public void addArmor(ItemStack item) {
        switch (ArmorType.getType(item)) {
            case HELMET: {
                setHelmet(item);
                break;
            }
            case CHESTPLATE: {
                setChestplate(item);
                break;
            }
            case LEGGINGS: {
                setLeggings(item);
                break;
            }
            case BOOTS: {
                setBoots(item);
            }
        }
    }

    public boolean removeItem(ItemStack item) {
        if (item == null) {
            return false;
        }
        switch (ArmorType.getType(item)) {
            case HELMET: {
                setHelmet(null);
                break;
            }
            case CHESTPLATE: {
                setChestplate(null);
                break;
            }
            case LEGGINGS: {
                setLeggings(null);
                break;
            }
            case BOOTS: {
                setBoots(null);
                break;
            }
            default: {
                int count = _items.size();
                for (int i = 0; i < count; i++) {
                    
                    if (_items.get(i).getType() != item.getType()) 
                    	continue;
                    _items.remove(i);
                    i--;
                    count--;
                }
            }
        }
        return true;
    }

    public boolean getCancelled() {
        return _cancelled;
    }

    public void setCancelled(boolean cancel) {
        _cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return _handlers;
    }

    public static HandlerList getHandlerList() {
        return _handlers;
    }
   
}

