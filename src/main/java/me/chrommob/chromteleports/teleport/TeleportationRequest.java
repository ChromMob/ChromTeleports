package me.chrommob.chromteleports.teleport;

import me.chrommob.chromteleports.ChromTeleports;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TeleportationRequest {
    private boolean accepted = false;
    private final String sender;
    private final String receiver;

    public TeleportationRequest(String sender, String receiver) {
        this.sender = sender;
        this.receiver = receiver;
        Bukkit.getScheduler().runTaskLater(ChromTeleports.instance(), () -> {
            if (!accepted) {
                Player player = Bukkit.getPlayer(sender);
                if (player != null) {
                    player.sendMessage("Tvuj pozadavek na teleportaci expiroval!");
                }
            }
        }, 20 * 60);
    }

    public void accept() {
        accepted = true;
        Player sender = Bukkit.getPlayer(this.sender);
        Player receiver = Bukkit.getPlayer(this.receiver);
        if (sender == null || receiver == null) {
            if (sender != null) {
                sender.sendMessage("Hrac jiz neni online");
            }
            return;
        }
        Bukkit.getScheduler().runTaskLater(ChromTeleports.instance(), () -> {
            if (!moved)
                sender.teleport(receiver);
        }, 20*3);
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }
}
