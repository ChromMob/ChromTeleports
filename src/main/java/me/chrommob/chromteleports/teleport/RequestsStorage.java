package me.chrommob.chromteleports.teleport;

import java.util.HashMap;

public class RequestsStorage {
    private final HashMap<String, TeleportationRequest> requests = new HashMap<>();

    public TeleportationRequest getRequest(String name) {
        return requests.get(name);
    }

    public void addRequest(String name, String target) {
        requests.put(name, new TeleportationRequest(name, target));
    }

    public void removeRequest(String name) {
        requests.remove(name);
    }

    public boolean hasRequest(String name) {
        return requests.containsKey(name);
    }
}
