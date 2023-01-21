package me.chrommob.chromteleports.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import me.chrommob.chromteleports.ChromTeleports;
import me.chrommob.chromteleports.delays.dataholders.CommandType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class Tpa {
    @CommandAlias("tpa")
    @Default
    @SuppressWarnings("unused")
    @CommandCompletion("@players")
    public void onTpa(CommandSender sender, String targetString) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to use this command!");
            return;
        }
        boolean targetOnline = false;
        Player target = ChromTeleports.instance().getServer().getPlayer(targetString);
        if (target != null) {
            targetOnline = true;
        }
        if (!targetOnline) {
            sender.sendMessage("Hrac " + targetString + " neni online!");
            return;
        }
        CommandType type = CommandType.TPA;
        long currentTime = System.currentTimeMillis();
        long lastUsed = ChromTeleports.instance().getDelayGetter().getLastUsed(sender.getName(), type);
        boolean canUse = false;
        long delay = 0;
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            if (info.getPermission().startsWith("chromteleports.delay.")) {
                String[] split = info.getPermission().split("\\.");
                if (split.length == 3) {
                    try {
                        delay = Long.parseLong(split[2]) * 1000;
                        if (currentTime - lastUsed >= delay) {
                            player.sendMessage("Poslal jsi pozadavek na teleportaci hraci " + targetString + "!");
                            player.sendMessage("Hrac " + targetString + " ma " + 60 + " sekund na prijmuti pozadavku!");
                            canUse = true;
                            break;
                        } else {
                            player.sendMessage("Musis jeste pockat " + Math.round(((lastUsed + (delay/1000.0)) - currentTime) * 1000) + " sekund pred tim, nez muzes znovu pouzit tento prikaz!");
                            canUse = false;
                            break;
                        }
                    } catch (NumberFormatException ignored) {
                        player.sendMessage("Nastala chyba pri zpracovani pozadavku! Otevri si ticket na nasem discordu!");
                    }
                }
            }
        }
        if (!canUse) {
            return;
        }
//        ChromTeleports.instance().getDelayGetter().setLastUsed(sender.getName(), type, currentTime);
    }
}
