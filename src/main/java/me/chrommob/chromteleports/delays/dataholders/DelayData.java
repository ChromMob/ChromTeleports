package me.chrommob.chromteleports.delays.dataholders;

import java.util.HashMap;

public class DelayData {
    private final HashMap<CommandType, Long> lastUsed = new HashMap<>();
    public long getLastUsed(CommandType type) {
        return lastUsed.getOrDefault(type, 0L);
    }

    public void setLastUsed(CommandType type, long lastUsed) {
        this.lastUsed.put(type, lastUsed);
    }
}
