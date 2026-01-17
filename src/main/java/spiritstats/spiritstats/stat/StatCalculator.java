package spiritstats.spiritstats.stat;

public class StatCalculator {

    public static double bonusHealth(PlayerStatData d) {
        return (d.getResonance() * 1.0)
                + (d.getFlow() * 0.8);
    }

    public static double attack(PlayerStatData d) {
        return d.getFlow() * 0.15;
    }

    public static double defense(PlayerStatData d) {
        return d.getResonance() * 0.2;
    }

    public static double damageIncrease(PlayerStatData d) {
        return d.getAttackGlyph() * 0.003;
    }

    public static double damageReduction(PlayerStatData d) {
        return d.getDefenseGlyph() * 0.002;
    }
}