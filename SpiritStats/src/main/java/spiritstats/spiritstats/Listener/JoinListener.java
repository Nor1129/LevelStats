package spiritstats.spiritstats.Listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;
import spiritstats.spiritstats.main.SpiritStats;
import spiritstats.spiritstats.stat.StatApplier;
import spiritstats.spiritstats.stat.StatManager;
import spiritstats.spiritstats.level.LevelManager;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        StatManager.get(p);
        LevelManager.get(p);

        Bukkit.getScheduler().runTaskLater(SpiritStats.getInstance(), () -> {
            if (p.isOnline()) {
                StatApplier.apply(p);
            }
        }, 5L);
    }
}