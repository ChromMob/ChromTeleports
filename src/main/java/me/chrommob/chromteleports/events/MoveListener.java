package me.chrommob.chromteleports.events;

import me.chrommob.chromteleports.ChromTeleports;
import me.chrommob.chromteleports.teleport.RequestsStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {
    private final RequestsStorage storage;
    public MoveListener(ChromTeleports plugin) {
        storage = plugin.getRequestsStorage();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (storage.hasRequest(event.getPlayer().getName())) {
            storage.getRequest(event.getPlayer().getName()).moved();
        }
    }
}
