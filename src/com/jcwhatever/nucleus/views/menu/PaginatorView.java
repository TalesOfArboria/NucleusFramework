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

import com.jcwhatever.nucleus.mixins.IPaginator;
import com.jcwhatever.nucleus.mixins.IPaginator.PageStartIndex;
import com.jcwhatever.nucleus.utils.MetaKey;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.items.ItemStackMatcher;
import com.jcwhatever.nucleus.views.View;
import com.jcwhatever.nucleus.views.ViewCloseReason;
import com.jcwhatever.nucleus.views.ViewOpenReason;
import com.jcwhatever.nucleus.views.ViewSession;

import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

/**
 * A basic paginator view.
 *
 * <p>The paginator view provides a selection interface for an
 * {@link com.jcwhatever.nucleus.mixins.IPaginator} instance. When a page
 * is selected the view is closed and the result can be retrieved using the
 * {@link #getSelectedPage} method.</p>
 */
public class PaginatorView extends MenuView {

    private static final MetaKey<Integer>
            SELECTED_PAGE = new MetaKey<>(Integer.class);


    /**
     * Show the paginator view if the specified paginator has more than 1 page.
     * Otherwise show the next page.
     *
     * <p>If the paginator view is shown, selecting a page will cause the next view
     * to open.</p>
     *
     * @param viewSession  The view session.
     * @param nextView     The next view to show.
     * @param paginator    The paginator.
     * @param matcher      The {@link ItemStackMatcher} to use.
     */
    public static void paginateNext(ViewSession viewSession,
                                    View nextView,
                                    IPaginator paginator, @Nullable ItemStackMatcher matcher) {

        if (paginator.getTotalPages() > 1) {

            viewSession.next(new PaginatorView(nextView.getPlugin(), paginator, nextView, matcher));

        }
        else {
            viewSession.next(nextView);
        }
    }

    private final IPaginator _paginator;
    private final View _nextView;

    private int _selectedPage = 1;

    /**
     * Constructor.
     *
     * @param comparer   The item stack comparer.
     */
    public PaginatorView(Plugin plugin, IPaginator paginator,
                         @Nullable ItemStackMatcher comparer) {
        this(plugin, paginator, null, comparer);
    }

    /**
     * Constructor.
     *
     * @param comparer   The item stack comparer.
     */
    public PaginatorView(Plugin plugin, IPaginator paginator,
                         @Nullable View nextView,
                         @Nullable ItemStackMatcher comparer) {

        super(plugin, comparer);

        PreCon.notNull(paginator);

        _paginator = paginator;
        _nextView = nextView;
    }

    /**
     * Get the page selected.
     */
    public int getSelectedPage() {
        return _selectedPage;
    }

    @Override
    public String getTitle() {
        return "Select page";
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

            int page = i;
            if (_paginator.getPageStartIndex() == PageStartIndex.ONE)
                page++;

            menuItems.add(getPageItem(i, page));
        }

        return menuItems;
    }

    @Override
    protected void onItemSelect(MenuItem menuItem) {

        Integer selectedPage = menuItem.getMeta(SELECTED_PAGE);
        if (selectedPage == null)
            throw new AssertionError();

        _selectedPage = selectedPage;

        if (_nextView == null) {
            getViewSession().previous();
        }
        else {
            getViewSession().next(_nextView);
        }
    }

    /**
     * Create a menu item for the slot and page.
     *
     * @param slot  The slot the menu item will be in.
     * @param page  The page the menu item represents.
     */
    protected MenuItem getPageItem(int slot, int page) {
        return new MenuItemBuilder(Material.PAPER)
                .title("Page" + page)
                .description("Click to view page " + page + '.')
                .meta(SELECTED_PAGE, page)
                .build(slot);
    }
}
