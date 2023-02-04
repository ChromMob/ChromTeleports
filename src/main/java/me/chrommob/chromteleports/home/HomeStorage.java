package me.chrommob.chromteleports.home;

import com.github.puregero.multilib.MultiLib;
import me.chrommob.chromteleports.ChromTeleports;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class HomeStorage {
    private final Map<UUID, Set<HomeData>> homes = ChromTeleports.instance().getHomeDatabaseLoader().loadAllHomes();
    public HomeStorage() {
        MultiLib.onString(ChromTeleports.instance(), "home:create", s -> {
            String[] split = s.split(" ");
            UUID player = UUID.fromString(split[0]);
            if (homes.containsKey(player)) {
                for (HomeData home : homes.get(player)) {
                    if (home.getName().equalsIgnoreCase(split[1])) {
                        return;
                    }
                }
            }
            homes.putIfAbsent(player, new HashSet<>());
            homes.get(player).add(new HomeData(split[1], player));
        });
        MultiLib.onString(ChromTeleports.instance(), "home:delete", s -> {
            String[] split = s.split(" ");
            UUID player = UUID.fromString(split[0]);
            if (!homes.containsKey(player)) {
                return;
            }
            for (HomeData home : homes.get(player)) {
                if (home.getName().equalsIgnoreCase(split[1])) {
                    homes.get(player).remove(home);
                    break;
                }
            }
        });
    }

    public boolean addHome(Player player, String name, boolean overwrite) {
        homes.putIfAbsent(player.getUniqueId(), new HashSet<>());
        if (homes.get(player.getUniqueId()).stream().anyMatch(home -> home.getName().equalsIgnoreCase(name))) {
            if (overwrite) {
                removeHome(player, name);
                homes.get(player.getUniqueId()).add(new HomeData(name, player.getLocation(), player.getUniqueId(), true));
                return true;
            } else {
                return false;
            }
        } else {
            homes.get(player.getUniqueId()).add(new HomeData(name, player.getLocation(), player.getUniqueId(), true));
            return true;
        }
    }

    public void removeHome(Player player, String name) {
        if (!homes.containsKey(player.getUniqueId())) {
            return;
        }
        boolean found = false;
        for (HomeData home : homes.get(player.getUniqueId())) {
            if (home.getName().equalsIgnoreCase(name)) {
                homes.get(player.getUniqueId()).remove(home);
                found = true;
                break;
            }
        }
        if (!found) {
            return;
        }
        ChromTeleports.instance().getHomeDatabaseLoader().deleteHomeData(name, player.getUniqueId());
        MultiLib.notify("home:delete", player.getUniqueId() + " " + name);
    }

    public Set<String> getHomes(Player sender) {
        if (!homes.containsKey(sender.getUniqueId())) {
            return new HashSet<>();
        }
        return homes.get(sender.getUniqueId()).stream().map(HomeData::getName).collect(Collectors.toSet());
    }

    public HomeData getHomeData(Player sender, String name) {
        if (!homes.containsKey(sender.getUniqueId())) {
            return null;
        }
        for (HomeData home : homes.get(sender.getUniqueId())) {
            if (home.getName().equalsIgnoreCase(name)) {
                return home;
            }
        }
        return null;
    }

    public void moved(Player moved) {
        if (!homes.containsKey(moved.getUniqueId())) {
            return;
        }
        for (HomeData home : homes.get(moved.getUniqueId())) {
            home.moved();
        }
    }
}
