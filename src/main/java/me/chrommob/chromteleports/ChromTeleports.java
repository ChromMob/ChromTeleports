package me.chrommob.chromteleports;

import co.aikar.commands.PaperCommandManager;
import me.chrommob.chromteleports.commands.Home;
import me.chrommob.chromteleports.commands.Tpa;
import me.chrommob.chromteleports.delays.DelayGetter;
import me.chrommob.chromteleports.events.MoveListener;
import me.chrommob.chromteleports.home.DatabaseLoader;
import me.chrommob.chromteleports.home.HomeStorage;
import me.chrommob.chromteleports.tpa.RequestsStorage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChromTeleports extends JavaPlugin {
    private static ChromTeleports instance;
    private PaperCommandManager manager;
    private DelayGetter delayGetter;
    private RequestsStorage requestsStorage;
    private DatabaseLoader databaseLoader;
    private HomeStorage homeStorage;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();
        reloadConfig();
        manager = new PaperCommandManager(this);
        delayGetter = new DelayGetter();
        requestsStorage = new RequestsStorage();
        databaseLoader = new DatabaseLoader();
        homeStorage = new HomeStorage();

        registerEvents();
        registerCommands();
    }

    private void registerCommands() {
        manager.registerCommand(new Tpa());
        manager.registerCommand(new Home());
        manager.getCommandCompletions().registerCompletion("homes", commandCompletionContext -> {
            Player sender = commandCompletionContext.getPlayer();
            return homeStorage.getHomes(sender);
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

    public DatabaseLoader getDatabaseLoader() {
        return databaseLoader;
    }

    public HomeStorage getHomeStorage() {
        return homeStorage;
    }
}
