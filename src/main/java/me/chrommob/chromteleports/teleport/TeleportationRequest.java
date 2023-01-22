package me.chrommob.chromteleports.teleport;

import me.chrommob.chromteleports.ChromTeleports;
import me.chrommob.chromteleports.delays.dataholders.CommandType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TeleportationRequest {
    private boolean moved = false;
    private boolean accepted = false;
    private final String sender;
    private final String receiver;

    public TeleportationRequest(String sender, String receiver) {
        this.sender = sender;
        this.receiver = receiver;
        Bukkit.getScheduler().runTaskLater(ChromTeleports.instance(), () -> {
            if (!ChromTeleports.instance().getRequestsStorage().hasRequest(sender)) {
                return;
            }
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
            if (receiver != null) {
                receiver.sendMessage("Hrac jiz neni online");
            }
            return;
        }
        sender.sendMessage("Teleportuji te, 3 sekundy se nehybej.");
        moved = false;
        Bukkit.getScheduler().runTaskLater(ChromTeleports.instance(), () -> {
            if (!moved) {
                sender.teleport(receiver);
                sender.sendMessage("Teleportuji...");
                receiver.sendMessage("Teleportuji...");
                ChromTeleports.instance().getDelayGetter().setLastUsed(getSender(), CommandType.TPA, System.currentTimeMillis());
            } else {
                sender.sendMessage("Pohnul jsi se teleport zrusen.");
                receiver.sendMessage(getSender() + " se pohnul rusim teleportaci.");
            }
            ChromTeleports.instance().getRequestsStorage().removeRequest(getSender());
        }, 20*3);
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void moved() {
        moved = true;
    }
}
