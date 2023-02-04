package me.chrommob.chromteleports;

import co.aikar.commands.PaperCommandManager;
import me.chrommob.chromteleports.commands.Home;
import me.chrommob.chromteleports.commands.Tpa;
import me.chrommob.chromteleports.commands.Warp;
import me.chrommob.chromteleports.delays.DelayGetter;
import me.chrommob.chromteleports.events.MoveListener;
import me.chrommob.chromteleports.home.HomeDatabaseLoader;
import me.chrommob.chromteleports.home.HomeStorage;
import me.chrommob.chromteleports.tpa.RequestsStorage;
import me.chrommob.chromteleports.warps.WarpDatabaseLoader;
import me.chrommob.chromteleports.warps.WarpStorage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public final class ChromTeleports extends JavaPlugin {
    private static ChromTeleports instance;
    private PaperCommandManager manager;
    private DelayGetter delayGetter;
    private RequestsStorage requestsStorage;
    private HomeDatabaseLoader homeDatabaseLoader;
    private WarpDatabaseLoader warpDatabaseLoader;
    private HomeStorage homeStorage;
    private WarpStorage warpStorage;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();
        reloadConfig();
        manager = new PaperCommandManager(this);
        delayGetter = new DelayGetter();
        requestsStorage = new RequestsStorage();
        homeDatabaseLoader = new HomeDatabaseLoader();
        warpDatabaseLoader = new WarpDatabaseLoader();

        homeStorage = new HomeStorage();
        warpStorage = new WarpStorage();

        registerEvents();
        registerCommands();
    }

    private void registerCommands() {
        manager.registerCommand(new Tpa());
        manager.registerCommand(new Home());
        manager.registerCommand(new Warp());
        manager.getCommandCompletions().registerCompletion("homes", commandCompletionContext -> {
            Player sender = commandCompletionContext.getPlayer();
            return homeStorage.getHomes(sender);
        });

        manager.getCommandCompletions().registerCompletion("warps", commandCompletionContext -> {
            Set<String> warps = warpStorage.getWarps();
            Set<String> userAllowedWarps = new HashSet<>();
            Player sender = commandCompletionContext.getPlayer();
            for (String warp : warps) {
                if (sender.hasPermission("chromteleports.warp.use." + warp)) {
                    userAllowedWarps.add(warp);
                }
            }
            return userAllowedWarps;
        });
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new MoveListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static ChromTeleports instance() {
        return instance;
    }

    public DelayGetter getDelayGetter() {
        return delayGetter;
    }

    public RequestsStorage getRequestsStorage() {
        return requestsStorage;
    }

    public HomeDatabaseLoader getHomeDatabaseLoader() {
        return homeDatabaseLoader;
    }

    public HomeStorage getHomeStorage() {
        return homeStorage;
    }

    public WarpStorage getWarpStorage() {
        return warpStorage;
    }

    public WarpDatabaseLoader getWarpDatabaseLoader() {
        return warpDatabaseLoader;
    }
}
