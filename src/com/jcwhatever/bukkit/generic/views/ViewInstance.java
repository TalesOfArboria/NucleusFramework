/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.views.InventoryActionInfoHandler.InventoryActionInfo;
import com.jcwhatever.bukkit.generic.views.InventoryActionInfoHandler.ViewActionOrder;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

import javax.annotation.Nullable;

/**
 *
 * Represents an instance of a View created for a specific player
 */
public abstract class ViewInstance {

    private String _instanceTitle;
    private String _prefix;

    private final IView _view;
    private final ViewInstance _prev;
    private final Player _player;
    private final ViewMeta _instanceMeta;
    private final ViewMeta _sessionMeta;

    private Block _sourceBlock;
    private ViewInstance _next;
    private InventoryView _inventoryView;

    private boolean _isClosingForNext = false;
    private boolean _isRefreshing = false;


    /**
     * Constructor.
     *
     * @param view          The view that spawned this instance.
     * @param previous      The previous instance the player was viewing, if any.
     * @param p             The player the instance is for.
     * @param sessionMeta   The meta for the session.
     * @param instanceMeta  The meta for this specific instance.
     */
    public ViewInstance(IView view, @Nullable ViewInstance previous, Player p,
                        ViewMeta sessionMeta, @Nullable ViewMeta instanceMeta) {

        PreCon.notNull(view);
        PreCon.notNull(p);
        PreCon.notNull(sessionMeta);

        _view = view;
        _prev = previous;
        _player = p;
        _instanceMeta = instanceMeta;
        _sessionMeta = sessionMeta;

        if (_prev != null) {
            _prev.setNext(this);
        }

        _prefix = "";
        int depth = getMenuDepth();
        for (int i = 0; i < depth; i++) {
            _prefix += ">";
        }

        setTitle(getView().getDefaultTitle());

    }

    /**
     * Get view title prefix.
     */
    public String getPrefix () {

        return _prefix;
    }

    /**
     * Get the view title. Includes prefix.
     */
    public String getTitle () {

        return _instanceTitle;
    }

    /**
     * Set the title of the view. Prefix is automatically appended.
     *
     * @param title  The title
     */
    public void setTitle (String title) {

        _instanceTitle = _prefix + ' ' + title;

        if (_instanceTitle.length() > 32) {
            _instanceTitle = _instanceTitle.substring(0, (31 - 3)) + "...";
        }
    }

    /**
     * Close and re-open the view
     */
    public void refresh() {
        _isRefreshing = true;
        getPlayer().closeInventory();
    }

    /**
     * Get the number of views viewed before this view.
     */
    public int getMenuDepth () {

        if (_prev == null)
            return 0;

        int depth = 1;

        ViewInstance prev = _prev;
        while ((prev = prev._prev) != null) {
            depth++;
        }

        return depth;
    }

    /**
     * Get the player this view is for.
     */
    public final Player getPlayer () {

        return _player;
    }

    /**
     * Get the view that spawned this instance.
     */
    public final IView getView () {

        return _view;
    }


    /**
     * Get the view instances Bukkit InventoryView
     */
    public final InventoryView getInventoryView () {

        return _inventoryView;
    }


    /**
     * Get the meta data associated with this specific instance.
     */
    public final ViewMeta getInstanceMeta () {

        return _instanceMeta;
    }


    /**
     * Get the meta data for the players view session.
     */
    public ViewMeta getSessionMeta () {

        return _sessionMeta;
    }


    /**
     * Get the source block used to start the session, if any.
     */
    @Nullable
    public final Block getSourceBlock () {

        return _sourceBlock;
    }


    /**
     * Get the previous view instance, if any.
     */
    @Nullable
    public final ViewInstance getPrev () {

        return _prev;
    }


    /**
     * Get the next view instance, if any.
     */
    @Nullable
    public final ViewInstance getNext () {

        return _next;
    }

    // Internal. set the next view instance.
    void setNext (ViewInstance instance) {

        _next = instance;
    }

    /**
     * Get the view instance the player is currently looking at.
     */
    public final ViewInstance getLast () {

        ViewInstance next = this;
        ViewInstance last = this;

        while ((next = next._next) != null) {
            last = next;
        }

        return last;
    }

    /**
     * Close the current instance and show the previous instance.
     */
    public final void showPrev () {

        showPrev(null);
    }

    /**
     * Close the current instance and show the previous instance.
     *
     * @param meta  The meta to use
     */
    public final void showPrev (@Nullable ViewMeta meta) {

        // check for prev and that this is the current player view
        if (_prev == null || _next != null)
            return;

        _player.closeInventory();

        _prev.show(_prev.getSourceBlock(), meta != null
                ? meta
                : _prev.getInstanceMeta());
    }

    /**
     * Show this instance using current meta.
     */
    public final ViewInstance show () {

        return show(null, null);
    }


    /**
     * Show this instance using new instanceMeta
     *
     * @param instanceMeta  The instance meta object
     */
    public final ViewInstance show (ViewMeta instanceMeta) {

        return show(null, instanceMeta);
    }


