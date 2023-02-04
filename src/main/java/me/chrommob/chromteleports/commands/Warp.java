package me.chrommob.chromteleports.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.github.puregero.multilib.MultiLib;
import me.chrommob.chromteleports.ChromTeleports;
import me.chrommob.chromteleports.delays.dataholders.CommandType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

@CommandAlias("warp")
public class Warp extends BaseCommand {
    @SuppressWarnings("unused")
    @Default
    @CommandCompletion("@warps")
    @CommandPermission("chromteleports.warp.use")
    public void onWarp(CommandSender sender, String warpName) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return;
        }
        if (!player.hasPermission("chromteleports.warp.use." + warpName)) {
            player.sendMessage(Component.text("Nemas pravo pouzit tento warp!").color(NamedTextColor.RED));
            return;
        }
        CommandType type = CommandType.WARP;
        long currentTime = System.currentTimeMillis();
        long lastUsed = ChromTeleports.instance().getDelayGetter().getLastUsed(sender.getName(), type);
        boolean canUse = false;
        long delay = 0;
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            if (info.getPermission().startsWith("chromteleports.warp.delay.")) {
                String[] split = info.getPermission().split("\\.");
                if (split.length == 4) {
                    try {
                        delay = Integer.parseInt(split[3]) * 1000L;
                        if (currentTime - lastUsed >= delay) {
                            canUse = true;
                        } else {
                            player.sendMessage(Component.text("Musis jeste pockat ").color(NamedTextColor.WHITE).append(Component.text(Math.round((delay - (currentTime - lastUsed)) / 1000.0) + " sekund").color(NamedTextColor.RED)).append(Component.text(" pred tim, nez muzes znovu pouzit tento prikaz!").color(NamedTextColor.WHITE)));
                        }
                        break;
                    } catch (NumberFormatException ignored) {
                        player.sendMessage(Component.text("Nastala chyba pri zpracovani pozadavku! Otevri si ticket na nasem discordu!").color(NamedTextColor.RED));
                        break;
                    }
                }
            }
        }
        if (!canUse) {
            return;
        }
        Location location = ChromTeleports.instance().getWarpStorage().getWarpLocation(warpName);
        if (location == null) {
            player.sendMessage(Component.text("Tento warp neexistuje!").color(NamedTextColor.RED));
            return;
        }
        ChromTeleports.instance().getDelayGetter().setLastUsed(sender.getName(), type, currentTime);
        sender.sendMessage(Component.text("Teleportuji te, 3 sekundy se ").color(NamedTextColor.WHITE).append(Component.text("nehybej!").color(NamedTextColor.RED)));
        ChromTeleports.instance().getWarpStorage().addWarpRequest(sender.getName(), location);
    }

    @SuppressWarnings("unused")
    @Subcommand("set")
    @CommandPermission("chromteleports.warp.set")
    public void onSetWarp(CommandSender sender, String warpName) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return;
        }
        Location location = player.getLocation();
        ChromTeleports.instance().getWarpStorage().addWarp(warpName, location);
        MultiLib.notify("warp:create", warpName);
        player.sendMessage(Component.text("Warp ").color(NamedTextColor.WHITE).append(Component.text(warpName).color(NamedTextColor.GREEN)).append(Component.text(" byl uspesne vytvoren!").color(NamedTextColor.WHITE)));
    }

    @SuppressWarnings("unused")
    @Subcommand("remove")
    @CommandPermission("chromteleports.warp.remove")
    @CommandCompletion("@warps")
    public void onRemoveWarp(CommandSender sender, String warpName) {
        ChromTeleports.instance().getWarpStorage().removeWarp(warpName);
        MultiLib.notify("warp:delete", warpName);
        sender.sendMessage(Component.text("Warp byl uspesne odebran!").color(NamedTextColor.GREEN));
    }
}
