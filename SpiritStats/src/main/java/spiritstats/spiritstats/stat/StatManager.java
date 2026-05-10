package spiritstats.spiritstats.stat;


import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class StatManager {
    private static final Map<UUID, PlayerStatData> stats = new HashMap<>();
    private static File dataFolder;

    public static void init(File pluginFolder) {
        dataFolder = new File(pluginFolder, "data");
        if (!dataFolder.exists()) dataFolder.mkdirs();
    }

    public static PlayerStatData get(Player p) {
        return stats.computeIfAbsent(p.getUniqueId(), uuid -> load(uuid));
    }

    private static PlayerStatData load(UUID uuid) {
        File file = new File(dataFolder, uuid + ".yml");
        PlayerStatData d = new PlayerStatData();

        if (!file.exists()) return d;

        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        d.setResonance(yml.getInt("resonance"));
        d.setFlow(yml.getInt("flow"));
        d.setAttackGlyph(yml.getInt("attackGlyph"));
        d.setDefenseGlyph(yml.getInt("defenseGlyph"));
        d.addPoint(yml.getInt("statPoint"));

        return d;
    }

    public static void save(Player p) {
        save(p.getUniqueId());
    }

    public static void save(UUID uuid) {
        PlayerStatData d = stats.get(uuid);
        if (d == null) return;

        File file = new File(dataFolder, uuid + ".yml");
        YamlConfiguration yml = new YamlConfiguration();

        yml.set("resonance", d.getResonance());
        yml.set("flow", d.getFlow());
        yml.set("attackGlyph", d.getAttackGlyph());
        yml.set("defenseGlyph", d.getDefenseGlyph());
        yml.set("statPoint", d.getStatPoint());

        try {
            yml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void unload(Player p) {
        save(p);
        stats.remove(p.getUniqueId());
    }

    public static void saveAll() {
        stats.keySet().forEach(StatManager::save);
    }

    public static void reloadAll() {
        stats.clear();

        File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            try {
                UUID uuid = UUID.fromString(file.getName().replace(".yml", ""));
                stats.put(uuid, load(uuid));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            StatApplier.apply(p);
        }
    }
}