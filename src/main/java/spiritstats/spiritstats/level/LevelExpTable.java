package spiritstats.spiritstats.level;

public class LevelExpTable {

    private static final int[] LEVEL_XP = new int[] {
            // 1 ~ 10
            50, 75, 100, 130, 170, 220, 280, 350, 430, 500,

            // 11 ~ 20
            700, 950, 1250, 1600, 2000, 2400, 2800, 3200, 3600, 4000,

            // 21 ~ 30
            4400, 4800, 5200, 5600, 6000, 6500, 7000, 7600, 8200, 8800,

            // 31 ~ 40
            9500, 10200, 11000, 11800, 12700, 13600, 14600, 15600, 16700, 18000,

            // 41 ~ 50
            19500, 21000, 23000, 25000, 27000, 29000, 31000, 33000, 35000, 38000,

            // 51 ~ 60
            41000, 44000, 47000, 50000, 54000, 58000, 62000, 66000, 70000, 75000,

            // 61 ~ 70
            80000, 86000, 92000, 98000, 105000, 112000, 120000, 128000, 136000
    };

    public static int getRequiredExp(int level) {
        if (level < 1 || level > 70) {
            throw new IllegalArgumentException("Level must be between 1 and 70");
        }
        return LEVEL_XP[level - 1];
    }
}