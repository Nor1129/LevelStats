package jobslevelpl.jobslevelpl;

import java.util.UUID;

public class PlayerData {
    private final UUID playerId;
    private int miningLevel;
    private int farmingLevel;
    private int fishingLevel;
    private int miningExperience;
    private int farmingExperience;
    private int fishingExperience;
    private boolean reachedLevel120;
    private boolean reachedLevel140;
    private boolean reachedLevel160;
    private boolean reachedLevel180;
    private boolean reachedLevel1100;
    private boolean reachedLevel220;
    private boolean reachedLevel240;
    private boolean reachedLevel260;
    private boolean reachedLevel280;
    private boolean reachedLevel2100;
    private boolean reachedLevel320;
    private boolean reachedLevel340;
    private boolean reachedLevel360;
    private boolean reachedLevel380;
    private boolean reachedLevel3100;

    private final int[] expToLevelUp = {
            50, 100, 150, 225, 300, 375, 450, 525, 600, 675,  // 1-10 레벨
            750, 825, 900, 975, 1050, 1125, 1200, 1275, 1350, 1425,  // 11-20 레벨
            2000, 2100, 2200, 2300, 2400, 2500, 2600, 2700, 2800, 2900,  // 21-30 레벨
            4000, 4125, 4250, 4375, 4500, 4625, 4750, 4875, 5000, 5125,  // 31-40 레벨
            6000, 6150, 6300, 6450, 6600, 6750, 6900, 7050, 7200, 7350,  // 41-50 레벨
            8000, 8175, 8350, 8525, 8700, 8875, 9050, 9225, 9400, 9575,  // 51-60 레벨
            10000, 10200, 10400, 10600, 10800, 11000, 11200, 11400, 11600, 11800,  // 61-70 레벨
            15000, 16000, 17000, 18000, 19000, 20000, 22000, 24000, 26000, 28000,  // 71-80 레벨
            30000, 33000, 36000, 39000, 42000, 45000, 48000, 51000, 54000, 57000,  // 81-90 레벨
            60000, 64000, 68000, 72000, 76000, 80000, 84000, 88000, 92000, 100000  // 91-100 레벨
    };


    public PlayerData(UUID playerId) {
        this.playerId = playerId;
        this.miningLevel = 0;
        this.farmingLevel = 0;
        this.fishingLevel = 0;
        this.miningExperience = 0;
        this.farmingExperience = 0;
        this.fishingExperience = 0;
        this.reachedLevel120 = false;
        this.reachedLevel140 = false;
        this.reachedLevel160 = false;
        this.reachedLevel180 = false;
        this.reachedLevel1100 = false;
        this.reachedLevel220 = false;
        this.reachedLevel240 = false;
        this.reachedLevel260 = false;
        this.reachedLevel280 = false;
        this.reachedLevel2100 = false;
        this.reachedLevel320 = false;
        this.reachedLevel340 = false;
        this.reachedLevel360 = false;
        this.reachedLevel380 = false;
        this.reachedLevel3100 = false;
    }

    public boolean hasReachedLevel120() {
        return reachedLevel120;
    }

    public void setReachedLevel120(boolean reachedLevel120) {
        this.reachedLevel120 = reachedLevel120;
    }

    public boolean hasReachedLevel140() {
        return reachedLevel140;
    }

    public void setReachedLevel140(boolean reachedLevel140) {
        this.reachedLevel140 = reachedLevel140;
    }

    public boolean hasReachedLevel160() {
        return reachedLevel160;
    }

    public void setReachedLevel160(boolean reachedLevel160) {
        this.reachedLevel160 = reachedLevel160;
    }

    public boolean hasReachedLevel180() {
        return reachedLevel180;
    }

    public void setReachedLevel180(boolean reachedLevel180) {
        this.reachedLevel180 = reachedLevel180;
    }

    public boolean hasReachedLevel1100() {
        return reachedLevel1100;
    }

    public void setReachedLevel1100(boolean reachedLevel1100) {
        this.reachedLevel1100 = reachedLevel1100;
    }

    public boolean hasReachedLevel220() {
        return reachedLevel220;
    }

    public void setReachedLevel220(boolean reachedLevel220) {
        this.reachedLevel220 = reachedLevel220;
    }

