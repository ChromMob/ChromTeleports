package me.chrommob.chromteleports.events;

import me.chrommob.chromteleports.ChromTeleports;
import me.chrommob.chromteleports.home.HomeStorage;
import me.chrommob.chromteleports.tpa.RequestsStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {
    private final RequestsStorage storage;
    private final HomeStorage homeStorage;
    public MoveListener(ChromTeleports plugin) {
        storage = plugin.getRequestsStorage();
        homeStorage = plugin.getHomeStorage();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (storage.hasRequest(event.getPlayer().getName())) {
            storage.getRequest(event.getPlayer().getName()).moved();
        }
        homeStorage.moved(event.getPlayer());
    }
}
