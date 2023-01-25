package me.chrommob.chromteleports.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.github.puregero.multilib.MultiLib;
import me.chrommob.chromteleports.ChromTeleports;
import me.chrommob.chromteleports.delays.dataholders.CommandType;
import me.chrommob.chromteleports.tpa.TeleportationRequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

@CommandAlias("tpa")
public class Tpa extends BaseCommand {
    @Default
    @SuppressWarnings("unused")
    @CommandCompletion("@players")
    @CommandPermission("chromteleports.tpa.use")
    public void onTpa(CommandSender sender, String targetString) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("You must be a player to use this command!").color(NamedTextColor.RED));
            return;
        }
        boolean targetOnline = false;
        Player target = ChromTeleports.instance().getServer().getPlayer(targetString);
        if (target != null) {
            targetOnline = true;
        }
        if (!targetOnline) {
            sender.sendMessage(Component.text("Hrac neni online!").color(NamedTextColor.RED));
            return;
        }
        if (target == player) {
            sender.sendMessage(Component.text("Nemuzes se teleportovat sam na sebe!").color(NamedTextColor.RED));
            return;
        }
        if (ChromTeleports.instance().getRequestsStorage().hasRequest(player.getName())) {
            player.sendMessage(Component.text("Jiz mas existujici pozadavek na teleport.").color(NamedTextColor.RED));
            player.sendMessage(Component.text("Napis \"/tpa cancel\" aby si ho zrusil.").color(NamedTextColor.AQUA));
            return;
        }
        CommandType type = CommandType.TPA;
        long currentTime = System.currentTimeMillis();
        long lastUsed = ChromTeleports.instance().getDelayGetter().getLastUsed(sender.getName(), type);
        boolean canUse = false;
        long delay = 0;
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            if (info.getPermission().startsWith("chromteleports.tpa.delay.")) {
                String[] split = info.getPermission().split("\\.");
                if (split.length == 3) {
                    try {
                        delay = Long.parseLong(split[2]) * 1000;
                        if (currentTime - lastUsed >= delay) {
                            player.sendMessage(Component.text("Poslal jsi pozadavek na teleportaci hraci ").color(NamedTextColor.WHITE).append(Component.text(targetString).color(NamedTextColor.AQUA)).append(Component.text("!").color(NamedTextColor.WHITE)));
                            player.sendMessage(Component.text("Napis \"/tpa cancel\" aby si pozadavek zrusil.").color(NamedTextColor.AQUA));
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
            player.sendMessage(Component.text("Nastala chyba pri zpracovani pozadavku! Otevri si ticket na nasem discordu!").color(NamedTextColor.RED));
        }
        if (!canUse) {
            return;
        }
        target.sendMessage(Component.text("Hrac ").color(NamedTextColor.WHITE).append(Component.text(player.getName()).color(NamedTextColor.AQUA)).append(Component.text(" se chce teleportovat na tebe!").color(NamedTextColor.WHITE)));
        ChromTeleports.instance().getRequestsStorage().addRequest(player.getName(), target.getName());
    }

    @Subcommand("cancel")
    @CommandPermission("chromteleports.tpa.cancel")
    @SuppressWarnings("unused")
    public void onCancel(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("You must be a player to use this command!").color(NamedTextColor.RED));
            return;
        }
        if (!ChromTeleports.instance().getRequestsStorage().hasRequest(player.getName())) {
            player.sendMessage(Component.text("Nemas otevreny pozadavek na teleportaci!").color(NamedTextColor.RED));
            return;
        }
        ChromTeleports.instance().getRequestsStorage().removeRequest(player.getName());
        player.sendMessage(Component.text("Tvuj pozadavek na teleportaci byl uspesne ").color(NamedTextColor.WHITE).append(Component.text("zrusen").color(NamedTextColor.AQUA)).append(Component.text("!").color(NamedTextColor.WHITE)));
    }

    @Subcommand("accept")
    @SuppressWarnings("unused")
    @CommandCompletion("@players")
    @CommandPermission("chromteleports.tpa.accept")
    public void onAccept(CommandSender sender, String targetString) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("You must be a player to use this command!").color(NamedTextColor.RED));
            return;
        }
        if (player.getName().equals(targetString)) {
            player.sendMessage(Component.text("Nemuzes prijmout pozadavek na teleportaci od sebe!").color(NamedTextColor.RED));
            return;
        }
        boolean targetOnline = false;
        Player target = ChromTeleports.instance().getServer().getPlayer(targetString);
        if (target != null) {
            targetOnline = true;
        }
        if (!targetOnline) {
            sender.sendMessage(Component.text("Hrac ").color(NamedTextColor.WHITE).append(Component.text(targetString).color(NamedTextColor.RED)).append(Component.text(" neni online!").color(NamedTextColor.WHITE)));
            return;
        }
        if (MultiLib.isExternalPlayer(target)) {
            MultiLib.notify("tpa:accept", target.getName() + " " + player.getName());
        } else {
            TeleportationRequest request = ChromTeleports.instance().getRequestsStorage().getRequest(target.getName());
            if (request == null) {
                player.sendMessage(Component.text("Hrac ").color(NamedTextColor.WHITE).append(Component.text(targetString).color(NamedTextColor.RED)).append(Component.text(" nema otevreny pozadavek na teleportaci!").color(NamedTextColor.WHITE)));
                return;
            }
            if (!request.getSender().equals(player.getName())) {
                player.sendMessage(Component.text("Hrac ").color(NamedTextColor.WHITE).append(Component.text(targetString).color(NamedTextColor.RED)).append(Component.text(" nema otevreny pozadavek na teleportaci od tebe!").color(NamedTextColor.WHITE)));
                return;
            }
            request.accept();
        }
    }
}
