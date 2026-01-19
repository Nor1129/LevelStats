package jobslevelpl.jobslevelpl;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class PlayerManager {

    private final File dataFolder;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final HashMap<UUID, PlayerData> playerDataMap = new HashMap<>();

    public PlayerManager(File dataFolder) {
        this.dataFolder = dataFolder;
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public void savePlayerData(UUID playerId) {
        PlayerData data = playerDataMap.get(playerId);
        if (data != null) {
            File file = new File(dataFolder, playerId.toString() + ".json");
            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(data, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public PlayerData loadPlayerData(UUID playerId) {
        File file = new File(dataFolder, playerId.toString() + ".json");
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                PlayerData data = gson.fromJson(reader, PlayerData.class);
                playerDataMap.put(playerId, data);
                return data;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new PlayerData(playerId);
    }

    public PlayerData getPlayerData(UUID playerId) {
        return playerDataMap.computeIfAbsent(playerId, this::loadPlayerData);
    }

    public Set<UUID> getAllPlayerIds() {
        return playerDataMap.keySet();
    }
}