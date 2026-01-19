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
        double levelHp = level.getLevelHpBonus();

        double maxHealth = baseHp + statHp + levelHp;

        p.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                .setBaseValue(maxHealth);

        if (p.getHealth() > maxHealth) {
            p.setHealth(maxHealth);
        }
    }
}