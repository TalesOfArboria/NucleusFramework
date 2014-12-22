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


package com.jcwhatever.bukkit.generic.messaging;

import com.jcwhatever.bukkit.generic.internal.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.utils.PreCon;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Chat list paginator.
 */
public class ChatPaginator {

    @Localizable static final String _HEADER = "----------------------------------------\r{AQUA}{0} {GRAY}(Page {1} of {2})";
    @Localizable static final String _FOOTER = "----------------------------------------";

    private final Plugin _plugin;
    private final IMessenger _msg;
    private final List<Object[]> _printList = new ArrayList<>(50);

    protected int _itemsPerPage = 6;
    protected String _headerFormat;
    protected String _footerFormat;
    protected String _title;

    /**
     * Constructor.
     *
     * <p>
     *     6 items per page, default header and footer.
     * </p>
     *
     * @param plugin  The owning plugin.
     */
    public ChatPaginator(Plugin plugin) {
        PreCon.notNull(plugin);

        _plugin = plugin;
        _msg = MessengerFactory.get(plugin);
    }

    /**
     * Constructor.
     *
     * <p>
     *     No header or footer.
     * </p>
     *
     * @param plugin        The owning plugin.
     * @param itemsPerPage  Number of items to show per page.
     */
    public ChatPaginator(Plugin plugin, int itemsPerPage) {
        PreCon.notNull(plugin);
        PreCon.greaterThanZero(itemsPerPage);

        _plugin = plugin;
        _msg = MessengerFactory.get(plugin);
        _itemsPerPage = itemsPerPage;
    }

    /**
     * Constructor.
     *
     * <p>
     *     6 items per page, default header and footer.
     * </p>
     *
     * @param plugin        The owning plugin.
     * @param itemsPerPage  Number of items to show per page.
     * @param title         The title to insert into the header.
     */
    public ChatPaginator(Plugin plugin, int itemsPerPage, String title) {
        PreCon.notNull(title);

        _plugin = plugin;
        _msg = MessengerFactory.get(plugin);
        _itemsPerPage = itemsPerPage;
        _title = title;
    }

    /**
     * Constructor.
     *
     * <p>
     *     6 items per page.
     * </p>
     * <p>
     *     Header format uses numbers in braces to insert title, current page and total pages:<br>
     *     {0} = title<br>
     *     {1} = current page<br>
     *     {2} = total pages
     * </p>
     *
     * @param plugin        The owning plugin.
     * @param headerFormat  The header format.
     * @param footerFormat  The footer format.
     * @param title         The title to insert into the header.
     */
    public ChatPaginator(Plugin plugin, Object headerFormat, Object footerFormat, String title) {
        this(plugin, 5, headerFormat, footerFormat, title);
    }

    /**
     * Constructor.
     *
     * <p>
     *     Header format uses numbers in braces to insert title, current page and total pages:<br>
     *     {0} = title<br>
     *     {1} = current page<br>
     *     {2} = total pages
     * </p>
     *
     * @param plugin        The owning plugin.
     * @param itemsPerPage  Number of items to show per page.
     * @param headerFormat  The header format.
     * @param footerFormat  The footer format.
     * @param title         The title to insert into the header.
     */
    public ChatPaginator(Plugin plugin, int itemsPerPage, Object headerFormat, Object footerFormat, String title) {
        PreCon.notNull(headerFormat);
        PreCon.notNull(footerFormat);
        PreCon.notNull(title);

        _plugin = plugin;
        _msg = MessengerFactory.get(plugin);
        _itemsPerPage = itemsPerPage;
        _headerFormat = headerFormat.toString();
        _footerFormat = footerFormat.toString();
        _title = title;
    }

    /**
     * Get the owning plugin.
     */
    public Plugin getPlugin() {
        return _plugin;
    }

    /**
     * Get the paginator title.
     */
    @Nullable
    public String getTitle() {
        return _title;
    }

    /**
     * Add an item.
     *
     * <p>
     *     The format of the item is specified when the paginator
     *     is shown to a {@code CommandSender}. The objects provided
     *     as parameters are inserted into the format.
     * </p>
     *
     * @param parameters  The object parameters inserted into the item format.
     */
    public void add(Object...parameters) {
        PreCon.notNull(parameters);
        PreCon.greaterThanZero(parameters.length);

        _printList.add(parameters);
    }

    /**
     * Add an item with a format that overrides the item format used when
     * displaying the paginator.
     *
     * @param format      The format that applies to the item.
     * @param parameters  The object parameters inserted into the item format.
     */
    public void addFormatted(Object format, Object...parameters) {
        PreCon.notNull(format);
        PreCon.notNull(parameters);

        _printList.add(new Object[]{new PreFormattedLine(format.toString(), parameters)});
    }

    /**
     * Add all objects from a collection.
     *
     * <p>
     *     Each item in the collection is 1 item in the paginator.
     *     Multiple objects can be added per item by placing them in
     *     an {@code Object[]}
     * </p>
     *
     * @param collection  The collection to add.
     */
    public void addAll(Collection<?> collection) {
        PreCon.notNull(collection);

        for (Object object : collection) {
            if (object instanceof Object[]) {
                _printList.add((Object[]) object );
            }
            else {
                _printList.add(new Object[] { object });
            }
        }
    }

    /**
     * Show a page of the paginator to a {@code CommandSender}.
     *
     * <p>
     *     The format parameter only applies to items that are not
     *     added with a specific format.
     * </p>
     *
     * @param sender  The command sender to display the page to.
     * @param page    The page to display.
     * @param format  The format that applies to the items on the page.
     */
    public void show(CommandSender sender, int page, Object format) {
        PreCon.notNull(sender);
        PreCon.notNull(format);

        page = page > 0 ? page : 1;

        int totalPages = (int)Math.ceil((double)_printList.size() / _itemsPerPage);

        String header = _headerFormat != null
                ? Lang.get(_plugin, _headerFormat, _title, Math.max(1, page), Math.max(1, totalPages))
                : Lang.get(_HEADER, _title, Math.max(1, page), Math.max(1, totalPages));

        if (!header.isEmpty())
            _msg.tell(sender, header);

        if (page < 1 || page > totalPages) {
            if (page == 1) {
                _msg.tell(sender, "No items to display.");
            }
            else {
                _msg.tell(sender, "Page " + page + " was not found.");
            }
        }
        else {

            int start = page * _itemsPerPage - _itemsPerPage;
            int end = Math.min(start + _itemsPerPage - 1, _printList.size() - 1);

            for (int i = start; i <= end; ++i) {
                Object[] line = _printList.get(i);
                if (line.length == 1 && line[0] instanceof PreFormattedLine) {
                    PreFormattedLine preformatted = (PreFormattedLine) line[0];
                    _msg.tell(sender, preformatted.format, preformatted.parameters);
                } else {
                    _msg.tell(sender, format, line);
                }
            }
        }

        String footer = _footerFormat != null
                ? Lang.get(_plugin, _footerFormat, _title, Math.max(1, page), Math.max(1, totalPages))
                : Lang.get(_FOOTER, _title, Math.max(1, page), Math.max(1, totalPages));

        if (!footer.isEmpty())
            _msg.tell(sender, footer);
    }

    /*
     * Used to store formatting information for a single item.
     */
    private static final class PreFormattedLine {
        public final String format;
        public final Object[] parameters;

        public PreFormattedLine(String format, Object[] parameters) {
            this.format = format;
            this.parameters = parameters;
        }
    }

}
