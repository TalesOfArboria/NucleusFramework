package com.jcwhatever.bukkit.generic.views;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.jcwhatever.bukkit.generic.GenericsLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import com.jcwhatever.bukkit.generic.events.bukkit.AnvilItemRenameEvent;
import com.jcwhatever.bukkit.generic.items.ItemFilterManager;
import com.jcwhatever.bukkit.generic.items.ItemStackHelper;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.views.InventoryActionInfoHandler.InventoryActionInfo;
import com.jcwhatever.bukkit.generic.views.InventoryActionInfoHandler.ViewActionOrder;

public class AnvilView extends AbstractView {
	
	private static EventListener _eventListener;
	
	private ItemFilterManager _filterManager;

	
	
	@Override
	protected void onInit(String name, IDataNode dataNode, ViewManager viewManager) {
		
		_filterManager = new ItemFilterManager(viewManager.getPlugin(), dataNode.getNode("item-filter"));
		
		if (_eventListener == null) {
			_eventListener = new EventListener();
			Bukkit.getPluginManager().registerEvents(_eventListener, GenericsLib.getPlugin());
		}
	}
	
	@Override
	public InventoryType getInventoryType() {
		return InventoryType.ANVIL;
	}
	
	@Override
	public ViewType getViewType() {
		return ViewType.ANVIL;
	}

	@Override
	public void dispose() {
		// do nothing
	}
	
	@Override
	protected void onLoadSettings(IDataNode dataNode) {
		// do nothing
	}
	
	
	public ItemFilterManager getFilterManager() {
		return _filterManager;
	}
	
	private static class EventListener implements Listener {
		
		@EventHandler(priority = EventPriority.HIGHEST)
		private void onAnvilItemRepair(AnvilItemRenameEvent event) {
			Player p = event.getPlayer();
			
			ViewInstance current = ViewManager.getCurrent(p);

			if (current instanceof AnvilInstance) {
				
				AnvilView view = (AnvilView)current.getView();
				ItemStack result = event.getRenamedItem();
				
				if (!view.getFilterManager().isValidItem(result)) {
					InventoryView invView = current.getInventoryView();
					if (invView != null) {
						ItemStack stack = result.clone();
						ItemStackHelper.setLore(stack, ChatColor.RED + "Not repairable here.");
						invView.setItem(0, stack);
					}
				}
			}
		}
	}

	@Override
	protected ViewInstance onCreateInstance(Player p, ViewInstance previous, ViewMeta sessionMeta, ViewMeta meta) {
		AnvilInstance instance = new AnvilInstance(this, previous, p, sessionMeta, meta);
		return instance;
	}
	
	
	
	public class AnvilInstance extends ViewInstance {

		public AnvilInstance(IView view, ViewInstance previous, Player p, ViewMeta sessionMeta, ViewMeta initialMeta) {
			super(view, previous, p, sessionMeta, initialMeta);
		}

		@Override
		protected InventoryView onShow(ViewMeta meta) {
			
			if (getSourceBlock() == null)
				return null;
			
			Location loc = getSourceBlock().getLocation();
			try {
				
				Player p = getPlayer();

				Method getHandle = p.getClass().getDeclaredMethod("getHandle");

				Object entityHuman = getHandle.invoke(p);

				Method openAnvil = entityHuman.getClass().getDeclaredMethod("openAnvil", int.class, int.class, int.class);

				openAnvil.invoke(entityHuman, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

			}
            catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
            catch (SecurityException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
			
			return null;
		}
		
		@Override
		public ViewResult getResult() {
			return null;
		}

		@Override
		protected InventoryView onShowAsPrev(ViewMeta instanceMeta, ViewResult result) {
			return onShow(instanceMeta);
		}


		@Override
		protected void onClose(ViewCloseReason reason) {
			// do nothing
		}

		@Override
		protected boolean onItemsPlaced(InventoryActionInfo actionInfo, ViewActionOrder actionOrder) {
			return true;
		}

		@Override
		protected boolean onItemsPickup(InventoryActionInfo actionInfo, ViewActionOrder actionOrder) {
			return true;
		}

		@Override
		protected boolean onItemsDropped(InventoryActionInfo actionInfo, ViewActionOrder actionOrder) {
			return true;
		}

        @Override
        protected boolean onLowerItemsPlaced (InventoryActionInfo actionInfo, ViewActionOrder actionOrder) {
            return true;
        }

        @Override
        protected boolean onLowerItemsPickup (InventoryActionInfo actionInfo, ViewActionOrder actionOrder) {
            return true;
        }
				
	}

	
	
}
