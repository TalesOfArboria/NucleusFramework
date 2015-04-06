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

package com.jcwhatever.nucleus.providers;

import com.jcwhatever.nucleus.mixins.INamedInsensitive;
import com.jcwhatever.nucleus.providers.bankitems.IBankItemsProvider;
import com.jcwhatever.nucleus.providers.economy.IEconomyProvider;
import com.jcwhatever.nucleus.providers.friends.IFriendsProvider;
import com.jcwhatever.nucleus.providers.jail.IJailProvider;
import com.jcwhatever.nucleus.providers.kits.IKitProvider;
import com.jcwhatever.nucleus.providers.npc.INpcProvider;
import com.jcwhatever.nucleus.providers.permissions.IPermissionsProvider;
import com.jcwhatever.nucleus.providers.playerlookup.IPlayerLookupProvider;
import com.jcwhatever.nucleus.providers.regionselect.IRegionSelectProvider;
import com.jcwhatever.nucleus.providers.storage.IStorageProvider;
import com.jcwhatever.nucleus.utils.PreCon;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Service provider types.
 */
public enum ProviderType implements INamedInsensitive {

    BANK_ITEMS    ("BankItems",    IBankItemsProvider.class),
    ECONOMY       ("Economy",      IEconomyProvider.class),
    FRIENDS       ("Friends",      IFriendsProvider.class),
    JAIL          ("Jail",         IJailProvider.class),
    KITS          ("Kits",         IKitProvider.class),
    NPC           ("NPC",          INpcProvider.class),
    PERMISSIONS   ("Permissions",  IPermissionsProvider.class),
    PLAYER_LOOKUP ("PlayerLookup", IPlayerLookupProvider.class),
    REGION_SELECT ("RegionSelect", IRegionSelectProvider.class),
    STORAGE       ("Storage",      IStorageProvider.class);

    private static Map<Class<? extends IProvider>, ProviderType> _typeMap;
    private static Map<String, ProviderType> _nameMap;

    /**
     * Get the {@link ProviderType} from the specified service provider API interface class.
     *
     * @param apiType  The api type.
     *
     * @return  The {@link ProviderType} or null if not found.
     */
    @Nullable
    public static ProviderType from(Class<? extends IProvider> apiType) {
        PreCon.notNull(apiType);

        buildMaps();

        return _typeMap.get(apiType);
    }

    /**
     * Get the {@link ProviderType} from the specified server provider API type name.
     *
     * @param name  The api name.
     *
     * @return  The {@link ProviderType} or null if not found.
     */
    @Nullable
    public static ProviderType from(String name) {
        PreCon.notNull(name);

        buildMaps();

        return _nameMap.get(name.toLowerCase());
    }

    private final String _name;
    private final String _searchName;
    private final Class<? extends IProvider> _apiType;

    ProviderType(String name, Class<? extends IProvider> apiType) {

        _name = name;
        _searchName = name.toLowerCase();
        _apiType = apiType;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getSearchName() {
        return _searchName;
    }

    public Class<? extends IProvider> getApiType() {
        return _apiType;
    }

    private static void buildMaps() {
        if (_typeMap != null)
            return;

        _typeMap = new HashMap<>(17);
        _nameMap = new HashMap<>(17);

        for (ProviderType type : values()) {
            _typeMap.put(type.getApiType(), type);
            _nameMap.put(type.getSearchName(), type);
        }
    }
}
