package spiritstats.spiritstats.level;

public class PlayerLevelData {

    private int level = 1;
    private int exp = 0;
    private double levelHpBonus;

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public void setLevel(int v) {
        level = Math.max(1, Math.min(70, v));
    }

    public void addLevel(int v) {
        setLevel(level + v);
    }

    public void setExp(int v) {
        exp = Math.max(0, v);
    }

    public void addExp(int v) {
        exp += v;
        if (exp < 0) exp = 0;
    }

    public double getLevelHpBonus() {
        return levelHpBonus;
    }

    public void addLevelHpBonus(double amount) {
        this.levelHpBonus += amount;
    }
}