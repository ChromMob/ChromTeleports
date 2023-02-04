package me.chrommob.chromteleports.warps;

import com.github.puregero.multilib.MultiLib;
import me.chrommob.chromteleports.ChromTeleports;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WarpStorage {
    private Map<String, Location> warps = ChromTeleports.instance().getWarpDatabaseLoader().loadAll();
    private Map<String, WarpRequest> requests = new ConcurrentHashMap<>();
    public WarpStorage() {
        MultiLib.onString(ChromTeleports.instance(), "warp:create", name -> {
            if (warps.containsKey(name)) {
                return;
            }
            Bukkit.getScheduler().runTaskAsynchronously(ChromTeleports.instance(), () -> {
                Location location = ChromTeleports.instance().getWarpDatabaseLoader().loadByName(name);
                if (location == null) {
                    ChromTeleports.instance().getLogger().info("Failed to load warp " + name);
                }
                warps.put(name, location);
            });
        });
        MultiLib.onString(ChromTeleports.instance(), "warp:delete", name -> {
            warps.remove(name);
        });
    }

    public void addWarp(String name, Location location) {
        warps.put(name, location);
        ChromTeleports.instance().getWarpDatabaseLoader().saveWarp(name, location);
    }

    public void removeWarp(String name) {
        warps.remove(name);
        ChromTeleports.instance().getWarpDatabaseLoader().deleteWarp(name);
    }

    public Location getWarpLocation(String warpName) {
        return warps.getOrDefault(warpName, null);
    }

    public Set<String> getWarps() {
        return warps.keySet();
    }

    public void moved(Player player) {
        if (requests.containsKey(player.getName())) {
            requests.get(player.getName()).moved();
        }
    }

    public void addWarpRequest(String name, Location location) {
        for (WarpRequest request : requests.values()) {
            if (request.isTeleported()) {
                requests.remove(request.getName());
            }
        }
        requests.put(name, new WarpRequest(name, location));
    }
}
