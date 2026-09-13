package org.atcraftmc.starlight.core.data;

import me.gb2022.gluon.service.Service;
import org.atcraftmc.starlight.data.storage.DataEntry;
import org.atcraftmc.starlight.data.storage.StorageContext;

public interface IDataService extends Service, StorageContext {
    DataEntry getData(String player);

    void saveData(String player);

    int getEntryCount();
}
