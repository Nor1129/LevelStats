package jobslevelpl.jobslevelpl.api;

import jobslevelpl.jobslevelpl.Jobslevelpl;
import jobslevelpl.jobslevelpl.PlayerData;
import jobslevelpl.jobslevelpl.PlayerManager;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DataAPI {

    private static PlayerManager getManager() {
        Jobslevelpl plugin = Jobslevelpl.getInstance();
        if (plugin == null) {
            throw new IllegalStateException("Jobslevelpl 플러그인이 로드되지 않았습니다.");
        }
        return plugin.getPlayerManager();
    }

    private static PlayerData getData(UUID uuid) {
        return getManager().getPlayerData(uuid);
    }

    /* ================= Mining ================= */

    public static int getMiningLevel(Player player) {
        return getData(player.getUniqueId()).getMiningLevel();
    }

    public static int getMiningExperience(Player player) {
        return getData(player.getUniqueId()).getMiningExperience();
    }

    public static int getMiningExpToLevelUp(Player player) {
        return getData(player.getUniqueId()).getMiningExpToLevelUp();
    }

    public static void addMiningExperience(Player player, int exp) {
        getData(player.getUniqueId()).addMiningExperience(exp);
    }

    /* ================= Farming ================= */

    public static int getFarmingLevel(Player player) {
        return getData(player.getUniqueId()).getFarmingLevel();
    }

    public static int getFarmingExperience(Player player) {
        return getData(player.getUniqueId()).getFarmingExperience();
    }

    public static int getFarmingExpToLevelUp(Player player) {
        return getData(player.getUniqueId()).getFarmingExpToLevelUp();
    }

    public static void addFarmingExperience(Player player, int exp) {
        getData(player.getUniqueId()).addFarmingExperience(exp);
    }

    /* ================= Fishing ================= */

    public static int getFishingLevel(Player player) {
        return getData(player.getUniqueId()).getFishingLevel();
    }

    public static int getFishingExperience(Player player) {
        return getData(player.getUniqueId()).getFishingExperience();
    }

    public static int getFishingExpToLevelUp(Player player) {
        return getData(player.getUniqueId()).getFishingExpToLevelUp();
    }

    public static void addFishingExperience(Player player, int exp) {
        getData(player.getUniqueId()).addFishingExperience(exp);
    }
}
