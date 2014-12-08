/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.jcwhatever.bukkit.generic.views;

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.views.InventoryActionInfoHandler.InventoryActionInfo;
import com.jcwhatever.bukkit.generic.views.InventoryActionInfoHandler.ViewActionOrder;
import com.jcwhatever.bukkit.generic.views.triggers.IViewTrigger;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import javax.annotation.Nullable;

/**
 * Abstract implementation of a view.
 */
public abstract class AbstractView implements IView {

    private static EventListener _eventListener;

    private String _name;
    private String _title;
    protected IDataNode _dataNode;
    private ViewManager _viewManager;

    private boolean _isInitialized;
    private IViewTrigger _trigger;

    /**
     * Initialize the view.
     *
     * @param name         The name of the view.
     * @param viewManager  The view manager responsible for the view.
     * @param dataNode     The data node to save settings to.
     */
    @Override
    public final void init(String name, ViewManager viewManager, @Nullable IDataNode dataNode) {
        PreCon.notNullOrEmpty(name);
        PreCon.notNull(viewManager);

        if (_isInitialized)
            throw new IllegalStateException("Custom inventory view can only be initialized once.");

        _isInitialized = true;

        _name = name;
        _dataNode = dataNode;
        _viewManager = viewManager;

        onInit(name, dataNode, viewManager);

        loadSettings();

        if (_eventListener == null) {
            _eventListener = new EventListener();
            Bukkit.getPluginManager().registerEvents(_eventListener, viewManager.getPlugin());
        }
    }

    @Override
    public final String getName() {
        return _name;
    }

    @Override
    public final String getDefaultTitle() {
        return _title != null ? _title : _name;
    }

    /**
     * Set or remove the default title.
     *
     * @param title  The title to set.
     */
    protected final void setDefaultTitle(@Nullable String title) {
        _title = title;
    }

    @Override
    public final IViewTrigger getViewTrigger() {
        return _trigger;
    }

    @Override
    public final boolean setViewTrigger(@Nullable Class<? extends IViewTrigger> triggerClass) {

        // don't make changes if the new trigger is the same as the current trigger
        if (_trigger != null && triggerClass != null && triggerClass.equals(_trigger.getClass())) {
            return false;
        }

        IDataNode triggerNode = null;

        if (_dataNode != null) {
            triggerNode = _dataNode.getNode("trigger");
            triggerNode.remove();
        }

        // check if removing current trigger.
        if (triggerClass == null) {

            // dispose current trigger
            if (_trigger != null) {
                _trigger.dispose();
            }

            _trigger = null;

            if (triggerNode != null) {
                triggerNode.saveAsync(null);
            }
            return true;
        }

        // instantiate new trigger
        IViewTrigger trigger = instantiateTrigger(triggerClass, triggerNode);
        if (trigger == null)
            return false;

        if (_dataNode != null) {
            _dataNode.set("trigger.class-name", triggerClass.getName());
            _dataNode.saveAsync(null);
        }

        _trigger = trigger;

        return true;
    }

    @Override
    public final ViewManager getViewManager() {
        return _viewManager;
    }

    @Override
    public ViewInstance createInstance(Player p, ViewInstance previous, ViewMeta sessionMeta) {
        return createInstance(p, previous, sessionMeta, null);
    }

    @Override
    public ViewInstance createInstance(
            Player p, ViewInstance previous, ViewMeta sessionMeta, @Nullable ViewMeta instanceMeta) {
        return onCreateInstance(p, previous, sessionMeta, instanceMeta);
    }

