package spiritstats.spiritstats.stat;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import spiritstats.spiritstats.level.LevelManager;
import spiritstats.spiritstats.level.PlayerLevelData;

public class StatApplier {

    public static void apply(Player p) {
        PlayerStatData stat = StatManager.get(p);
        PlayerLevelData level = LevelManager.get(p);

        double baseHp = 20.0;
        double statHp = StatCalculator.bonusHealth(stat);
        double levelHp = Math.max(0, level.getLevelHpBonus());

        double maxHealth = Math.max(1.0, baseHp + statHp + levelHp);

        p.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                .setBaseValue(maxHealth);

        if (p.getHealth() > maxHealth) {
            p.setHealth(maxHealth);
        }

        if (p.getHealth() <= 0) {
            p.setHealth(1.0);
        }
    }
}