package spiritstats.spiritstats.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import spiritstats.spiritstats.stat.PlayerStatData;
import spiritstats.spiritstats.stat.StatCalculator;
import spiritstats.spiritstats.stat.StatManager;

public class DamageListener implements Listener {

    @EventHandler
    public void onCombat(EntityDamageByEntityEvent e) {

        if (e.getDamager() instanceof Player attacker) {
            PlayerStatData atkData = StatManager.get(attacker);

            double base = e.getDamage();
            double attack = StatCalculator.attack(atkData);
            double inc = StatCalculator.damageIncrease(atkData);

            double result = (base + attack) * (1 + inc);
            e.setDamage(result);
        }

        if (e.getEntity() instanceof Player victim) {
            PlayerStatData defData = StatManager.get(victim);

            double damage = e.getDamage();
            double defense = StatCalculator.defense(defData);
            double reduction = StatCalculator.damageReduction(defData);

            double reduced = Math.max(0, damage - defense);
            reduced = reduced * (1 - reduction);

            e.setDamage(reduced);
        }
    }
}