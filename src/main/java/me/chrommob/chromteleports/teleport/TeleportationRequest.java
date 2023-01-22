package me.chrommob.chromteleports.teleport;

import me.chrommob.chromteleports.ChromTeleports;
import me.chrommob.chromteleports.delays.dataholders.CommandType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.naming.Name;

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
                    player.sendMessage(Component.text("Tvuj pozadavek na teleportaci expiroval!").color(NamedTextColor.RED));
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
                receiver.sendMessage(Component.text("Hrac jiz neni online.").color(NamedTextColor.RED));
            }
            return;
        }
        sender.sendMessage(Component.text("Teleportuji te, 3 sekundy se ").color(NamedTextColor.WHITE).append(Component.text("nehybej!").color(NamedTextColor.RED)));
        moved = false;
        Bukkit.getScheduler().runTaskLater(ChromTeleports.instance(), () -> {
            if (!moved) {
                sender.teleport(receiver);
                Component teleport = Component.text("Teleportuji...").color(NamedTextColor.DARK_GREEN);
                sender.sendMessage(teleport);
                receiver.sendMessage(teleport);
                ChromTeleports.instance().getDelayGetter().setLastUsed(getSender(), CommandType.TPA, System.currentTimeMillis());
            } else {
                sender.sendMessage(Component.text("Pohnul jsi se teleport zrusen.").color(NamedTextColor.RED));
                receiver.sendMessage(Component.text(getSender() + " se ").color(NamedTextColor.WHITE).append(Component.text("pohnul").color(NamedTextColor.RED)).append(Component.text(" rusim teleportaci.").color(NamedTextColor.WHITE)));
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
