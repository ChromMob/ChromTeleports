package me.chrommob.chromteleports.warps;

import me.chrommob.chromteleports.ChromTeleports;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WarpRequest {
    private final String sender;
    private final Location location;
    private boolean moved = false;
    private boolean teleported = false;
    public WarpRequest(String sender, Location location) {
        this.sender = sender;
        this.location = location;
        Bukkit.getScheduler().runTaskLater(ChromTeleports.instance(), () -> {
            Player player = Bukkit.getPlayer(sender);
            if (player == null) {
                return;
            }
            if (moved) {
                player.sendMessage(Component.text("Pohnul jsi se, teleportace zrusena.", NamedTextColor.RED));
                return;
            } else if (teleported) {
                return;
            }
            player.sendMessage(Component.text("Teleportuji...", NamedTextColor.GREEN));
            Bukkit.getScheduler().runTask(ChromTeleports.instance(), () -> player.teleport(location));
            teleported = true;
        }, 20 * 3);
    }

    public void moved() {
        moved = true;
    }

    public boolean isTeleported() {
        return teleported;
    }

    public String getName() {
        return sender;
    }
}
