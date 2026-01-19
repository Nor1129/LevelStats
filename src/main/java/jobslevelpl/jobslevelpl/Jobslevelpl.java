package jobslevelpl.jobslevelpl;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.*;

import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import spiritstats.spiritstats.level.LevelManager;
import spiritstats.spiritstats.level.PlayerLevelData;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public final class Jobslevelpl extends JavaPlugin implements Listener {
    private static Jobslevelpl instance;
    private PlayerManager playerManager;
    private final Map<UUID, BossBar> miningBossBars = new HashMap<>();
    private final Map<UUID, BossBar> farmingBossBars = new HashMap<>();
    private final Map<UUID, BossBar> fishingBossBars = new HashMap<>();
    private final Map<String, Double> fishChances = new HashMap<>();
    private final Map<String, Integer> fishModelData = new HashMap<>();
    private final Map<UUID, Long> lastFishEventTime = new HashMap<>();
    private final Random random = new Random();
    private int saveIntervalTicks = 200;
    private Economy economy;
    private Map<String, List<String>> playerJobs;
    private File jobsFile;
    private FileConfiguration jobsConfig;
    private final Set<Location> placedBlocks = new HashSet<>();
    private Connection connection;
    private final String host = "localhost";
    private final String database = "RaoGramJobDB";
    private final String username = "root";
    private final String password = "Mypass11word29ljh";


    @Override
    public void onEnable() {
        instance = this;
        playerJobs = new HashMap<>();
        connect();
        if (connection == null) {
            getLogger().severe("DB 연결 실패로 플러그인을 비활성화합니다.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        loadJobsData();
        this.getCommand("직업").setExecutor(new JobCommand(this));
        getServer().getPluginManager().registerEvents(new JobListener(this), this);
        getServer().getPluginManager().registerEvents(new JobBookListener(this), this);
        loadPlacedBlocks();

        Bukkit.getPluginManager().registerEvents(this, this);
        initializeFishData();
        getRandomFish();

        if (!setupEconomy()) {
            getLogger().severe("Vault 플러그인을 찾을 수 없습니다. 생활직업 플러그인을 비활성화!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    updateScoreboard(player);
                }
            }
        }.runTaskTimer(this, 0L, 20L);

        this.playerManager = new PlayerManager(this.getDataFolder());
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("저장된 생활직업 데이터를 불러왔습니다.");

        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID playerId = player.getUniqueId();
            playerManager.loadPlayerData(playerId);
        }

        startAutoSaveTask();

        getLogger().info("Dev.노아-생활직업 플러그인 활성화!");
    }

    @Override
    public void onDisable() {
        saveJobsData();
        getLogger().info("Dev.노아-생활직업 플러그인 비활성화!");
        for (UUID playerId : playerManager.getAllPlayerIds()) {
            playerManager.savePlayerData(playerId);
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        getLogger().info("생활직업 데이터가 저장되었습니다.");
    }

    public static Jobslevelpl getInstance() {
        return instance;
    }

    public boolean connect() {
        try {
            if (connection != null && !connection.isClosed()) return true;

            Connection tempConn = DriverManager.getConnection(
                    "jdbc:mysql://" + host + "/?useSSL=false&serverTimezone=UTC",
                    username, password
            );

            Statement stmt = tempConn.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + database);
            stmt.close();
            tempConn.close();

            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + "/" + database + "?useSSL=false&serverTimezone=UTC",
                    username, password
            );

            createTablesIfNotExists();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            connection = null;
            return false;
        }
    }

    private void createTablesIfNotExists() throws SQLException {
        String sqljobdata = "CREATE TABLE IF NOT EXISTS placed_blocks (" +
                "world VARCHAR(50), x INT, y INT, z INT, type VARCHAR(50), " +
                "PRIMARY KEY(world,x,y,z))";

        Statement stmt = connection.createStatement();
        stmt.executeUpdate(sqljobdata);
        stmt.close();
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    void savePlacedBlock(Location loc, Material type) {
        if (!isConnected()) return;

        placedBlocks.add(loc);

        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO placed_blocks (world, x, y, z, type) VALUES (?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE type = VALUES(type)")) {
            ps.setString(1, loc.getWorld().getName());
            ps.setInt(2, loc.getBlockX());
            ps.setInt(3, loc.getBlockY());
            ps.setInt(4, loc.getBlockZ());
            ps.setString(5, type.name());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void loadPlacedBlocks() {
        if (!isConnected()) return;

        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT world, x, y, z FROM placed_blocks");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String worldName = rs.getString("world");
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int z = rs.getInt("z");
                World world = getServer().getWorld(worldName);

                if (world != null) {
                    Location loc = new Location(world, x, y, z);
                    placedBlocks.add(loc);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void removePlacedBlock(Location loc) {
        if (!isConnected()) return;

        placedBlocks.remove(loc);

        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM placed_blocks WHERE world=? AND x=? AND y=? AND z=?")) {
            ps.setString(1, loc.getWorld().getName());
            ps.setInt(2, loc.getBlockX());
            ps.setInt(3, loc.getBlockY());
            ps.setInt(4, loc.getBlockZ());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    boolean isPlacedBlock(Location loc) {
        return placedBlocks.contains(loc);
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        return economy != null;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    private void initializeFishData() {
        addFish("§cF§f등급 §a복어", 90.0, 1);
        addFish("§cF§f등급 §a대구", 90.0, 2);
        addFish("§cF§f등급 §a연어", 90.0, 3);
        addFish("§cF§f등급 §a열대어", 90.0, 4);

        addFish("§7E§f등급 §a꽃게", 80.0, 5);
        addFish("§7E§f등급 §a멸치", 80.0, 6);
        addFish("§7E§f등급 §a조개", 80.0, 7);
        addFish("§7E§f등급 §a금붕어", 80.0, 8);
        addFish("§7E§f등급 §a쭈꾸미", 80.0, 9);
        addFish("§7E§f등급 §a갈치", 80.0, 10);

        addFish("§aD§f등급 §a문어", 60.0, 11);
        addFish("§aD§f등급 §a오징어", 60.0, 12);
        addFish("§aD§f등급 §a고등어", 60.0, 13);
        addFish("§aD§f등급 §a가재", 60.0, 14);
        addFish("§aD§f등급 §a늑대거북", 60.0, 15);
        addFish("§aD§f등급 §a가물치", 60.0, 16);
        addFish("§aD§f등급 §a청새치", 60.0, 17);

        addFish("§aC§f등급 §a블루탱", 40.0, 18);
        addFish("§aC§f등급 §a가오리", 40.0, 19);
        addFish("§aC§f등급 §a바다거북", 40.0, 20);
        addFish("§aC§f등급 §a참치", 40.0, 21);
        addFish("§aC§f등급 §a메기", 40.0, 22);
        addFish("§aC§f등급 §a다랑어", 40.0, 23);

        addFish("§bB§f등급 §a피라쿠루", 25.0, 24);
        addFish("§bB§f등급 §a아로와나", 25.0, 25);
        addFish("§bB§f등급 §a피라니아", 25.0, 26);
        addFish("§bB§f등급 §a왕연어", 25.0, 27);
        addFish("§bB§f등급 §a아귀", 25.0, 28);

        addFish("§cA§f등급 §a큰돌고래", 10.0, 29);
        addFish("§cA§f등급 §a악어", 10.0, 30);
        addFish("§cA§f등급 §a범고래", 10.0, 31);
        addFish("§cA§f등급 §a대왕고래", 10.0, 32);
        addFish("§cA§f등급 §a귀상어", 10.0, 33);
        addFish("§cA§f등급 §a고래상어", 10.0, 34);

        addFish("§c§lS§f등급 §a산갈치", 2.0, 35);
        addFish("§c§lS§f등급 §a백상아리", 2.0, 36);
        addFish("§c§lS§f등급 §a메갈로돈", 2.0, 37);
        addFish("§c§lS§f등급 §a바실로사우루스", 2.0, 38);

        addFish("§c§lSS§f§l등급 §a크라켄", 0.5, 39);
        addFish("§c§lSS§f§l등급 §a레비아탄", 0.5, 40);
        addFish("§c§lSS§f§l등급 §a데메니기스", 0.5, 41);
        addFish("§c§lSS§f§l등급 §a실러캔스", 0.5, 42);

        addFish("§c§lSSS§f§l등급 §a라기아크루스", 0.3, 43);
    }

    private void addFish(String name, double chance, int modelData) {
        fishChances.put(name, chance);
        fishModelData.put(name, modelData);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getCaught() != null && event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Player player = event.getPlayer();
            UUID playerUUID = player.getUniqueId();

            long currentTime = System.currentTimeMillis();
            if (lastFishEventTime.containsKey(playerUUID) && currentTime - lastFishEventTime.get(playerUUID) < 1000) {
                return;
            }
            lastFishEventTime.put(playerUUID, currentTime);

            event.getCaught().remove();

            ItemStack fish = getRandomFishForPlayer(player);
            player.getInventory().addItem(fish);

            int exp = getExperienceForFish(fish);
            if (exp > 0) {
                addExperience(player, exp, "낚시");
                updateBossBar(player, "낚시");
            }

            player.sendMessage("§b[낚시] " + fish.getItemMeta().getDisplayName() + "§f를(을) 낚았습니다!");
        }
    }

    private ItemStack getRandomFish() {
        double totalProbability = fishChances.values().stream().mapToDouble(Double::doubleValue).sum();
        double randomValue = random.nextDouble() * totalProbability;
        double cumulativeProbability = 0.0;

        for (Map.Entry<String, Double> entry : fishChances.entrySet()) {
            cumulativeProbability += entry.getValue();
            if (randomValue <= cumulativeProbability) {
                return createFishItem(entry.getKey());
            }
        }
        return createFishItem("§cF§f등급 §a대구");
    }

    private ItemStack createFishItem(String fishName) {
        ItemStack fish = new ItemStack(Material.CHARCOAL);
        ItemMeta meta = fish.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(fishName);

            Integer modelData = fishModelData.get(fishName);
            if (modelData != null) {
                meta.setCustomModelData(modelData);
            }

            List<String> lore = new ArrayList<>();
            lore.add("§9커스텀 물고기");
            lore.add("");
            lore.add("§7물고기 매입상이 나타날 경우 더 비싼 값어치에 판매할 수 있다.");
            meta.setLore(lore);

            fish.setItemMeta(meta);
        }
        return fish;
    }

    private String getHighestFishingJob(List<String> jobs) {
        if (jobs.contains("§b장인 어부")) {
            return "§b장인 어부";
        } else if (jobs.contains("§b전문 어부")) {
            return "§b전문 어부";
        } else if (jobs.contains("§b숙련 어부")) {
            return "§b숙련 어부";
        } else if (jobs.contains("§b견습 어부")) {
            return "§b견습 어부";
        } else if (jobs.contains("§b초보 어부")) {
            return "§b초보 어부";
        }
        return null;
    }

    private ItemStack getRandomFishForPlayer(Player player) {
        List<String> jobs = this.getPlayerJobs().getOrDefault(player.getUniqueId().toString(), new ArrayList<>());
        String highestFishingJob = getHighestFishingJob(jobs);

        Map<String, Double> availableFishChances = getFishChancesForJob(highestFishingJob);

        double totalProbability = availableFishChances.values().stream().mapToDouble(Double::doubleValue).sum();
        double randomValue = random.nextDouble() * totalProbability;
        double cumulativeProbability = 0.0;

        for (Map.Entry<String, Double> entry : availableFishChances.entrySet()) {
            cumulativeProbability += entry.getValue();
            if (randomValue <= cumulativeProbability) {
                return createFishItem(entry.getKey());
            }
        }
        return createFishItem("§cF§f등급 §a대구"); // 기본값
    }

    private Map<String, Double> getFishChancesForJob(String highestFishingJob) {
        Map<String, Double> filteredFishChances = new HashMap<>();

        if (highestFishingJob == null) {
            filteredFishChances.putAll(getFishChances("F", "E"));
            return filteredFishChances;
        }

        switch (highestFishingJob) {
            case "§b초보 어부":
                filteredFishChances.putAll(getFishChances("E", "D", "C", "B"));
                break;
            case "§b견습 어부":
                filteredFishChances.putAll(getFishChances("E", "D", "C", "B", "A"));
                break;
            case "§b숙련 어부":
                filteredFishChances.putAll(getFishChances("D", "C", "B", "A", "S"));
                break;
            case "§b전문 어부":
                filteredFishChances.putAll(getFishChances("C", "B", "A", "S", "SS"));
                break;
            case "§b장인 어부":
                filteredFishChances.putAll(getFishChances("A", "S", "SS", "SSS"));
                break;
        }
        return filteredFishChances;
    }

    private Map<String, Double> getFishChances(String... grades) {
        Map<String, Double> result = new HashMap<>();
        for (String grade : grades) {
            for (Map.Entry<String, Double> entry : fishChances.entrySet()) {
                if (entry.getKey().contains(grade)) {
                    result.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return result;
    }

    private void updateScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();

        Objective objective = board.registerNewObjective("money", "dummy", "§x§7§5§2§6§D§0R§x§8§5§3§8§D§8a§x§9§5§4§A§E§0o§x§A§6§5§D§E§8G§x§B§6§6§F§E§Fr§x§C§6§8§1§F§7a§x§D§6§9§3§F§Fm");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        PlayerLevelData d = LevelManager.get(player);
        double balance = economy.getBalance(player);
        DecimalFormat df = new DecimalFormat("#,###");
        String formattedBalance = df.format(balance);
        String getExp = df.format(d.getExp());

        PlayerData playerData = playerManager.getPlayerData(player.getUniqueId());
        int miningLevel = playerData.getMiningLevel();
        int farmingLevel = playerData.getFarmingLevel();
        int fishingLevel = playerData.getFishingLevel();

        List<String> jobs = this.getPlayerJobs().getOrDefault(player.getUniqueId().toString(), null);
        String latestJob = (jobs == null || jobs.isEmpty()) ? "§a백수" : jobs.get(jobs.size() - 1);

        World world = player.getWorld();
        String worldName = world.getName();
        String translatedWorldName;

        switch (worldName) {
            case "world":
                translatedWorldName = "월드";
                break;
            case "world_nether":
                translatedWorldName = "지옥";
                break;
            case "world_the_end":
                translatedWorldName = "엔드";
                break;
            case "Bastille":
                translatedWorldName = "바스티유 감옥";
                break;
            case "Island":
                translatedWorldName = "라따라따섬";
                break;
            case "raoworld":
                translatedWorldName = "라오";
                break;
            case "realworld":
                translatedWorldName = "무역배";
                break;
            case "testworld":
                translatedWorldName = "운영자 월드";
                break;
            case "wither_arena":
                translatedWorldName = "월드 보스";
                break;
            case "deepseaworld":
                translatedWorldName = "원양어선";
                break;
            default:
                translatedWorldName = worldName;
                break;
        }

        Score score0 = objective.getScore("§a닉네임§f : " + player.getName());
        Score score1 = objective.getScore("§e소지금§f : " + formattedBalance + "§f \uE021");
        Score score2 = objective.getScore("§6직업§f : " + latestJob);
        Score score3 = objective.getScore("§f현재 채널 §f: §e" + translatedWorldName);
        Score score4 = objective.getScore("§1");
        Score score5 = objective.getScore("§6[ 생활 레벨 ]");
        Score score6 = objective.getScore("§7채광§f : " + miningLevel);
        Score score7 = objective.getScore("§a농사§f : " + farmingLevel);
        Score score8 = objective.getScore("§b낚시§f : " + fishingLevel);
        Score score9 = objective.getScore("§2");
        Score score10 = objective.getScore("§c[ 전투 레벨 ]");
        Score score11 = objective.getScore("§e레벨§f : " + d.getLevel());
        Score score12 = objective.getScore("§7경험치§f : " + getExp);

        score0.setScore(10);
        score1.setScore(9);
        score2.setScore(8);
        score3.setScore(7);
        score4.setScore(6);
        score5.setScore(5);
        score6.setScore(4);
        score7.setScore(3);
        score8.setScore(2);
        score9.setScore(1);
        score10.setScore(0);
        score11.setScore(-1);
        score12.setScore(-2);

        player.setScoreboard(board);
    }

    private void startAutoSaveTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID playerId : playerManager.getAllPlayerIds()) {
                    playerManager.savePlayerData(playerId);
                }
            }
        }.runTaskTimer(this, saveIntervalTicks, saveIntervalTicks);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        playerManager.loadPlayerData(playerId);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        playerManager.savePlayerData(playerId);
        getLogger().info(player.getName() + "님의 데이터가 저장되었습니다.");
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        Material type = block.getType();
        if (type == Material.CARROTS || type == Material.POTATOES || type == Material.BEETROOTS || type == Material.NETHER_WART || type == Material.COCOA || type == Material.WHEAT) {
            return;
        }
        savePlacedBlock(block.getLocation(), type);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        Block block = event.getBlock();
        Location loc = block.getLocation();

        boolean isPlaced = isPlacedBlock(loc);

        if (isPlaced) {
            return;
        }

        if (!isPlaced) {
            if (isMiningBlock(block.getType())) {
                playerManager.getPlayerData(playerUUID);
                int exp = getExperienceForBlock(block.getType());
                if (exp > 0) {
                    addExperience(player, exp, "채광");
                    updateBossBar(player, "채광");
                }
            } else if (isFarmingBlock(block.getType()) || isFullyGrownCrop(block)) {
                playerManager.getPlayerData(playerUUID);
                int exp = getExperienceForFarming(block.getType(), block);
                if (exp > 0) {
                    addExperience(player, exp, "농사");
                    updateBossBar(player, "농사");
                }
            }
        }
    }

    private boolean isFullyGrownCrop(Block block) {
        Material type = block.getType();

        switch (type) {
            case CARROTS:
                org.bukkit.block.data.Ageable carrots = (org.bukkit.block.data.Ageable) block.getBlockData();
                return carrots.getAge() == carrots.getMaximumAge();
            case POTATOES:
                org.bukkit.block.data.Ageable potatoes = (org.bukkit.block.data.Ageable) block.getBlockData();
                return potatoes.getAge() == potatoes.getMaximumAge();
            case BEETROOTS:
                org.bukkit.block.data.Ageable beetroots = (org.bukkit.block.data.Ageable) block.getBlockData();
                return beetroots.getAge() == beetroots.getMaximumAge();
            case WHEAT:
                org.bukkit.block.data.Ageable wheat = (org.bukkit.block.data.Ageable) block.getBlockData();
                return wheat.getAge() == wheat.getMaximumAge();
            case NETHER_WART:
                org.bukkit.block.data.Ageable nether = (org.bukkit.block.data.Ageable) block.getBlockData();
                return nether.getAge() == nether.getMaximumAge();
            case COCOA:
                org.bukkit.block.data.Ageable cocoa = (org.bukkit.block.data.Ageable) block.getBlockData();
                return cocoa.getAge() == cocoa.getMaximumAge();
            default:
                return true;
        }
    }

    boolean isMiningBlock(Material block) {
        return block == Material.COAL_ORE || block == Material.DEEPSLATE_COAL_ORE || block == Material.COPPER_ORE || block == Material.DEEPSLATE_COPPER_ORE || block == Material.LAPIS_ORE || block == Material.DEEPSLATE_LAPIS_ORE || block == Material.REDSTONE_ORE || block == Material.DEEPSLATE_REDSTONE_ORE || block == Material.IRON_ORE || block == Material.DEEPSLATE_IRON_ORE || block == Material.GOLD_ORE || block == Material.DEEPSLATE_GOLD_ORE || block == Material.DIAMOND_ORE || block == Material.DEEPSLATE_DIAMOND_ORE || block == Material.EMERALD_ORE || block == Material.DEEPSLATE_EMERALD_ORE || block == Material.ANCIENT_DEBRIS || block == Material.NETHER_QUARTZ_ORE || block == Material.NETHER_GOLD_ORE;
    }

    boolean isFarmingBlock(Material block) {
        return block == Material.WHEAT || block == Material.CARROTS || block == Material.POTATOES || block == Material.BEETROOTS ||
                block == Material.MELON || block == Material.PUMPKIN || block == Material.SUGAR_CANE ||
                block == Material.COCOA || block == Material.NETHER_WART;
    }

    private int getExperienceForBlock(Material block) {
        switch (block) {
            case COAL_ORE:
                return 3;
            case DEEPSLATE_COAL_ORE:
                return 3;
            case COPPER_ORE:
                return 3;
            case DEEPSLATE_COPPER_ORE:
                return 3;
            case IRON_ORE:
                return 5;
            case DEEPSLATE_IRON_ORE:
                return 5;
            case NETHER_QUARTZ_ORE:
                return 8;
            case NETHER_GOLD_ORE:
                return 8;
            case GOLD_ORE:
                return 10;
            case DEEPSLATE_GOLD_ORE:
                return 10;
            case LAPIS_ORE:
                return 12;
            case DEEPSLATE_LAPIS_ORE:
                return 12;
            case REDSTONE_ORE:
                return 12;
            case DEEPSLATE_REDSTONE_ORE:
                return 12;
            case DIAMOND_ORE:
                return 30;
            case DEEPSLATE_DIAMOND_ORE:
                return 30;
            case EMERALD_ORE:
                return 50;
            case DEEPSLATE_EMERALD_ORE:
                return 50;
            case ANCIENT_DEBRIS:
                return 120;
            default:
                return 0;
        }
    }

    private int getExperienceForFarming(Material block, Block blockState) {
        if (block == Material.WHEAT || block == Material.CARROTS || block == Material.POTATOES || block == Material.BEETROOTS) {
            Ageable ageable = (Ageable) blockState.getBlockData();
            if (ageable.getAge() == ageable.getMaximumAge()) {
                return 3;
            }
        } else if (block == Material.MELON || block == Material.PUMPKIN) {
            return 5;
        } else if (block == Material.SUGAR_CANE) {
            return 1;
        } else if (block == Material.COCOA || block == Material.NETHER_WART) {
            Ageable ageable = (Ageable) blockState.getBlockData();
            if (ageable.getAge() == ageable.getMaximumAge()) {
                return 2;
            }
        }
        return 0;
    }

    private int getExperienceForFish(ItemStack fishItem) {
        if (fishItem == null || !fishItem.hasItemMeta() || !fishItem.getItemMeta().hasDisplayName()) {
            return 1;
        }

        String fishName = fishItem.getItemMeta().getDisplayName();

        if (fishName.contains("§7E§f등급")) {
            return 3;
        } else if (fishName.contains("§cF§f등급")) {
            return 5;
        } else if (fishName.contains("§aD§f등급")) {
            return 10;
        } else if (fishName.contains("§aC§f등급")) {
            return 12;
        } else if (fishName.contains("§bB§f등급")) {
            return 50;
        } else if (fishName.contains("§cA§f등급")) {
            return 120;
        } else if (fishName.contains("§c§lS§f등급")) {
            return 300;
        } else if (fishName.contains("§c§lSS§f§l등급")) {
            return 500;
        } else if (fishName.contains("§c§lSSS§f§l등급")) {
            return 1000;
        }
        return 1;
    }

    public boolean hasRequiredJob1(Player player, String requiredJob) {
        List<String> playerJobs = this.getPlayerJobs().get(player.getUniqueId().toString());
        return playerJobs != null && playerJobs.contains(requiredJob);
    }

    public boolean hasRequiredJob2(Player player, String requiredJob) {
        List<String> playerJobs = this.getPlayerJobs().get(player.getUniqueId().toString());
        return playerJobs != null && playerJobs.contains(requiredJob);
    }

    public boolean hasRequiredJob3(Player player, String requiredJob) {
        List<String> playerJobs = this.getPlayerJobs().get(player.getUniqueId().toString());
        return playerJobs != null && playerJobs.contains(requiredJob);
    }


    public void addExperience(Player player, int exp, String skill) {
        PlayerData playerData = playerManager.getPlayerData(player.getUniqueId());
        int previousLevel1 = playerData.getMiningLevel();
        int previousLevel2 = playerData.getFarmingLevel();
        int previousLevel3 = playerData.getFishingLevel();

        if (skill.equals("채광")) {
            int currentMiningLevel = playerData.getMiningLevel();

            if (currentMiningLevel >= 20 && currentMiningLevel < 40 && !hasRequiredJob1(player, "§7초보 광부")) {
                return;
            } else if (currentMiningLevel >= 40 && currentMiningLevel < 60 && !hasRequiredJob1(player, "§7견습 광부")) {
                return;
            } else if (currentMiningLevel >= 60 && currentMiningLevel < 80 && !hasRequiredJob1(player, "§7숙련 광부")) {
                return;
            } else if (currentMiningLevel >= 80 && currentMiningLevel < 100 && !hasRequiredJob1(player, "§7전문 광부")) {
                return;
            }

            if (currentMiningLevel < 100) {
                playerData.addMiningExperience(exp);

                while (playerData.getMiningExperience() >= playerData.getMiningExpToLevelUp() && currentMiningLevel < 100) {
                    playerData.addMiningExperience(-playerData.getMiningExpToLevelUp());
                    playerData.addMiningLevel(1);
                    currentMiningLevel = playerData.getMiningLevel();
                }

                if (playerData.getMiningLevel() > previousLevel1) {
                    player.sendMessage("§7[채광] §7레벨이 올랐습니다! §a( §f" + previousLevel1 + " §f-> " + playerData.getMiningLevel() + " §a)");
                }

                if (playerData.getMiningLevel() == 20 && !playerData.hasReachedLevel120()) {
                    player.sendMessage("§7채광 §a20 레벨에 도달하였습니다. §c더 이상 경험치를 획득 할 수 없습니다.");
                    player.sendMessage("§c직업의 서를 구매하여 레벨의 한계를 해금해야지 경험치를 획득할 수 있습니다.");
                    playerData.setReachedLevel120(true);
                }

                if (playerData.getMiningLevel() == 40 && !playerData.hasReachedLevel140()) {
                    player.sendMessage("§7채광 §a40 레벨에 도달하였습니다. §c더 이상 경험치를 획득 할 수 없습니다.");
                    player.sendMessage("§c직업의 서를 구매하여 레벨의 한계를 해금해야지 경험치를 획득할 수 있습니다.");
                    playerData.setReachedLevel140(true);
                }

                if (playerData.getMiningLevel() == 60 && !playerData.hasReachedLevel160()) {
                    player.sendMessage("§7채광 §a60 레벨에 도달하였습니다. §c더 이상 경험치를 획득 할 수 없습니다.");
                    player.sendMessage("§c직업의 서를 구매하여 레벨의 한계를 해금해야지 경험치를 획득할 수 있습니다.");
                    playerData.setReachedLevel160(true);
                }

                if (playerData.getMiningLevel() == 80 && !playerData.hasReachedLevel180()) {
                    player.sendMessage("§7채광 §a80 레벨에 도달하였습니다. §c더 이상 경험치를 획득 할 수 없습니다.");
                    player.sendMessage("§c직업의 서를 구매하여 레벨의 한계를 해금해야지 경험치를 획득할 수 있습니다.");
                    playerData.setReachedLevel180(true);
                }

                if (playerData.getMiningLevel() == 100 && !playerData.hasReachedLevel1100()) {
                    Bukkit.broadcastMessage("§c\uE022 §a" + player.getName() + "§6님께서 채광 100레벨을 달성했습니다!");
                    player.playSound(player.getLocation(), "minecraft:entity.player.levelup", 5.0f, 1.0f);
                    playerData.setReachedLevel1100(true);
                }
            }
        } else if (skill.equals("농사")) {
            int currentFarmingLevel = playerData.getFarmingLevel();

            if (currentFarmingLevel >= 20 && currentFarmingLevel < 40 && !hasRequiredJob2(player, "§6초보 농부")) {
                return;
            } else if (currentFarmingLevel >= 40 && currentFarmingLevel < 60 && !hasRequiredJob2(player, "§6견습 농부")) {
                return;
            } else if (currentFarmingLevel >= 60 && currentFarmingLevel < 80 && !hasRequiredJob2(player, "§6숙련 농부")) {
                return;
            } else if (currentFarmingLevel >= 80 && currentFarmingLevel < 100 && !hasRequiredJob2(player, "§6전문 농부")) {
                return;
            }

            if (playerData.getFarmingLevel() < 100) {
                playerData.addFarmingExperience(exp);

                while (playerData.getFarmingExperience() >= playerData.getFarmingExpToLevelUp() && playerData.getFarmingLevel() < 100) {
                    playerData.addFarmingExperience(-playerData.getFarmingExpToLevelUp());
                    playerData.addFarmingLevel(1);
                }

                if (playerData.getFarmingLevel() > previousLevel2) {
                    player.sendMessage("§a[농사] §7레벨이 올랐습니다! §a( §f" + previousLevel2 + " §f-> " + playerData.getFarmingLevel() + " §a)");
                    player.playSound(player.getLocation(), "minecraft:entity.player.levelup", 2.0f, 1.0f);
                }

                if (playerData.getFarmingLevel() == 20 && !playerData.hasReachedLevel220()) {
                    player.sendMessage("§6농부 §a20 레벨에 도달하였습니다. §c더 이상 경험치를 획득 할 수 없습니다.");
                    player.sendMessage("§c직업의 서를 구매하여 레벨의 한계를 해금해야지 경험치를 획득할 수 있습니다.");
                    playerData.setReachedLevel220(true);
                }

                if (playerData.getFarmingLevel() == 40 && !playerData.hasReachedLevel240()) {
                    player.sendMessage("§6농부 §a40 레벨에 도달하였습니다. §c더 이상 경험치를 획득 할 수 없습니다.");
                    player.sendMessage("§c직업의 서를 구매하여 레벨의 한계를 해금해야지 경험치를 획득할 수 있습니다.");
                    playerData.setReachedLevel240(true);
                }

                if (playerData.getFarmingLevel() == 60 && !playerData.hasReachedLevel260()) {
                    player.sendMessage("§6농부 §a60 레벨에 도달하였습니다. §c더 이상 경험치를 획득 할 수 없습니다.");
                    player.sendMessage("§c직업의 서를 구매하여 레벨의 한계를 해금해야지 경험치를 획득할 수 있습니다.");
                    playerData.setReachedLevel260(true);
                }

                if (playerData.getFarmingLevel() == 80 && !playerData.hasReachedLevel280()) {
                    player.sendMessage("§6농부 §a80 레벨에 도달하였습니다. §c더 이상 경험치를 획득 할 수 없습니다.");
                    player.sendMessage("§c직업의 서를 구매하여 레벨의 한계를 해금해야지 경험치를 획득할 수 있습니다.");
                    playerData.setReachedLevel280(true);
                }

                if (playerData.getFarmingLevel() == 100 && !playerData.hasReachedLevel2100()) {
                    Bukkit.broadcastMessage("§c\uE022 §a" + player.getName() + "§6님께서 농사 100레벨을 달성했습니다!");
                    player.playSound(player.getLocation(), "minecraft:entity.player.levelup", 5.0f, 1.0f);
                    playerData.setReachedLevel2100(true);
                }
            }
        } else if (skill.equals("낚시")) {
            int currentFishingLevel = playerData.getFishingLevel();

            if (currentFishingLevel >= 20 && currentFishingLevel < 40 && !hasRequiredJob3(player, "§b초보 어부")) {
                return;
            } else if (currentFishingLevel >= 40 && currentFishingLevel < 60 && !hasRequiredJob3(player, "§b견습 어부")) {
                return;
            } else if (currentFishingLevel >= 60 && currentFishingLevel < 80 && !hasRequiredJob3(player, "§b숙련 어부")) {
                return;
            } else if (currentFishingLevel >= 80 && currentFishingLevel < 100 && !hasRequiredJob3(player, "§b전문 어부")) {
                return;
            }
            if (playerData.getFishingLevel() < 100) {
                playerData.addFishingExperience(exp);

                while (playerData.getFishingExperience() >= playerData.getFishingExpToLevelUp() && playerData.getFishingLevel() < 100) {
                    playerData.addFishingExperience(-playerData.getFishingExpToLevelUp());
                    playerData.addFishingLevel(1);
                }

                if (playerData.getFishingLevel() > previousLevel3) {
                    player.sendMessage("§b[낚시] §7레벨이 올랐습니다! §a( §f" + previousLevel3 + " §f-> " + playerData.getFishingLevel() + " §a)");
                    player.playSound(player.getLocation(), "minecraft:entity.player.levelup", 2.0f, 1.0f);
                }

                if (playerData.getFishingLevel() == 20 && !playerData.hasReachedLevel320()) {
                    player.sendMessage("§b어부 §a20 레벨에 도달하였습니다. §c더 이상 경험치를 획득 할 수 없습니다.");
                    player.sendMessage("§c직업의 서를 구매하여 레벨의 한계를 해금해야지 경험치를 획득할 수 있습니다.");
                    playerData.setReachedLevel320(true);
                }

                if (playerData.getFishingLevel() == 40 && !playerData.hasReachedLevel340()) {
                    player.sendMessage("§b어부 §a40 레벨에 도달하였습니다. §c더 이상 경험치를 획득 할 수 없습니다.");
                    player.sendMessage("§c직업의 서를 구매하여 레벨의 한계를 해금해야지 경험치를 획득할 수 있습니다.");
                    playerData.setReachedLevel340(true);
                }

                if (playerData.getFishingLevel() == 60 && !playerData.hasReachedLevel360()) {
                    player.sendMessage("§b어부 §a60 레벨에 도달하였습니다. §c더 이상 경험치를 획득 할 수 없습니다.");
                    player.sendMessage("§c직업의 서를 구매하여 레벨의 한계를 해금해야지 경험치를 획득할 수 있습니다.");
                    playerData.setReachedLevel360(true);
                }

                if (playerData.getFishingLevel() == 80 && !playerData.hasReachedLevel380()) {
                    player.sendMessage("§b어부 §a80 레벨에 도달하였습니다. §c더 이상 경험치를 획득 할 수 없습니다.");
                    player.sendMessage("§c직업의 서를 구매하여 레벨의 한계를 해금해야지 경험치를 획득할 수 있습니다.");
                    playerData.setReachedLevel380(true);
                }

                if (playerData.getFishingLevel() == 100 && !playerData.hasReachedLevel3100()) {
                    Bukkit.broadcastMessage("§c\uE022 §a" + player.getName() + "§6님께서 어부 100레벨을 달성했습니다!");
                    player.playSound(player.getLocation(), "minecraft:entity.player.levelup", 5.0f, 1.0f);
                    playerData.setReachedLevel3100(true);
                }
            }
        }
        updateBossBar(player, skill);
    }

    public void updateBossBar(Player player, String skill) {
        UUID playerId = player.getUniqueId();
        PlayerData playerData = playerManager.getPlayerData(playerId);

        BossBar bossBar = null;

        if (skill.equals("채광")) {
            bossBar = miningBossBars.get(playerId);
            if (bossBar == null) {
                bossBar = Bukkit.createBossBar("채광 경험치", BarColor.GREEN, BarStyle.SOLID);
                miningBossBars.put(playerId, bossBar);
            }
            double progress = (double) playerData.getMiningExperience() / playerData.getMiningExpToLevelUp();
            bossBar.setProgress(Math.min(progress, 1.0));
            bossBar.setTitle("§7[채광] §f레벨 §a" + playerData.getMiningLevel() + " §f경험치 [" + playerData.getMiningExperience() + "/" + playerData.getMiningExpToLevelUp() + "]");
        } else if (skill.equals("농사")) {
            bossBar = farmingBossBars.get(playerId);
            if (bossBar == null) {
                bossBar = Bukkit.createBossBar("농사 경험치", BarColor.GREEN, BarStyle.SOLID);
                farmingBossBars.put(playerId, bossBar);
            }
            double progress = (double) playerData.getFarmingExperience() / playerData.getFarmingExpToLevelUp();
            bossBar.setProgress(Math.min(progress, 1.0));
            bossBar.setTitle("§a[농사] §f레벨 §a" + playerData.getFarmingLevel() + " §f경험치 [" + playerData.getFarmingExperience() + "/" + playerData.getFarmingExpToLevelUp() + "]");
        } else if (skill.equals("낚시")) {
            bossBar = fishingBossBars.get(playerId);
            if (bossBar == null) {
                bossBar = Bukkit.createBossBar("낚시 경험치", BarColor.GREEN, BarStyle.SOLID);
                fishingBossBars.put(playerId, bossBar);
            }
            double progress = (double) playerData.getFishingExperience() / playerData.getFishingExpToLevelUp();
            bossBar.setProgress(Math.min(progress, 1.0));
            bossBar.setTitle("§b[낚시] §f레벨 §a" + playerData.getFishingLevel() + " §f경험치 [" + playerData.getFishingExperience() + "/" + playerData.getFishingExpToLevelUp() + "]");
        }

        bossBar.addPlayer(player);
        scheduleBossBarRemoval(playerId, skill);
    }

    private void scheduleBossBarRemoval(final UUID playerId, final String skill) {
        new BukkitRunnable() {
            @Override
            public void run() {
                BossBar bossBar = null;

                if (skill.equals("채광")) {
                    bossBar = miningBossBars.get(playerId);
                } else if (skill.equals("농사")) {
                    bossBar = farmingBossBars.get(playerId);
                } else if (skill.equals("낚시")) {
                    bossBar = fishingBossBars.get(playerId);
                }

                if (bossBar != null) {
                    bossBar.removeAll();
                    if (skill.equals("채광")) {
                        miningBossBars.remove(playerId);
                    } else if (skill.equals("농사")) {
                        farmingBossBars.remove(playerId);
                    } else if (skill.equals("낚시")) {
                        fishingBossBars.remove(playerId);
                    }
                }
            }
        }.runTaskLater(this, 200L);
    }

    private void processMiningCommand(String action, Player player, PlayerData playerData, int amount) {
        switch (action) {
            case "레벨추가":
                if (playerData.getMiningLevel() >= 100) {
                    player.sendMessage("§7[채광] §c최대 레벨입니다! 더 이상 레벨을 추가할 수 없습니다.");
                    return;
                }
                playerData.addMiningLevel(amount);
                player.sendMessage("§7[채광] §f레벨이 " + amount + "만큼 추가되었습니다. 현재 레벨: " + playerData.getMiningLevel());
                break;
            case "레벨삭제":
                if (playerData.getMiningLevel() <= 0) {
                    player.sendMessage("§7[채광] §c최소 레벨입니다! 더 이상 레벨을 삭제할 수 없습니다.");
                    return;
                }
                playerData.removeMiningLevel(amount);
                player.sendMessage("§7[채광] §f레벨이 " + amount + "만큼 차감되었습니다. 현재 레벨: " + playerData.getMiningLevel());
                break;
            case "경험치추가":
                if (playerData.getMiningLevel() >= 100) {
                    player.sendMessage("§7[채광] §c이미 최대 레벨입니다! 더 이상 경험치를 추가할 수 없습니다.");
                    return;
                }
                playerData.addMiningExperience(amount);
                player.sendMessage("§7[채광] §f경험치가 " + amount + "만큼 추가되었습니다.");
                break;
            case "경험치삭제":
                if (playerData.getMiningExperience() - amount < 0) {
                    player.sendMessage("§7[채광] §c경험치를 초과하였습니다! 더 이상 경험치를 삭제할 수 없습니다.");
                    return;
                }
                playerData.addMiningExperience(-amount);
                player.sendMessage("§7[채광] §f경험치가 " + amount + "만큼 차감되었습니다.");
                break;
            default:
                player.sendMessage("§c§l[!] 올바른 명령어를 입력해주세요 (레벨추가, 레벨삭제, 경험치추가, 경험치삭제).");
                break;
        }
        updateBossBar(player, "채광");
    }

    private void processFarmingCommand(String action, Player player, PlayerData playerData, int amount) {
        switch (action) {
            case "레벨추가":
                if (playerData.getFarmingLevel() >= 100) {
                    player.sendMessage("§a[농사] §c최대 레벨입니다! 더 이상 레벨을 추가할 수 없습니다.");
                    return;
                }
                playerData.addFarmingLevel(amount);
                player.sendMessage("§a[농사] §f레벨이 " + amount + "만큼 추가되었습니다. 현재 레벨: " + playerData.getFarmingLevel());
                break;
            case "레벨삭제":
                if (playerData.getFarmingLevel() <= 0) {
                    player.sendMessage("§a[농사] §c최소 레벨입니다! 더 이상 레벨을 삭제할 수 없습니다.");
                    return;
                }
                playerData.removeFarmingLevel(amount);
                player.sendMessage("§a[농사] §f레벨이 " + amount + "만큼 차감되었습니다. 현재 레벨: " + playerData.getFarmingLevel());
                break;
            case "경험치추가":
                if (playerData.getFarmingLevel() >= 100) {
                    player.sendMessage("§a[농사] §c이미 최대 레벨입니다! 더 이상 경험치를 추가할 수 없습니다.");
                    return;
                }
                playerData.addFarmingExperience(amount);
                player.sendMessage("§a[농사] §f경험치가 " + amount + "만큼 추가되었습니다.");
                break;
            case "경험치삭제":
                if (playerData.getFarmingExperience() - amount < 0) {
                    player.sendMessage("§a[농사] §c경험치를 초과하였습니다! 더 이상 경험치를 삭제할 수 없습니다.");
                    return;
                }
                playerData.addFarmingExperience(-amount);
                player.sendMessage("§a[농사] §f경험치가 " + amount + "만큼 차감되었습니다.");
                break;
            default:
                player.sendMessage("§c§l[!] 올바른 명령어를 입력해주세요 (레벨추가, 레벨삭제, 경험치추가, 경험치삭제).");
                break;
        }
        updateBossBar(player, "농사");
    }

    private void processFishingCommand(String action, Player player, PlayerData playerData, int amount) {
        switch (action) {
            case "레벨추가":
                if (playerData.getFishingLevel() >= 100) {
                    player.sendMessage("§b[낚시] §c최대 레벨입니다! 더 이상 레벨을 추가할 수 없습니다.");
                    return;
                }
                playerData.addFishingLevel(amount);
                player.sendMessage("§b[낚시] §f레벨이 " + amount + "만큼 추가되었습니다. 현재 레벨: " + playerData.getFishingLevel());
                break;
            case "레벨삭제":
                if (playerData.getFishingLevel() <= 0) {
                    player.sendMessage("§b[낚시] §c최소 레벨입니다! 더 이상 레벨을 삭제할 수 없습니다.");
                    return;
                }
                playerData.removeFishingLevel(amount);
                player.sendMessage("§b[낚시] §f레벨이 " + amount + "만큼 차감되었습니다. 현재 레벨: " + playerData.getFishingLevel());
                break;
            case "경험치추가":
                if (playerData.getFishingLevel() >= 100) {
                    player.sendMessage("§b[낚시] §c이미 최대 레벨입니다! 더 이상 경험치를 추가할 수 없습니다.");
                    return;
                }
                playerData.addFishingExperience(amount);
                player.sendMessage("§b[낚시] §f경험치가 " + amount + "만큼 추가되었습니다.");
                break;
            case "경험치삭제":
                if (playerData.getFishingExperience() - amount < 0) {
                    player.sendMessage("§b[낚시] §c경험치를 초과하였습니다! 더 이상 경험치를 삭제할 수 없습니다.");
                    return;
                }
                playerData.addFishingExperience(-amount);
                player.sendMessage("§b[낚시] §f경험치가 " + amount + "만큼 차감되었습니다.");
                break;
            default:
                player.sendMessage("§c§l[!] 올바른 명령어를 입력해주세요 (레벨추가, 레벨삭제, 경험치추가, 경험치삭제).");
                break;
        }
        updateBossBar(player, "낚시");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("lifelevelup.command.level")) {
                player.sendMessage("§c§l[!] 이 명령어를 사용할 권한이 없습니다.");
                return true;
            }
        }

        if (command.getName().equalsIgnoreCase("레벨")) {

            if (args.length < 4) {
                sender.sendMessage("");
                sender.sendMessage("§8/레벨");
                sender.sendMessage("§8ㄴ §7[닉네임] [확인] 0 0");
                sender.sendMessage("§8- §f해당 플레이어에 레벨을 확인한다.");
                sender.sendMessage("");
                sender.sendMessage("§8ㄴ §7[닉네임] [채광] [레벨추가 | 레벨삭제 | 경험치추가 | 경험치삭제] [숫자]");
                sender.sendMessage("§8- §f해당 플레이어에게 광부 레벨, 경험치를 추가 또는 삭제한다.");
                sender.sendMessage("");
                sender.sendMessage("§8ㄴ §7[닉네임] [농사] [레벨추가 | 레벨삭제 | 경험치추가 | 경험치삭제] [숫자]");
                sender.sendMessage("§8- §f해당 플레이어에게 농사 레벨, 경험치를 추가 또는 삭제한다.");
                sender.sendMessage("");
                sender.sendMessage("§8ㄴ §7[닉네임] [낚시] [레벨추가 | 레벨삭제 | 경험치추가 | 경험치삭제] [숫자]");
                sender.sendMessage("§8- §f해당 플레이어에게 낚시 레벨, 경험치를 추가 또는 삭제한다.");
                sender.sendMessage("");
                return true;
            }

            String playerName = args[0];
            String skill = args[1].toLowerCase();
            String action = args[2].toLowerCase();
            int amount;

            Player targetPlayer = Bukkit.getPlayer(playerName);
            if (targetPlayer == null) {
                sender.sendMessage("§c§l[!] 플레이어 " + playerName + "를 찾을 수 없습니다: ");
                return true;
            }

            PlayerData playerData = playerManager.getPlayerData(targetPlayer.getUniqueId());

            if (args.length >= 2 && args[1].equalsIgnoreCase("확인")) {
                int miningLevel = playerData.getMiningLevel();
                int miningExp = playerData.getMiningExperience();
                int farmingLevel = playerData.getFarmingLevel();
                int farmingExp = playerData.getFarmingExperience();
                int fishingLevel = playerData.getFishingLevel();
                int fishingExp = playerData.getFishingExperience();

                sender.sendMessage("");
                sender.sendMessage("§8[ §7" + targetPlayer.getName() + "§6 생활 레벨 §8]");
                sender.sendMessage("§8ㄴ §7채광§f: §a레벨 §f" + miningLevel + " §e│ 경험치 §f" + miningExp + " EXP");
                sender.sendMessage("§8ㄴ §a농사§f: §a레벨 §f" + farmingLevel + " §e│ 경험치 §f" + farmingExp + " EXP");
                sender.sendMessage("§8ㄴ §b낚시§f: §a레벨 §f" + fishingLevel + " §e│ 경험치 §f" + fishingExp + " EXP");
                sender.sendMessage("");
                return true;
            }

            try {
                amount = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§c§l[!] 숫자 값을 입력해주세요.");
                return true;
            }

            switch (skill) {
                case "채광":
                    processMiningCommand(action, targetPlayer, playerData, amount);
                    break;
                case "농사":
                    processFarmingCommand(action, targetPlayer, playerData, amount);
                    break;
                case "낚시":
                    processFishingCommand(action, targetPlayer, playerData, amount);
                    break;
                default:
                    sender.sendMessage("§c§l[!] 올바른 직업 (채광/농사/낚시)을 입력해주세요.");
                    return true;
            }
            return true;
        }
        return false;
    }

    private void loadJobsData() {
        jobsFile = new File(getDataFolder(), "jobs.yml");

        if (!jobsFile.exists()) {
            jobsFile.getParentFile().mkdirs();
            try {
                jobsFile.createNewFile();
            } catch (IOException e) {
                getLogger().severe("jobs.yml 파일을 생성하는 도중 오류가 발생했습니다.");
                e.printStackTrace();
            }
        }

        jobsConfig = YamlConfiguration.loadConfiguration(jobsFile);

        for (String playerUUID : jobsConfig.getKeys(false)) {
            playerJobs.put(playerUUID, jobsConfig.getStringList(playerUUID));
        }
    }

    public void saveJobsData() {
        for (Map.Entry<String, List<String>> entry : playerJobs.entrySet()) {
            jobsConfig.set(entry.getKey(), entry.getValue());
        }

        try {
            jobsConfig.save(jobsFile);
        } catch (IOException e) {
            getLogger().severe("jobs.yml 파일 저장 중 오류 발생: " + e.getMessage());
        }
    }

    public Map<String, List<String>> getPlayerJobs() {
        return playerJobs;
    }

    public FileConfiguration getJobsConfig() {
        return jobsConfig;
    }
}