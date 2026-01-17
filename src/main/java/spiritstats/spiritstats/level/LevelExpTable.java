package spiritstats.spiritstats.level;

public class LevelExpTable {

    public static int getRequiredExp(int level) {

        if (level <= 10) {
            return level * 80;
        }

        if (level <= 30) {
            return (int) (level * 150 + 500);
        }

        if (level <= 50) {
            return (int) (level * 250 + 2000);
        }

        return (int) (level * 400 + 5000);
    }
}
