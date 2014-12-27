/*
 * This file is part of NucleusFramework for Bukkit, licensed under the MIT License (MIT).
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

package com.jcwhatever.nucleus.views.menu;

import com.jcwhatever.nucleus.utils.items.ItemStackComparer;
import com.jcwhatever.nucleus.mixins.IPaginator;
import com.jcwhatever.nucleus.views.IViewFactory;
import com.jcwhatever.nucleus.views.ViewSession;
import com.jcwhatever.nucleus.views.data.ViewArgumentKey;
import com.jcwhatever.nucleus.views.data.ViewArguments;
import com.jcwhatever.nucleus.views.data.ViewCloseReason;
import com.jcwhatever.nucleus.views.data.ViewOpenReason;
import com.jcwhatever.nucleus.views.data.ViewResultKey;
import com.jcwhatever.nucleus.views.data.ViewResults;
import com.jcwhatever.nucleus.views.data.ViewResults.ViewResult;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

/**
 * A basic paginator view.
 *
 * <p>The paginator view provides a selection interface for an {@link com.jcwhatever.nucleus.mixins.IPaginator}
 * instance. The paginator will also open up the next view once the player has selected a
 * page.</p>
 *
 * <p>The next view can then get the {@link com.jcwhatever.nucleus.mixins.IPaginator} instance from the paginator view
 * (by finding the previous view from the view session) and determine which page in the
 * paginator to use by retrieving the {@code PaginatorView.SELECTED_PAGE} meta value from
 * the view results.</p>
 */
public class PaginatorView extends MenuView {

    // ARGUMENT KEYS

    /**
     * Argument meta key for the {@code IPaginator} instance that the
     * paginator view will represent and pass on to the next view.
     */
    public static final ViewArgumentKey<IPaginator>
            PAGINATOR = new ViewArgumentKey<>(IPaginator.class);

    /**
     * Argument meta key for the {@code IViewFactory} instance to will
     * create the next view instance after the player selects a page.
     */
    public static final ViewArgumentKey<IViewFactory>
            NEXT_VIEW = new ViewArgumentKey<>(IViewFactory.class);

    // RESULT KEYS

    /**
     * Result meta key to indicate the page the player selected. It is the
     * responsibility of the next view instance opened to read the value
     * from the paginator views results.
     */
    public static final ViewResultKey<Integer>
            SELECTED_PAGE = new ViewResultKey<>(Integer.class);

    private final IPaginator _paginator;
    private final IViewFactory _nextView;
    private ViewResults _results;

    /**
     * Constructor.
     *
     * @param title      The view title.
     * @param session    The player view session.
     * @param factory    The view factory that created the paginator.
     * @param arguments  The view arguments.
     * @param comparer   The item stack comparer.
     */
    public PaginatorView(@Nullable String title, ViewSession session,
                         IViewFactory factory, ViewArguments arguments,
                         @Nullable ItemStackComparer comparer) {
        super(title, session, factory, arguments, comparer);

        _paginator = arguments.get(PAGINATOR);
        if (_paginator == null) {
            throw new RuntimeException("PAGINATOR argument is required for PaginatorView.");
        }

        _nextView = arguments.get(NEXT_VIEW);
        if (_nextView == null) {
            throw new RuntimeException("NEXT_VIEW argument is required for PaginatorView.");
        }
    }

    @Nullable
    @Override
    public ViewResults getResults() {
        return _results;
    }

    @Override
    protected void onClose(ViewCloseReason reason) {
        // do nothing
    }

    @Override
    protected void onShow(ViewOpenReason reason) {
        // do nothing
    }

    @Override
    protected List<MenuItem> createMenuItems() {

        int itemsPerPage = _paginator.getItemsPerPage();
        if (itemsPerPage > MenuView.MAX_SLOTS)
            throw new RuntimeException("Items per page cannot be larger than " + MenuView.MAX_SLOTS);

        int totalPages = _paginator.getTotalPages();
        if (totalPages > MenuView.MAX_SLOTS)
            throw new RuntimeException("Total pages cannot be larger than " + MenuView.MAX_SLOTS);

        List<MenuItem> menuItems = new ArrayList<>(totalPages);

        for (int i=0; i < totalPages; i++) {
            menuItems.add(getPageItem(i, i));
        }

        return menuItems;
    }

    @Override
    protected void onItemSelect(MenuItem menuItem) {

        _results = new ViewResults(getArguments(),
                new ViewResult(PAGINATOR, _paginator),
                SELECTED_PAGE.getResult(menuItem.getSlot())
        );

        getViewSession().next(_nextView, _results);
    }

    /**
     * Create a menu item for the slot and page.
     *
     * @param slot  The slot the menu item will be in.
     * @param page  The page the menu item represents.
     */
    protected MenuItem getPageItem(int slot, int page) {

        MenuItem item = new MenuItem(slot);
        item.setTitle("Page " + page);
        item.setDescription("Click to view page " + page + '.');
        item.setItemStack(new ItemStack(Material.PAPER));

        return item;
    }
}
