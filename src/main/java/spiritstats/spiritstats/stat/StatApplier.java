package spiritstats.spiritstats.stat;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class StatApplier {

    public static void apply(Player p) {
        PlayerStatData d = StatManager.get(p);

        double maxHealth = 20 + StatCalculator.bonusHealth(d);

        p.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                .setBaseValue(maxHealth);

        if (p.getHealth() > maxHealth) {
            p.setHealth(maxHealth);
        }
    }
}