    /*
     * load initial settings.
     */
    private void loadSettings() {

        if (_dataNode == null)
            return;

        _title = _dataNode.getString("title", "Menu");

        final String triggerClassName = _dataNode.getString("trigger.class-name");
        if (triggerClassName != null) {

            Class<?> clazz = null;

            try {
                // get the class by name
                clazz = Class.forName(triggerClassName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            if (clazz != null) {

                // make sure the class implements IViewTrigger
                if (IViewTrigger.class.isAssignableFrom(clazz)) {

                    Class<? extends IViewTrigger> triggerClass = clazz.asSubclass(IViewTrigger.class);

                    _trigger = instantiateTrigger(triggerClass, _dataNode.getNode("trigger"));
                }
            }
        }

        onLoadSettings(_dataNode);
    }


    /**
     * Called after the view is initialized.
     *
     * @param name         The name of the view.
     * @param dataNode     The views data node.
     * @param viewManager  The owning view manager.
     */
    protected abstract void onInit(String name, @Nullable IDataNode dataNode, ViewManager viewManager);

    /**
     * Called whenever a view instance needs to be created for a player.
     *
     * @param p             The player to create a view instance for.
     * @param previous      The players previous view instance.
     * @param sessionMeta   The view session meta.
     * @param instanceMeta  The meta for the view instance.
     *
     * @return  A new view instance.
     */
    protected abstract ViewInstance onCreateInstance(
            Player p, @Nullable ViewInstance previous, ViewMeta sessionMeta, @Nullable ViewMeta instanceMeta);

    /**
     * Called whenever the settings are loaded or reloaded.
     *
     * @param dataNode  The views data node.
     */
    protected abstract void onLoadSettings(IDataNode dataNode);

    /*
     *  Create a new instance of a trigger.
     */
    @Nullable
    private IViewTrigger instantiateTrigger(
            Class<? extends IViewTrigger> triggerClass, @Nullable IDataNode dataNode) {

        IViewTrigger trigger;

        try {
            trigger = triggerClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

        trigger.init(this, getViewManager(), dataNode);

        return trigger;
    }

    /**
     * Global Bukkit event listener
     */
    private static class EventListener implements Listener {

        @EventHandler(priority = EventPriority.HIGHEST)
        private void onInventoryClick(final InventoryClickEvent event) {

            HumanEntity entity = event.getWhoClicked();
            if (!(entity instanceof Player))
                return;

            Player p = (Player)entity;

            ViewInstance instance = ViewManager.getCurrent(p);
            if (instance == null)
                return;

            boolean allow = true;

            InventoryActionInfoHandler actionInfoHandler = new InventoryActionInfoHandler(event);

            InventoryActionInfo primaryInfo = actionInfoHandler.getPrimaryInfo();
            InventoryActionInfo secondaryInfo = actionInfoHandler.getSecondaryInfo();

            switch (actionInfoHandler.getPrimaryViewAction()) {
                case ITEMS_PLACED:
                    allow = instance.onItemsPlaced(primaryInfo, ViewActionOrder.PRIMARY);
                    break;
                case ITEMS_PICKUP:
                    allow = instance.onItemsPickup(primaryInfo, ViewActionOrder.PRIMARY);
                    break;
                case ITEMS_DROPPED:
                    allow = instance.onItemsDropped(primaryInfo, ViewActionOrder.PRIMARY);
                    break;
                case LOWER_PLACED:
                    allow = instance.onLowerItemsPlaced(primaryInfo, ViewActionOrder.PRIMARY);
                    break;
                case LOWER_PICKUP:
                    allow = instance.onLowerItemsPickup(primaryInfo, ViewActionOrder.PRIMARY);
                    break;
                default:
                    break;
            }

            switch (actionInfoHandler.getSecondaryViewAction()) {
                case ITEMS_PLACED:
                    allow = allow && instance.onItemsPlaced(secondaryInfo, ViewActionOrder.SECONDARY);
                    break;
                case ITEMS_PICKUP:
                    allow = allow && instance.onItemsPickup(secondaryInfo, ViewActionOrder.SECONDARY);
                    break;
                case ITEMS_DROPPED:
                    allow = allow && instance.onItemsDropped(secondaryInfo, ViewActionOrder.SECONDARY);
                    break;
                case LOWER_PLACED:
                    allow = allow && instance.onLowerItemsPlaced(secondaryInfo, ViewActionOrder.SECONDARY);
                    break;
                case LOWER_PICKUP:
                    allow = allow && instance.onLowerItemsPickup(secondaryInfo, ViewActionOrder.SECONDARY);
                    break;
                default:
                    break;
            }

            // cancel event
            if (!allow) {
                event.setCancelled(true);
            }

        }


        // Cleanup data related to closed inventory
        @EventHandler(priority = EventPriority.HIGHEST)
        private void onInventoryClose(InventoryCloseEvent event) {
            HumanEntity entity = event.getPlayer();
            if (!(entity instanceof Player))
                return;

            Player p = (Player)entity;

            final ViewInstance instance = ViewManager.getCurrent(p);
            if (instance == null)
                return;

            // determine if closing for next view
            if (instance.isClosingForNext()) {
                instance.resetIsClosingForNext();
                instance.onClose(ViewCloseReason.OPEN_NEXT);
                return;
            }

            // determine if closing to refresh
            if (instance.isRefreshing()) {
                instance.resetIsRefreshing();
                instance.onClose(ViewCloseReason.REFRESH);

                instance.getView().getViewManager().show(
                        instance.getPlayer(),
                        instance.getView(),
                        instance.getSourceBlock(),
                        instance.getInstanceMeta());
                return;
            }

            final ViewInstance previous = instance.getPrev();

            if (previous == null) {
                ViewManager.clearCurrent(p);
                instance.onClose(ViewCloseReason.GOING_BACK);
            }
            else if (!previous.isClosingForNext()) {

                instance.onClose(ViewCloseReason.GOING_BACK);

                Bukkit.getScheduler().runTaskLater(instance.getView().getViewManager().getPlugin(), new Runnable() {

                    @Override
                    public void run() {

                        previous.showAsPrev(instance);
                        previous.setNext(null);
                    }

                }, 2);

            }
            else {
                previous.resetIsClosingForNext();
                instance.onClose(ViewCloseReason.OPEN_NEXT);
            }

        }


    }

}

