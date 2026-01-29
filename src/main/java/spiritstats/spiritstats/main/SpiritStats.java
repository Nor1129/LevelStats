package spiritstats.spiritstats.main;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import spiritstats.spiritstats.Listener.DamageListener;
import spiritstats.spiritstats.Listener.JoinListener;
import spiritstats.spiritstats.Listener.MobKillListener;
import spiritstats.spiritstats.Listener.SpiritStatsListener;
import spiritstats.spiritstats.api.SpiritStatsAPI;
import spiritstats.spiritstats.level.LevelCommand;
import spiritstats.spiritstats.level.LevelManager;
import spiritstats.spiritstats.stat.StatCommand;
import spiritstats.spiritstats.stat.StatGUI;
import spiritstats.spiritstats.stat.StatManager;

public final class SpiritStats extends JavaPlugin {

    private static SpiritStats instance;

    @Override
    public void onEnable() {
        instance = this;

        StatManager.init(getDataFolder());
        LevelManager.init(getDataFolder());
        SpiritStatsAPI api = new SpiritStatsAPI();
        Bukkit.getPluginManager().registerEvents(new SpiritStatsListener(api), this);
        getServer().getPluginManager().registerEvents(new DamageListener(), this);
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new StatGUI(), this);
        getServer().getPluginManager().registerEvents(new MobKillListener(), this);

        getCommand("전투스탯").setExecutor(new StatCommand());
        getCommand("전투레벨").setExecutor(new LevelCommand());

        getLogger().info("Dev.노아 - 전투 스탯 및 레벨 플러그인 활성화!");
    }

    @Override
    public void onDisable() {
        StatManager.saveAll();
        LevelManager.saveAll();
        getLogger().info("Dev.노아 - 전투 스탯 및 레벨 플러그인 활성화!");
    }

    public static SpiritStats getInstance() {
        return instance;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        StatManager.unload(e.getPlayer());
    }
}