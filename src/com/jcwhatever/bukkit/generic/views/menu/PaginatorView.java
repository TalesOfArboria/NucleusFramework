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

package com.jcwhatever.bukkit.generic.views.menu;

import com.jcwhatever.bukkit.generic.mixins.IPaginator;
import com.jcwhatever.bukkit.generic.views.IViewFactory;
import com.jcwhatever.bukkit.generic.views.IViewSession;
import com.jcwhatever.bukkit.generic.views.data.ViewArgumentKey;
import com.jcwhatever.bukkit.generic.views.data.ViewArguments;
import com.jcwhatever.bukkit.generic.views.data.ViewCloseReason;
import com.jcwhatever.bukkit.generic.views.data.ViewOpenReason;
import com.jcwhatever.bukkit.generic.views.data.ViewResultKey;
import com.jcwhatever.bukkit.generic.views.data.ViewResults;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

/*
 * 
 */
public class PaginatorView extends MenuView {

    // ARGUMENT KEYS
    public static final ViewArgumentKey<IPaginator>
            PAGINATOR = new ViewArgumentKey<>(IPaginator.class);

    public static final ViewArgumentKey<IViewFactory>
            NEXT_VIEW = new ViewArgumentKey<>(IViewFactory.class);

    // RESULT KEYS
    public static final ViewResultKey<Integer>
            SELECTED_PAGE = new ViewResultKey<>(Integer.class);


    private final IPaginator _paginator;
    private final IViewFactory _nextView;
    private ViewResults _results;

    public PaginatorView(@Nullable String title, IViewSession session, IViewFactory factory, ViewArguments arguments) {
        super(title, session, factory, arguments);

        _paginator = arguments.get(PAGINATOR);
        if (_paginator == null) {
            throw new RuntimeException("PAGINATOR argument is required for PaginatorView.");
        }

        _nextView = arguments.get(NEXT_VIEW);
        if (_nextView == null) {
            throw new RuntimeException("NEXT_VIEW argument is required for PaginatorView.");
        }
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
                SELECTED_PAGE.getResult(menuItem.getSlot())
        );

        getViewSession().next(_nextView, _results);
    }

    protected MenuItem getPageItem(int slot, int page) {

        MenuItem item = new MenuItem(slot, this);
        item.setTitle("Page " + page);
        item.setDescription("Click to view page " + page + '.');
        item.setItemStack(new ItemStack(Material.PAPER));

        return item;
    }

    @Nullable
    @Override
    public ViewResults getResults() {
        return _results;
    }
}