    public boolean hasReachedLevel240() {
        return reachedLevel240;
    }

    public void setReachedLevel240(boolean reachedLevel240) {
        this.reachedLevel240 = reachedLevel240;
    }

    public boolean hasReachedLevel260() {
        return reachedLevel260;
    }

    public void setReachedLevel260(boolean reachedLevel260) {
        this.reachedLevel260 = reachedLevel260;
    }

    public boolean hasReachedLevel280() {
        return reachedLevel280;
    }

    public void setReachedLevel280(boolean reachedLevel280) {
        this.reachedLevel280 = reachedLevel280;
    }

    public boolean hasReachedLevel2100() {
        return reachedLevel2100;
    }

    public void setReachedLevel2100(boolean reachedLevel2100) {
        this.reachedLevel2100 = reachedLevel2100;
    }

    public boolean hasReachedLevel320() {
        return reachedLevel320;
    }

    public void setReachedLevel320(boolean reachedLevel320) {
        this.reachedLevel320 = reachedLevel320;
    }

    public boolean hasReachedLevel340() {
        return reachedLevel340;
    }

    public void setReachedLevel340(boolean reachedLevel340) {
        this.reachedLevel340 = reachedLevel340;
    }

    public boolean hasReachedLevel360() {
        return reachedLevel360;
    }

    public void setReachedLevel360(boolean reachedLevel360) {
        this.reachedLevel360 = reachedLevel360;
    }

    public boolean hasReachedLevel380() {
        return reachedLevel380;
    }

    public void setReachedLevel380(boolean reachedLevel380) {
        this.reachedLevel380 = reachedLevel380;
    }

    public boolean hasReachedLevel3100() {
        return reachedLevel3100;
    }

    public void setReachedLevel3100(boolean reachedLevel3100) {
        this.reachedLevel3100 = reachedLevel3100;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public int getMiningLevel() {
        return miningLevel;
    }

    public int getMiningExperience() {
        return miningExperience;
    }

    public int getMiningExpToLevelUp() {
        return expToLevelUp[Math.min(miningLevel, expToLevelUp.length - 1)];
    }

    public void addMiningExperience(int exp) {
        miningExperience = Math.max(miningExperience + exp, 0);
        while (miningExperience >= getMiningExpToLevelUp() && miningLevel < 100) {
            miningExperience -= getMiningExpToLevelUp();
            miningLevel++;
        }
    }

    public void addMiningLevel(int level) {
        miningLevel = Math.min(miningLevel + level, 100);
    }

    public void removeMiningLevel(int level) {
        miningLevel = Math.max(miningLevel - level, 0);
    }

    public int getFarmingLevel() {
        return farmingLevel;
    }

    public int getFarmingExperience() {
        return farmingExperience;
    }

    public int getFarmingExpToLevelUp() {
        return expToLevelUp[Math.min(farmingLevel, expToLevelUp.length - 1)];
    }

    public void addFarmingExperience(int exp) {
        farmingExperience = Math.max(farmingExperience + exp, 0);
        while (farmingExperience >= getFarmingExpToLevelUp() && farmingLevel < 100) {
            farmingExperience -= getFarmingExpToLevelUp();
            farmingLevel++;
        }
    }

    public void addFarmingLevel(int level) {
        farmingLevel = Math.min(farmingLevel + level, 100);
    }

    public void removeFarmingLevel(int level) {
        farmingLevel = Math.max(farmingLevel - level, 0);
    }

    public int getFishingLevel() {
        return fishingLevel;
    }

    public int getFishingExperience() {
        return fishingExperience;
    }

    public int getFishingExpToLevelUp() {
        return expToLevelUp[Math.min(fishingLevel, expToLevelUp.length - 1)];
    }

    public void addFishingExperience(int exp) {
        fishingExperience = Math.max(fishingExperience + exp, 0);
        while (fishingExperience >= getFishingExpToLevelUp() && fishingLevel < 100) {
            fishingExperience -= getFishingExpToLevelUp();
            fishingLevel++;
        }
    }

    public void addFishingLevel(int level) {
        fishingLevel = Math.min(fishingLevel + level, 100);
    }

    public void removeFishingLevel(int level) {
        fishingLevel = Math.max(fishingLevel - level, 0);
    }
}