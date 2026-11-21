package org.atcraftmc.starlight.data.recording;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import me.gb2022.gluon.service.ApplicationService;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@ApplicationService(id="record")
public class RecordService implements RemovalListener<String, OutputStream> {
    private final Cache<String, OutputStream> streamCache;
    private final Map<Class<?>, Function<?,String>> dataEncoders = new HashMap<>();

    public RecordService() {
        this.streamCache = CacheBuilder.newBuilder()
                                       .expireAfterAccess(Duration.ofSeconds(15))
                                       .removalListener(this)
                                       .build();
    }

    public <I> void registerEncoder(Class<I> type, Function<I,String> encoder) {
        this.dataEncoders.put(type, encoder);
    }

    public String encode(Object o){
        if(this.dataEncoders.containsKey(o.getClass())){
            //noinspection unchecked
            return ((Function<Object,String>)this.dataEncoders.get(o.getClass())).apply(o);
        }

        return o.toString();
    }




    @Override
    public void onRemoval(@NotNull RemovalNotification<String, OutputStream> notification) {
        try {
            if (notification.getValue() != null) {
                notification.getValue().close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(RecordEntry entry, Object[] items) {

    }
}
