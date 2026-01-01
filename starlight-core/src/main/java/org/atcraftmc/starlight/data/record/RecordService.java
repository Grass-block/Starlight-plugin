package org.atcraftmc.starlight.data.record;

import me.gb2022.modular.service.ApplicationService;
import me.gb2022.modular.service.Service;
import me.gb2022.modular.service.ServiceHolder;
import me.gb2022.modular.service.ServiceInject;
import org.atcraftmc.starlight.data.record.registry.RecordData;

import java.util.HashMap;

@ApplicationService(id = "record", impl = RecordService.ServiceImplementation.class)
public interface RecordService extends Service {
    @ServiceInject
    ServiceHolder<RecordService> INSTANCE = new ServiceHolder<>();

    static RecordService create() {
        return new ServiceImplementation();
    }

    static RecordEntry create(String id, String[] recordFormat) {
        RecordEntry entry = INSTANCE.get().createEntry(id, recordFormat);
        entry.open();
        return entry;
    }


    static void record(RecordData data) {
        //todo
    }


    RecordEntry createEntry(String id, String[] recordFormat);

    void save();

    final class ServiceImplementation implements RecordService {
        private final HashMap<String, RecordEntry> entries = new HashMap<>();

        @Override
        public void disable() {
            for (RecordEntry entry : this.entries.values()) {
                entry.close();
            }
        }

        @Override
        public RecordEntry createEntry(String id, String[] recordFormat) {
            if (this.entries.containsKey(id)) {
                return this.entries.get(id);
            }
            RecordEntry entry = new SimpleRecordEntry(id, recordFormat);
            this.entries.put(id, entry);
            return entry;
        }

        @Override
        public void save() {
            for (RecordEntry entry : this.entries.values()) {
                entry.close();
            }
        }
    }
}
