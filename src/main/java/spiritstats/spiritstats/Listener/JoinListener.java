package spiritstats.spiritstats.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;
import spiritstats.spiritstats.stat.StatApplier;
import spiritstats.spiritstats.stat.StatManager;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        StatManager.get(p);
        StatApplier.apply(p);
    }
}