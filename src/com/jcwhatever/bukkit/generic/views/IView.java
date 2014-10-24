package com.jcwhatever.bukkit.generic.views;

import com.jcwhatever.bukkit.generic.mixins.IDisposable;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.views.triggers.IViewTrigger;
import com.jcwhatever.bukkit.generic.views.triggers.TriggerType;

/**
 * Represents a type of player view
 */
public interface IView extends IDisposable {
	
	/**
	 * Called only once. Used internally after instantiating view.
     *
	 * @param name         The name of the view.
	 * @param dataNode     The data node to save settings to.
	 * @param viewManager  The view manager responsible for the view.
	 */
	void init(String name, IDataNode dataNode, ViewManager viewManager);
	
	/**
	 * Get the name of the view
	 */
	String getName();
	
	/**
	 * Get the default title used for a view if one is not set in the instance.
	 */
	String getDefaultTitle();
	
	/**
	 * Get the views View Manager.
	 */
	ViewManager getViewManager();
	
	/**
	 * Get the Bukkit inventory type of the inventory view.
	 */
	InventoryType getInventoryType();
	
	/**
	 * Get the view type
	 */
	ViewType getViewType();
	
	/**
	 * Get the optional trigger used to open the view.
	 */
	IViewTrigger getViewTrigger();
	
	/**
	 * Set the optional view trigger
     *
	 * @param type  The trigger type
	 */
	void setViewTrigger(TriggerType type);
	
	/**
	 * Create a new instance of the view to display to a player.
     *
	 * @param p            The player the instance is for.
	 * @param previous     The previous instance the player was viewing.
	 * @param sessionMeta  The meta used for the session.
	 */
	ViewInstance createInstance(Player p, ViewInstance previous, ViewMeta sessionMeta);
	
	/**
	 * Create a new instance of the view to display to a player.
     *
	 * @param p             The player the instance is for.
	 * @param previous      The previous instance the player was viewing.
	 * @param sessionMeta   The meta used for the session.
	 * @param instanceMeta  The meta that applies to the new instance.
	 */
	ViewInstance createInstance(Player p, ViewInstance previous, ViewMeta sessionMeta, ViewMeta instanceMeta);
	
	/**
	 * Called internally when the View is removed.
	 */
    @Override
	void dispose();


}
