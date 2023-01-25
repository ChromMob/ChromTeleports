package me.chrommob.chromteleports.tpa;

import com.github.puregero.multilib.MultiLib;
import me.chrommob.chromteleports.ChromTeleports;
import me.chrommob.chromteleports.home.HomeData;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RequestsStorage {
    private final Map<String, TeleportationRequest> requests = new HashMap<>();
    public RequestsStorage() {
        MultiLib.onString(ChromTeleports.instance(), "tpa:accept", s -> {
            String[] split = s.split(" ");
            if (split.length == 2) {
                if (!requests.containsKey(split[0])) {
                    return;
                }
                if (!requests.get(split[0]).getReceiver().equalsIgnoreCase(split[1])) {
                    return;
                }
                requests.get(split[0]).accept();
            }
        });
    }

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
