package me.chrommob.chromteleports.delays;

import me.chrommob.chromteleports.delays.dataholders.CommandType;
import me.chrommob.chromteleports.delays.dataholders.DelayData;

import java.util.HashMap;

public class DelayGetter {
    private final HashMap<String, DelayData> delayData = new HashMap<>();
    public long getLastUsed(String name, CommandType type) {
        return delayData.getOrDefault(name, new DelayData()).getLastUsed(type);
    }

    public void setLastUsed(String name, CommandType type, long currentTime) {
        delayData.putIfAbsent(name, new DelayData());
        delayData.get(name).setLastUsed(type, currentTime);
    }
}