    /**
     * Show this instance and set the source block. Use the current instance meta.
     *
     * @param sourceBlock  The block used to start the session, if any.
     */
    public final ViewInstance show (Block sourceBlock) {

        return show(sourceBlock, null);
    }

    /**
     * Show this instance and set the source block and instance meta.
     *
     * @param sourceBlock   The block used to start the session, if any.
     * @param instanceMeta  Meta data for this instance.
     */
    public final ViewInstance show (@Nullable final Block sourceBlock, final ViewMeta instanceMeta) {

        Bukkit.getScheduler().scheduleSyncDelayedTask(getView().getViewManager().getPlugin(), new Runnable() {

            @Override
            public void run () {

                if (_prev != null) {
                    _prev.setClosingForNext();
                    getPlayer().closeInventory();
                }

                // do not overwrite current source block unless
                // a new one is provided.
                if (sourceBlock != null) {
                    _sourceBlock = sourceBlock;
                }

                Bukkit.getScheduler().runTaskLater(getView().getViewManager().getPlugin(), new Runnable() {

                    @Override
                    public void run () {

                        _inventoryView = onShow(instanceMeta != null
                                ? instanceMeta
                                : _instanceMeta);
                    }

                }, 2);

            }

        }, 3);

        return this;
    }


    // Internal. Show this instance as a previous instance of the current
    // instance.
    void showAsPrev (final ViewInstance closingInstance) {

        Bukkit.getScheduler().runTaskLater(getView().getViewManager().getPlugin(), new Runnable() {

            @Override
            public void run () {

                _inventoryView = onShowAsPrev(_instanceMeta, closingInstance.getResult());
            }

        }, 2);
    }

    // Internal. Determine if this instance is closing
    // because it needs to be refreshed/re-opened.
    boolean isRefreshing() {

        return _isRefreshing;
    }

    // Internal. Return the IsRefreshing flag to false.
    void resetIsRefreshing() {

        _isRefreshing = false;
    }

    // Internal. Determine if this instance is closing because
    // the next instance is opening.
    boolean isClosingForNext () {

        return _isClosingForNext;
    }

    // Internal. Set the ClosingForNext flag to true.
    void setClosingForNext () {

        _isClosingForNext = true;
    }

    // Internal. Return the ClosingForNext flag to false.
    void resetIsClosingForNext () {

        _isClosingForNext = false;
    }

    /**
     * Get the optional result of the view instance.
     */
    @Nullable
    public abstract ViewResult getResult ();

    /**
     * Called when the view needs to be shown. Is not called if the view
     * is being shown as a previous view.
     *
     * <p>
     *     It is left to the implementation to actually construct
     *     and show the view to the player.
     * </p>
     *
     * @param instanceMeta  The meta data for the instance.
     *
     * @return Null if the view could not be shown.
     */
    @Nullable
    protected abstract InventoryView onShow (ViewMeta instanceMeta);

    /**
     * Called when the view is shown as a previous view.
     * If there is no action to be taken when shown as previous,
     * be sure to forward this method to onShow(instanceMeta)
     *
     * <p>
     *     It is left to the implementation to actually show
     *     and construct the view to the player.
     * </p>
     *
     * @param instanceMeta  The meta data for the instance.
     * @param result        The result meta data from the closing view instance.
     *
     * @return Null if the view could not be shown.
     */
    @Nullable
    protected abstract InventoryView onShowAsPrev (ViewMeta instanceMeta, ViewResult result);

    /**
     * Called when the view is closed.
     *
     * @param reason  The reason the view was closed.
     */
    protected abstract void onClose (ViewCloseReason reason);

    /**
     * Called when an item is placed in an inventory slot.
     *
     * @param actionInfo  Information pertaining to the event.
     *
     * @return True to allow the item to be placed. False to cancel event.
     */
    protected abstract boolean onItemsPlaced (InventoryActionInfo actionInfo, ViewActionOrder actionOrder);


    /**
     * Called when an item is picked up from an inventory slot.
     *
     * @param actionInfo  Information pertaining to the event.
     *
     * @return True to allow the item to be picked up. False to cancel event.
     */
    protected abstract boolean onItemsPickup (InventoryActionInfo actionInfo, ViewActionOrder actionOrder);


    /**
     * Called when an item is dropped outside the inventory view.
     *
     * @param actionInfo  Information pertaining to the event.
     *
     * @return True to allow the item to be dropped. False to cancel event.
     */
    protected abstract boolean onItemsDropped (InventoryActionInfo actionInfo, ViewActionOrder actionOrder);

    /**
     * Called when an item is placed into the lower inventory area.
     *
     * @param actionInfo  Information pertaining to the event
     *
     * @return True to allow the click action. False to cancel event.
     */
    protected abstract boolean onLowerItemsPlaced(InventoryActionInfo actionInfo, ViewActionOrder actionOrder);

    /**
     * Called when an item is picked up from the lower inventory area.
     *
     * @param actionInfo  Information pertaining to the event
     *
     * @return True to allow the click action. False to cancel event.
     */
    protected abstract boolean onLowerItemsPickup(InventoryActionInfo actionInfo, ViewActionOrder actionOrder);

}
