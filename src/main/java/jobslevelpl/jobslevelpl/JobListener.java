package jobslevelpl.jobslevelpl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

public class JobListener implements Listener {

    private final Jobslevelpl plugin;
    private final Random random = new Random();

    public JobListener(Jobslevelpl plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        List<String> playerJobs = plugin.getPlayerJobs().get(player.getUniqueId().toString());
        Location loc = event.getBlock().getLocation();
        Material blockType = event.getBlock().getType();

        if (playerJobs == null || playerJobs.isEmpty()) return;

        boolean isPlaced = plugin.isPlacedBlock(loc);

        if (isPlaced) {
            plugin.removePlacedBlock(loc);
        }

        if (!isPlaced) {
            if (playerJobs.contains("§7초보 광부") && isCommonBlock(blockType) && random.nextDouble() < 0.05) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 200, 0));
                player.sendMessage("§7[광부] §7초보 패시브 발동!");
            }

            if (playerJobs.contains("§7견습 광부") && isOreBlock(blockType) && random.nextDouble() < 0.10) {
                ItemStack additionalItem = getOreDrop(blockType);
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), additionalItem);
                player.sendMessage("§7[광부] §7견습 패시브 발동! +1");
            }

            if (playerJobs.contains("§7숙련 광부") && isOreBlock(blockType) && random.nextDouble() < 0.10) {
                ItemStack additionalItem = getOreDrop(blockType);
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), additionalItem);
                player.sendMessage("§7[광부] §7숙련 패시브 발동! +1");
            }

            if (playerJobs.contains("§7전문 광부") && isOreBlock(blockType) && random.nextDouble() < 0.10) {
                ItemStack additionalItem = getOreDrop(blockType, 2);
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), additionalItem);
                player.sendMessage("§7[광부] §7전문 패시브 발동! +2");
            }

            if (playerJobs.contains("§7장인 광부") && isOreBlock(blockType) && random.nextDouble() < 0.10) {
                ItemStack additionalItem = getOreDrop(blockType, 3);
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), additionalItem);
                player.sendMessage("§7[광부] §7장인 패시브 발동! +3");
            }

            if (playerJobs.contains("§6초보 농부") && isCropReady(blockType) && isCropFullyGrown(blockType, event.getBlock()) && random.nextDouble() < 0.10) {
                giveAdditionalCrop(player, event.getBlock().getLocation(), blockType, 1);
                player.sendMessage("§6[농부] §7초보 패시브 발동! +1");
            }
            if (playerJobs.contains("§6견습 농부") && isCropReady(blockType) && isCropFullyGrown(blockType, event.getBlock()) && random.nextDouble() < 0.10) {
                giveAdditionalCrop(player, event.getBlock().getLocation(), blockType, 1);
                player.sendMessage("§6[농부] §7견습 패시브 발동! +1");
            }
            if (playerJobs.contains("§6숙련 농부") && isCropReady(blockType) && isCropFullyGrown(blockType, event.getBlock()) && random.nextDouble() < 0.10) {
                giveAdditionalCrop(player, event.getBlock().getLocation(), blockType, 2);
                player.sendMessage("§6[농부] §7숙련 패시브 발동! +2");
            }
            if (playerJobs.contains("§6전문 농부") && isCropReady(blockType) && isCropFullyGrown(blockType, event.getBlock()) && random.nextDouble() < 0.10) {
                giveAdditionalCrop(player, event.getBlock().getLocation(), blockType, 3);
                player.sendMessage("§6[농부] §7전문 패시브 발동! +3");
            }
            if (playerJobs.contains("§6장인 농부") && isCropReady(blockType) && isCropFullyGrown(blockType, event.getBlock()) && random.nextDouble() < 0.10) {
                giveAdditionalCrop(player, event.getBlock().getLocation(), blockType, 1);
                player.sendMessage("§6[농부] §7장인 패시브 발동! +1");
            }
        }
    }

    private boolean isCropFullyGrown(Material blockType, org.bukkit.block.Block block) {
        if (block.getBlockData() instanceof org.bukkit.block.data.Ageable) {
            org.bukkit.block.data.Ageable ageable = (org.bukkit.block.data.Ageable) block.getBlockData();
            return ageable.getAge() == ageable.getMaximumAge();
        }
        return false;
    }

    private void giveAdditionalCrop(Player player, Location location, Material cropType, int amount) {
        ItemStack additionalCrop = null;

        switch (cropType) {
            case WHEAT:
                additionalCrop = new ItemStack(Material.WHEAT, amount);
                break;
            case CARROTS:
                additionalCrop = new ItemStack(Material.CARROT, amount);
                break;
            case POTATOES:
                additionalCrop = new ItemStack(Material.POTATO, amount);
                break;
            case BEETROOTS:
                additionalCrop = new ItemStack(Material.BEETROOT, amount);
                break;
            default:
                break;
        }

        if (additionalCrop != null) {
            location.getWorld().dropItemNaturally(location, additionalCrop);
        }
    }

    private boolean isCropReady(Material blockType) {
        return blockType == Material.WHEAT || blockType == Material.CARROTS || blockType == Material.POTATOES || blockType == Material.BEETROOTS;
    }

    private boolean isOreBlock(Material blockType) {
        switch (blockType) {
            case COAL_ORE:
            case DEEPSLATE_COAL_ORE:
            case IRON_ORE:
            case DEEPSLATE_IRON_ORE:
            case COPPER_ORE:
            case DEEPSLATE_COPPER_ORE:
            case GOLD_ORE:
            case DEEPSLATE_GOLD_ORE:
            case DIAMOND_ORE:
            case DEEPSLATE_DIAMOND_ORE:
            case EMERALD_ORE:
            case DEEPSLATE_EMERALD_ORE:
            case REDSTONE_ORE:
            case DEEPSLATE_REDSTONE_ORE:
            case LAPIS_ORE:
            case DEEPSLATE_LAPIS_ORE:
                return true;
            default:
                return false;
        }
    }

    private boolean isCommonBlock(Material blockType) {
        return blockType == Material.STONE || blockType == Material.DEEPSLATE || blockType == Material.GRANITE ||
                blockType == Material.DIORITE || blockType == Material.ANDESITE || blockType == Material.TUFF ||
                blockType == Material.NETHERRACK || blockType == Material.END_STONE || blockType == Material.CALCITE ||
                blockType == Material.DRIPSTONE_BLOCK || blockType == Material.OBSIDIAN || blockType == Material.CRYING_OBSIDIAN ||
                blockType == Material.BLACKSTONE || blockType == Material.BASALT;
    }

    private ItemStack getOreDrop(Material blockType) {
        return getOreDrop(blockType, 1);
    }

    private ItemStack getOreDrop(Material blockType, int amount) {
        Material dropType;
        switch (blockType) {
            case COAL_ORE:
            case DEEPSLATE_COAL_ORE:
                dropType = Material.COAL;
                break;
            case IRON_ORE:
            case DEEPSLATE_IRON_ORE:
                dropType = Material.RAW_IRON;
                break;
            case COPPER_ORE:
            case DEEPSLATE_COPPER_ORE:
                dropType = Material.RAW_COPPER;
                break;
            case GOLD_ORE:
            case DEEPSLATE_GOLD_ORE:
                dropType = Material.RAW_GOLD;
                break;
            case DIAMOND_ORE:
            case DEEPSLATE_DIAMOND_ORE:
                dropType = Material.DIAMOND;
                break;
            case EMERALD_ORE:
            case DEEPSLATE_EMERALD_ORE:
                dropType = Material.EMERALD;
                break;
            case REDSTONE_ORE:
            case DEEPSLATE_REDSTONE_ORE:
                dropType = Material.REDSTONE;
                break;
            case LAPIS_ORE:
            case DEEPSLATE_LAPIS_ORE:
                dropType = Material.LAPIS_LAZULI;
                break;
            default:
                dropType = null;
        }
        return new ItemStack(dropType, amount);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        List<String> playerJobs = plugin.getPlayerJobs().get(player.getUniqueId().toString());

        if (playerJobs == null || playerJobs.isEmpty() || event.getCaught() == null) return;

        if (event.getCaught() instanceof org.bukkit.entity.Item) {
            ItemStack caughtItem = ((org.bukkit.entity.Item) event.getCaught()).getItemStack();
            Material fishType = caughtItem.getType();

            switch (fishType) {
                case COD:
                case SALMON:
                case PUFFERFISH:
                case TROPICAL_FISH:
                    break;
                default:
                    fishType = Material.COD;
                    break;
            }

            if (playerJobs.contains("§b초보 어부") && random.nextDouble() < 0.15) {
                giveAdditionalFish(player, fishType, 1);
                player.sendMessage("§b[어부] §7초보 패시브 발동! +1");
            }

            if (playerJobs.contains("§b견습 어부") && random.nextDouble() < 0.20) {
                giveAdditionalFish(player, fishType, 1);
                player.sendMessage("§b[어부] §7견습 패시브 발동! +1");
            }

            if (playerJobs.contains("§b숙련 어부") && random.nextDouble() < 0.20) {
                giveAdditionalFish(player, fishType, 2);
                player.sendMessage("§b[어부] §7숙련 패시브 발동! +2");
            }

            if (playerJobs.contains("§b전문 어부") && random.nextDouble() < 0.20) {
                giveAdditionalFish(player, fishType, 3);
                player.sendMessage("§b[어부] §7전문 패시브 발동! +3");
            }

            if (playerJobs.contains("§b장인 어부") && random.nextDouble() < 0.20) {
                giveAdditionalFish(player, fishType, 4);
                player.sendMessage("§b[어부] §7장인 패시브 발동! +4");
            }
        }
    }

    private void giveAdditionalFish(Player player, Material fishType, int amount) {
        ItemStack additionalFish = new ItemStack(fishType, amount);
        player.getWorld().dropItemNaturally(player.getLocation(), additionalFish);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player == null) return;

        List<String> playerJobs = plugin.getPlayerJobs().get(player.getUniqueId().toString());

        if (playerJobs == null || playerJobs.isEmpty()) return;

        EntityType entityType = event.getEntityType();

        if (entityType == EntityType.COW || entityType == EntityType.SHEEP || entityType == EntityType.PIG || entityType == EntityType.DONKEY || entityType == EntityType.CHICKEN || entityType == EntityType.HORSE || entityType == EntityType.RABBIT) {
            if (!event.getDrops().isEmpty()) {
                Material dropType = event.getDrops().get(0).getType();
                Location dropLocation = event.getEntity().getLocation();

                if (playerJobs.contains("§c초보 도축") && random.nextDouble() < 0.05) {
                    giveAdditionalMeat(player, dropType, dropLocation, 1);
                    player.sendMessage("§c[도축] §7초보 패시브 발동! +1");
                }

                if (playerJobs.contains("§c견습 도축") && random.nextDouble() < 0.10) {
                    giveAdditionalMeat(player, dropType, dropLocation, 1);
                    player.sendMessage("§c[도축] §7견습 패시브 발동! +1");
                }

                if (playerJobs.contains("§c숙련 도축") && random.nextDouble() < 0.15) {
                    giveAdditionalMeat(player, dropType, dropLocation, 2);
                    player.sendMessage("§c[도축] §7숙련 패시브 발동! +2");
                }

                if (playerJobs.contains("§c전문 도축") && random.nextDouble() < 0.20) {
                    giveAdditionalMeat(player, dropType, dropLocation, 3);
                    player.sendMessage("§c[도축] §7전문 패시브 발동! +3");
                }

                if (playerJobs.contains("§c장인 도축") && random.nextDouble() < 0.10) {
                    giveAdditionalMeat(player, dropType, dropLocation, 3);
                    player.sendMessage("§c[도축] §7장인 패시브 발동! +3");
                }
            }
        }
    }

    private void giveAdditionalMeat(Player player, Material dropType, Location location, int amount) {
        ItemStack additionalMeat = new ItemStack(dropType, amount);
        player.getWorld().dropItemNaturally(location, additionalMeat);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        List<String> playerJobs = plugin.getPlayerJobs().get(player.getUniqueId().toString());

        if (playerJobs != null && playerJobs.contains("§a초보자")) {
            event.setKeepInventory(true);
            event.setKeepLevel(false);
            event.getDrops().clear();
            player.sendMessage("§a[초보자] §7사망하였으나 인벤토리를 잃지 않았습니다!");
        }
    }
}