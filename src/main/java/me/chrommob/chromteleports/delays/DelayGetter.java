package me.chrommob.chromteleports.delays;

import me.chrommob.chromteleports.delays.dataholders.CommandType;
import me.chrommob.chromteleports.delays.dataholders.DelayData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DelayGetter {
    private final Map<String, DelayData> delayData = new ConcurrentHashMap<>();
    public long getLastUsed(String name, CommandType type) {
        return delayData.getOrDefault(name, new DelayData()).getLastUsed(type);
    }

    public void setLastUsed(String name, CommandType type, long currentTime) {
        delayData.putIfAbsent(name, new DelayData());
        delayData.get(name).setLastUsed(type, currentTime);
    }
}
