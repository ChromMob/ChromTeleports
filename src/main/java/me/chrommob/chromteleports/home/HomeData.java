package me.chrommob.chromteleports.home;

import me.chrommob.chromteleports.ChromTeleports;
import me.chrommob.chromteleports.delays.dataholders.CommandType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HomeData {
    private final String name;
    private boolean loaded;
    private double x;
    private double y;
    private double z;
    private String world;
    private float yaw;
    private float pitch;
    private int taskId;
    private boolean teleporting;
    private boolean moved = false;
    public HomeData(String name, UUID player) {
        this.name = name;
        this.loaded = false;
        Bukkit.getScheduler().runTaskAsynchronously(ChromTeleports.instance(), () -> {
            ChromTeleports.instance().getDatabaseLoader().requestHomeData(name, player, this);
        });
    }

    public HomeData(String name, Player player) {
        this.name = name;
        this.x = player.getLocation().getX();
        this.y = player.getLocation().getY();
        this.z = player.getLocation().getZ();
        this.world = player.getLocation().getWorld().getName();
        this.yaw = player.getLocation().getYaw();
        this.pitch = player.getLocation().getPitch();
        this.loaded = true;
        writeToDatabase(player.getUniqueId());
    }

    public void teleport(Player player) {
        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(ChromTeleports.instance(), () -> {
            if (!loaded) {
                return;
            }
            scheduleTeleport(player);
        }, 0, 20).getTaskId();
    }

    private void scheduleTeleport(Player player) {
        Bukkit.getScheduler().cancelTask(taskId);
        if (teleporting) {
            return;
        }
        teleporting = true;
        Bukkit.getScheduler().runTaskLater(ChromTeleports.instance(), () -> {
            if (!moved) {
                player.teleport(new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch));
                player.sendMessage(Component.text("Teleportuji...").color(NamedTextColor.DARK_GREEN));
                ChromTeleports.instance().getDelayGetter().setLastUsed(player.getName(), CommandType.HOME, System.currentTimeMillis());
            } else {
                player.sendMessage(Component.text("Pohnul jsi se teleport zrusen.").color(NamedTextColor.RED));
            }
            teleporting = false;
        }, 20 * 3);
    }

    private void writeToDatabase(UUID player) {
        Bukkit.getScheduler().runTaskAsynchronously(ChromTeleports.instance(), () -> {
            ChromTeleports.instance().getDatabaseLoader().writeHomeData(this, player);
        });
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public String getName() {
        return name;
    }

    public String getWorld() {
        return world;
    }


    public void moved() {
        moved = true;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}