package spiritstats.spiritstats.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import spiritstats.spiritstats.level.LevelManager;
import spiritstats.spiritstats.level.LevelSystem;
import spiritstats.spiritstats.level.PlayerLevelData;

public class MobKillListener implements Listener {

    @EventHandler
    public void onMobKill(EntityDeathEvent e) {
        if (e.getEntity().getKiller() == null) return;

        Player p = e.getEntity().getKiller();
        int addExp;

        switch (e.getEntityType()) {
            case PIG, COW, SHEEP, CHICKEN,
                 RABBIT, HORSE, DONKEY, MULE,
                 GOAT, LLAMA, TRADER_LLAMA,
                 FOX, CAT, OCELOT,
                 SQUID, GLOW_SQUID,
                 COD, SALMON, PUFFERFISH, TROPICAL_FISH,
                 BAT, PARROT, TURTLE, FROG, AXOLOTL -> addExp = 2;

            case ZOMBIE, HUSK, DROWNED,
                 SKELETON, STRAY,
                 SPIDER, CAVE_SPIDER,
                 SILVERFISH, ENDERMITE,
                 SLIME, MAGMA_CUBE,
                 PHANTOM -> addExp = 4;

            case CREEPER,
                 PILLAGER, VINDICATOR,
                 ZOMBIFIED_PIGLIN,
                 PIGLIN,
                 HOGLIN, ZOGLIN -> addExp = 6;

            case ENDERMAN -> addExp = 8;
            case WITCH, BLAZE -> addExp = 10;
            case WITHER_SKELETON -> addExp = 15;
            case GUARDIAN -> addExp = 15;
            case ELDER_GUARDIAN -> addExp = 30;
            case RAVAGER -> addExp = 25;
            case EVOKER, VEX -> addExp = 12;

            case WITHER -> addExp = 500;
            case ENDER_DRAGON -> addExp = 1000;

            default -> {
                return;
            }
        }

        PlayerLevelData levelData = LevelManager.get(p);
        levelData.addExp(addExp);
        LevelSystem.checkLevelUp(p);
    }
}
