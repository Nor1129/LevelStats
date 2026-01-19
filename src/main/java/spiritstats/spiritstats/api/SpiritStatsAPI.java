package spiritstats.spiritstats.api;

import org.bukkit.entity.Player;
import spiritstats.spiritstats.level.LevelManager;
import spiritstats.spiritstats.level.LevelSystem;
import spiritstats.spiritstats.level.PlayerLevelData;
import spiritstats.spiritstats.stat.PlayerStatData;
import spiritstats.spiritstats.stat.StatManager;

public class SpiritStatsAPI {

    public static int getLevel(Player player) {
        return LevelManager.get(player).getLevel();
    }

    public static int getExp(Player player) {
        return LevelManager.get(player).getExp();
    }

    public static void addExp(Player player, int amount) {
        PlayerLevelData data = LevelManager.get(player);
        data.addExp(amount);

        LevelSystem.checkLevelUp(player);

        LevelManager.save(player);
    }

    public static void addLevel(Player player, int amount) {
        PlayerLevelData data = LevelManager.get(player);
        data.addLevel(amount);
        LevelManager.save(player);
    }

    public static PlayerStatData getStatData(Player player) {
        return StatManager.get(player);
    }

    public static int getResonance(Player player) {
        return StatManager.get(player).getResonance();
    }

    public static int getFlow(Player player) {
        return StatManager.get(player).getFlow();
    }

    public static int getAttack(Player player) {
        return StatManager.get(player).getAttackGlyph();
    }

    public static int getDefense(Player player) {
        return StatManager.get(player).getDefenseGlyph();
    }

    public static int getStatPoint(Player player) {
        return StatManager.get(player).getStatPoint();
    }

    public static void addStatPoint(Player player, int amount) {
        PlayerStatData d = StatManager.get(player);
        d.addPoint(amount);
        StatManager.save(player);
    }
}
