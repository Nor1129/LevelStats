package spiritstats.spiritstats.level;

import org.bukkit.entity.Player;
import spiritstats.spiritstats.stat.PlayerStatData;
import spiritstats.spiritstats.stat.StatManager;

public class LevelSystem {

    public static void checkLevelUp(Player p) {

        PlayerLevelData d = LevelManager.get(p);
        PlayerStatData stat = StatManager.get(p);

        boolean leveledUp = false;

        while (d.getLevel() < LevelManager.MAX_LEVEL) {
            int need = LevelExpTable.getRequiredExp(d.getLevel());

            if (d.getExp() < need) break;

            d.setExp(d.getExp() - need);
            d.addLevel(1);

            stat.addPoint(LevelManager.STAT_PER_LEVEL);
            leveledUp = true;

            p.sendTitle(
                    "§6[ §f레벨업 §6]",
                    "§6(" + d.getLevel() + "레벨) §f레벨 달성",
                    10, 40, 10
            );
        }

        if (leveledUp) {
            LevelManager.save(p);
            StatManager.save(p);
        }
    }
}
