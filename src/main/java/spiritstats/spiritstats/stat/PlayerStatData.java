package spiritstats.spiritstats.stat;

public class PlayerStatData {

    private int resonance;
    private int flow;
    private int attackGlyph;
    private int defenseGlyph;
    private int statPoint;

    public int getResonance() { return resonance; }
    public int getFlow() { return flow; }
    public int getAttackGlyph() { return attackGlyph; }
    public int getDefenseGlyph() { return defenseGlyph; }
    public int getStatPoint() { return statPoint; }

    public void setResonance(int v) { resonance = Math.min(150, Math.max(0, v)); }
    public void setFlow(int v) { flow = Math.min(150, Math.max(0, v)); }
    public void setAttackGlyph(int v) { attackGlyph = Math.min(150, Math.max(0, v)); }
    public void setDefenseGlyph(int v) { defenseGlyph = Math.min(150, Math.max(0, v)); }

    public void addResonance() { setResonance(resonance + 1); }
    public void addFlow() { setFlow(flow + 1); }
    public void addAttackGlyph() { setAttackGlyph(attackGlyph + 1); }
    public void addDefenseGlyph() { setDefenseGlyph(defenseGlyph + 1); }
    public void addPoint(int v) { statPoint = Math.max(0, statPoint + v); }

    public void removeResonance(int v) { setResonance(resonance - v); }
    public void removeFlow(int v) { setFlow(flow - v); }
    public void removeAttackGlyph(int v) { setAttackGlyph(attackGlyph - v); }
    public void removeDefenseGlyph(int v) { setDefenseGlyph(defenseGlyph - v); }

    public boolean canAddResonance() {
        return resonance < 150;
    }

    public boolean canAddFlow() {
        return flow < 150;
    }

    public boolean canAddAttackGlyph() {
        return attackGlyph < 150;
    }

    public boolean canAddDefenseGlyph() {
        return defenseGlyph < 150;
    }

    public boolean usePoint() {
        if (statPoint <= 0) return false;
        statPoint--;
        return true;
    }
}