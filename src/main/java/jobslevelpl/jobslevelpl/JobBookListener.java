package jobslevelpl.jobslevelpl;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JobBookListener implements Listener {

    private final Jobslevelpl plugin;

    public JobBookListener(Jobslevelpl plugin) {
        this.plugin = plugin;
    }

    private final List<String> minerJobOrder = Arrays.asList(
            "§7초보 광부",
            "§7견습 광부",
            "§7숙련 광부",
            "§7전문 광부",
            "§7장인 광부"
    );

    private final List<String> farmerJobOrder = Arrays.asList(
            "§6초보 농부",
            "§6견습 농부",
            "§6숙련 농부",
            "§6전문 농부",
            "§6장인 농부"
    );

    private final List<String> fisherJobOrder = Arrays.asList(
            "§b초보 어부",
            "§b견습 어부",
            "§b숙련 어부",
            "§b전문 어부",
            "§b장인 어부"
    );

    private final List<String> butcherJobOrder = Arrays.asList(
            "§c초보 도축",
            "§c견습 도축",
            "§c숙련 도축",
            "§c전문 도축",
            "§c장인 도축"
    );

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if ((event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) || event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.BOOK && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta == null || !meta.hasDisplayName()) return;

            String itemName = ChatColor.stripColor(meta.getDisplayName());

            switch (itemName) {
                case "광부 초보 직업의 서":
                    attemptJobUpgrade(player, "§7초보 광부", item, minerJobOrder);
                    break;
                case "광부 견습 직업의 서":
                    attemptJobUpgrade(player, "§7견습 광부", item, minerJobOrder);
                    break;
                case "광부 숙련 직업의 서":
                    attemptJobUpgrade(player, "§7숙련 광부", item, minerJobOrder);
                    break;
                case "광부 전문 직업의 서":
                    attemptJobUpgrade(player, "§7전문 광부", item, minerJobOrder);
                    break;
                case "광부 장인 직업의 서":
                    attemptJobUpgrade(player, "§7장인 광부", item, minerJobOrder);
                    break;

                case "농부 초보 직업의 서":
                    attemptJobUpgrade(player, "§6초보 농부", item, farmerJobOrder);
                    break;
                case "농부 견습 직업의 서":
                    attemptJobUpgrade(player, "§6견습 농부", item, farmerJobOrder);
                    break;
                case "농부 숙련 직업의 서":
                    attemptJobUpgrade(player, "§6숙련 농부", item, farmerJobOrder);
                    break;
                case "농부 전문 직업의 서":
                    attemptJobUpgrade(player, "§6전문 농부", item, farmerJobOrder);
                    break;
                case "농부 장인 직업의 서":
                    attemptJobUpgrade(player, "§6장인 농부", item, farmerJobOrder);
                    break;

                case "어부 초보 직업의 서":
                    attemptJobUpgrade(player, "§b초보 어부", item, fisherJobOrder);
                    break;
                case "어부 견습 직업의 서":
                    attemptJobUpgrade(player, "§b견습 어부", item, fisherJobOrder);
                    break;
                case "어부 숙련 직업의 서":
                    attemptJobUpgrade(player, "§b숙련 어부", item, fisherJobOrder);
                    break;
                case "어부 전문 직업의 서":
                    attemptJobUpgrade(player, "§b전문 어부", item, fisherJobOrder);
                    break;
                case "어부 장인 직업의 서":
                    attemptJobUpgrade(player, "§b장인 어부", item, fisherJobOrder);
                    break;

                case "도축 초보 직업의 서":
                    attemptJobUpgrade(player, "§c초보 도축", item, butcherJobOrder);
                    break;
                case "도축 견습 직업의 서":
                    attemptJobUpgrade(player, "§c견습 도축", item, butcherJobOrder);
                    break;
                case "도축 숙련 직업의 서":
                    attemptJobUpgrade(player, "§c숙련 도축", item, butcherJobOrder);
                    break;
                case "도축 전문 직업의 서":
                    attemptJobUpgrade(player, "§c전문 도축", item, butcherJobOrder);
                    break;
                case "도축 장인 직업의 서":
                    attemptJobUpgrade(player, "§c장인 도축", item, butcherJobOrder);
                    break;

                default:
                    break;
            }
        }
    }

    private void attemptJobUpgrade(Player player, String job, ItemStack item, List<String> jobOrder) {
        String playerUUID = player.getUniqueId().toString();
        List<String> jobs = plugin.getPlayerJobs().getOrDefault(playerUUID, new ArrayList<>());

        PlayerData playerData = plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        int playerMiningLevel = playerData.getMiningLevel();
        int playerFarmingLevel = playerData.getFarmingLevel();
        int playerFishingLevel = playerData.getFishingLevel();

        int requiredLevel1 = getRequiredLevel1ForJob(job);
        int requiredLevel2 = getRequiredLevel2ForJob(job);
        int requiredLevel3 = getRequiredLevel3ForJob(job);

        if (playerMiningLevel < requiredLevel1) {
            player.sendMessage("§c§l[!] " + "이 직업을 해금하려면 레벨이 최소 " + requiredLevel1 + " 이상이어야 합니다!");
            return;
        }

        if (playerFarmingLevel < requiredLevel2) {
            player.sendMessage("§c§l[!] " + "이 직업을 해금하려면 레벨이 최소 " + requiredLevel2 + " 이상이어야 합니다!");
            return;
        }

        if (playerFishingLevel < requiredLevel3) {
            player.sendMessage("§c§l[!] " + "이 직업을 해금하려면 레벨이 최소 " + requiredLevel3 + " 이상이어야 합니다!");
            return;
        }

        if (jobs.contains(job)) {
            player.sendMessage("§c§l[!] " + "이미 " + job + " 해금 되어있습니다!");
            return;
        }

        if (!canUpgradeJob(jobs, job, jobOrder)) {
            player.sendMessage("§c§l[!] 이 직업을 해금하려면 이전 직업을 먼저 해금해야 합니다!");
            return;
        }
        if (jobs.contains("§a초보자")) {
            jobs.remove("§a초보자");
        }

        jobs.add(job);
        plugin.getPlayerJobs().put(playerUUID, jobs);
        plugin.getJobsConfig().set(playerUUID, jobs);
        plugin.saveJobsData();

        player.sendMessage("§a§l[!] " + job + " §a§l직업이 저장되었습니다!");

        player.getInventory().getItemInMainHand().setAmount(item.getAmount() - 1);
    }

    private int getRequiredLevel1ForJob(String job) {
        switch (job) {
            case "§7초보 광부":
                return 20;
            case "§7견습 광부":
                return 40;
            case "§7숙련 광부":
                return 60;
            case "§7전문 광부":
                return 80;
            case "§7장인 광부":
                return 100;
            default:
                return 0;
        }
    }

    private int getRequiredLevel2ForJob(String job) {
        switch (job) {
            case "§6초보 농부":
                return 20;
            case "§6견습 농부":
                return 40;
            case "§6숙련 농부":
                return 60;
            case "§6전문 농부":
                return 80;
            case "§6장인 농부":
                return 100;
            default:
                return 0;
        }
    }

    private int getRequiredLevel3ForJob(String job) {
        switch (job) {
            case "§b초보 어부":
                return 20;
            case "§b견습 어부":
                return 40;
            case "§b숙련 어부":
                return 60;
            case "§b전문 어부":
                return 80;
            case "§b장인 어부":
                return 100;
            default:
                return 0;
        }
    }
    
    private boolean canUpgradeJob(List<String> jobs, String nextJob, List<String> jobOrder) {
        int nextJobIndex = jobOrder.indexOf(nextJob);
        if (nextJobIndex == -1) return false;

        if (nextJobIndex == 0) {
            return true;
        }

        String previousJob = jobOrder.get(nextJobIndex - 1);
        return jobs.contains(previousJob);
    }
}