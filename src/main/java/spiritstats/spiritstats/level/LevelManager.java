package spiritstats.spiritstats.level;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LevelManager {

    private static File folder;
    private static final Map<UUID, PlayerLevelData> dataMap = new HashMap<>();

    public static final int MAX_LEVEL = 70;
    public static final int STAT_PER_LEVEL = 3;

    public static void init(File dataFolder) {
        folder = new File(dataFolder, "level");
        if (!folder.exists()) folder.mkdirs();
    }

    public static PlayerLevelData get(Player p) {
        return dataMap.computeIfAbsent(p.getUniqueId(), uuid -> load(uuid));
    }

    private static PlayerLevelData load(UUID uuid) {
        File file = new File(folder, uuid + ".yml");
        PlayerLevelData d = new PlayerLevelData();

        if (!file.exists()) return d;

        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

        d.setLevel(Math.min(MAX_LEVEL, yml.getInt("level", 1)));
        d.setExp(yml.getInt("exp", 0));

        return d;
    }

    public static void save(Player p) {
        save(p.getUniqueId());
    }

    public static void save(UUID uuid) {
        PlayerLevelData d = dataMap.get(uuid);
        if (d == null) return;

        File file = new File(folder, uuid + ".yml");
        YamlConfiguration yml = new YamlConfiguration();

        yml.set("level", d.getLevel());
        yml.set("exp", d.getExp());

        try {
            yml.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveAll() {
        dataMap.keySet().forEach(LevelManager::save);
    }

    public static void reloadAll() {
        dataMap.clear();

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            try {
                String name = file.getName().replace(".yml", "");
                UUID uuid = UUID.fromString(name);

                PlayerLevelData data = load(uuid);
                dataMap.put(uuid, data);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
