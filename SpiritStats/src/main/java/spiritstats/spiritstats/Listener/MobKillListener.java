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

            case VILLAGER, TADPOLE, PARROT -> addExp = 1;

            case PIG, SHEEP, COW, MUSHROOM_COW, CHICKEN, OCELOT, CAT,
                 HORSE, DONKEY, MULE, TURTLE, FOX, AXOLOTL, FROG, SQUID,
                 COD, SALMON, TROPICAL_FISH, CAMEL, PUFFERFISH, ZOGLIN -> addExp = 2;

            case SKELETON_HORSE, ZOMBIE_HORSE, STRIDER, GLOW_SQUID, BAT,
                 WANDERING_TRADER, LLAMA, TRADER_LLAMA, PANDA, GOAT,
                 POLAR_BEAR, DOLPHIN, HOGLIN -> addExp = 3;

            case RABBIT, SPIDER, DROWNED, BEE, ZOMBIE, SKELETON, SLIME,
                 SILVERFISH, STRAY, ZOMBIE_VILLAGER, HUSK -> addExp = 4;


            case ALLAY, SNIFFER, CAVE_SPIDER, WOLF, ZOMBIFIED_PIGLIN,
                 CREEPER, GUARDIAN, SHULKER -> addExp = 5;

            case ENDERMAN -> addExp = 6;

            case GHAST ->  addExp = 7;

            case IRON_GOLEM, MAGMA_CUBE, ENDERMITE -> addExp = 8;

            case BLAZE -> addExp = 10;

            case WITCH, PIGLIN -> addExp = 12;

            case ELDER_GUARDIAN, WITHER_SKELETON, VINDICATOR -> addExp = 15;

            case EVOKER, PHANTOM -> addExp = 30;

            case VEX, PIGLIN_BRUTE -> addExp = 50;

            case PILLAGER -> addExp = 75;

            case RAVAGER -> addExp = 125;

            case WARDEN -> addExp = 150;

            case WITHER -> addExp = 180;

            case ENDER_DRAGON -> addExp = 200;

            default -> {
                return;
            }
        }

        PlayerLevelData levelData = LevelManager.get(p);
        levelData.addExp(addExp);
        LevelSystem.checkLevelUp(p);
    }
}
