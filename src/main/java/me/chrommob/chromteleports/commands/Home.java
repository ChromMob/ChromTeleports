package me.chrommob.chromteleports.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.chrommob.chromteleports.ChromTeleports;
import me.chrommob.chromteleports.delays.dataholders.CommandType;
import me.chrommob.chromteleports.home.HomeData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

@CommandAlias("home")
public class Home extends BaseCommand {
    @Default
    @SuppressWarnings("unused")
    @CommandPermission("chromteleports.home.use")
    @CommandCompletion("@homes")
    public void onHome(CommandSender sender, String name) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return;
        }
        CommandType type = CommandType.HOME;
        long currentTime = System.currentTimeMillis();
        long lastUsed = ChromTeleports.instance().getDelayGetter().getLastUsed(sender.getName(), type);
        boolean canUse = false;
        long delay = 0;
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            if (info.getPermission().startsWith("chromteleports.home.delay.")) {
                String[] split = info.getPermission().split("\\.");
                if (split.length == 3) {
                    try {
                        delay = Long.parseLong(split[2]) * 1000;
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
            player.sendMessage(Component.text("Nastala chyba pri zpracovani pozadavku! Otevri si ticket na nasem discordu!").color(NamedTextColor.RED));
        }
        HomeData homeData = ChromTeleports.instance().getHomeStorage().getHomeData(player, name);
        if (homeData == null) {
            sender.sendMessage(Component.text("Takovy home neexistuje!").color(NamedTextColor.RED));
            return;
        }
        homeData.teleport(player);
    }

    @Subcommand("set")
    @SuppressWarnings("unused")
    @CommandPermission("chromteleports.home.set")
    public void onSet(CommandSender sender, String name) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return;
        }
        ChromTeleports.instance().getHomeStorage().addHome(player, name);
        sender.sendMessage(Component.text("Home ").color(NamedTextColor.WHITE).append(Component.text(name).color(NamedTextColor.AQUA)).append(Component.text(" byl uspesne vytvoren!").color(NamedTextColor.WHITE)));
    }

    @Subcommand("delete")
    @SuppressWarnings("unused")
    @CommandPermission("chromteleports.home.delete")
    @CommandCompletion("@homes")
    public void onDelete(CommandSender sender, String name) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return;
        }
        HomeData homeData = ChromTeleports.instance().getHomeStorage().getHomeData(player, name);
        if (homeData == null) {
            sender.sendMessage(Component.text("Takovy home neexistuje!").color(NamedTextColor.RED));
            return;
        }
        ChromTeleports.instance().getHomeStorage().removeHome(player, name);
        sender.sendMessage(Component.text("Home ").color(NamedTextColor.WHITE).append(Component.text(name).color(NamedTextColor.AQUA)).append(Component.text(" byl uspesne smazan!").color(NamedTextColor.WHITE)));
    }
}
