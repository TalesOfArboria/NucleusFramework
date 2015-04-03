package com.jcwhatever.nucleus.storage;

import com.jcwhatever.nucleus.providers.storage.DataStorage;

/*
 * 
 */
public class DataStorageUtil {

    public static void setTestMode() {
        DataStorage.setTransientOnly(true);
    }
}
