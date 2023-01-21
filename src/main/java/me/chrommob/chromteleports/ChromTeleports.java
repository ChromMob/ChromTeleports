package me.chrommob.chromteleports;

import co.aikar.commands.PaperCommandManager;
import me.chrommob.chromteleports.delays.DelayGetter;
import me.chrommob.chromteleports.teleport.RequestsStorage;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChromTeleports extends JavaPlugin {
    private static ChromTeleports instance;
    private PaperCommandManager manager;
    private DelayGetter delayGetter;
    private RequestsStorage requestsStorage;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        manager = new PaperCommandManager(this);
        delayGetter = new DelayGetter();
        requestsStorage = new RequestsStorage();
        registerCommands();
    }

    private void registerCommands() {

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
}
