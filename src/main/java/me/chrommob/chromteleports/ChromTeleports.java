package me.chrommob.chromteleports;

import co.aikar.commands.PaperCommandManager;
import me.chrommob.chromteleports.delays.DelayGetter;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChromTeleports extends JavaPlugin {
    private static ChromTeleports instance;
    private PaperCommandManager manager;
    private DelayGetter delayGetter;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        manager = new PaperCommandManager(this);
        delayGetter = new DelayGetter();
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
}
