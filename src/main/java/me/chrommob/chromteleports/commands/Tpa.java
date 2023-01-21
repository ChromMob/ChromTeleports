package me.chrommob.chromteleports.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import me.chrommob.chromteleports.ChromTeleports;
import me.chrommob.chromteleports.delays.dataholders.CommandType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

@CommandAlias("tpa")
public class Tpa {
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
        if (ChromTeleports.instance().getRequestsStorage().hasRequest(player.getName())) {
            player.sendMessage("Jiz mas otevreny pozadavek na teleportaci!");
            player.sendMessage("Napis \"/tpa cancel\" aby si ho zrušil.");
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
        ChromTeleports.instance().getRequestsStorage().addRequest(player.getName(), target.getName());
//        ChromTeleports.instance().getDelayGetter().setLastUsed(sender.getName(), type, currentTime);
    }

    @Subcommand("cancel")
    @SuppressWarnings("unused")
    public void onCancel(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to use this command!");
            return;
        }
        if (!ChromTeleports.instance().getRequestsStorage().hasRequest(player.getName())) {
            player.sendMessage("Nemas otevreny pozadavek na teleportaci!");
            return;
        }
        ChromTeleports.instance().getRequestsStorage().removeRequest(player.getName());
        player.sendMessage("Tvuj pozadavek na teleportaci byl uspesne zrusen!");
    }
}
