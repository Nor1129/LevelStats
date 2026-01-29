package spiritstats.spiritstats.stat;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

public class StatGUI implements Listener {

    public static void open(Player p) {
        PlayerStatData d = StatManager.get(p);

        Inventory inv = Bukkit.createInventory(null, 36, "§8[스탯]");

        inv.setItem(10, item(Material.SHIELD, "§b공명", d.getResonance()));
        inv.setItem(12, item(Material.FEATHER, "§a흐름", d.getFlow()));
        inv.setItem(14, item(Material.IRON_SWORD, "§c공격 문양", d.getAttackGlyph()));
        inv.setItem(16, item(Material.IRON_CHESTPLATE, "§9방어 문양", d.getDefenseGlyph()));
        inv.setItem(31, item(Material.EXPERIENCE_BOTTLE, "§e남은 포인트", d.getStatPoint()));

        p.openInventory(inv);
    }

    private static ItemStack item(Material m, String name, int level) {
        ItemStack i = new ItemStack(m);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(name + " §7[" + level + "]");
        i.setItemMeta(meta);
        return i;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals("§8[스탯]")) return;
        if (e.getClickedInventory() == null) return;
        if (!e.getClickedInventory().equals(e.getView().getTopInventory())) return;

        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        PlayerStatData d = StatManager.get(p);
        int slot = e.getSlot();

        if (slot != 10 && slot != 12 && slot != 14 && slot != 16) return;

        boolean canIncrease = switch (slot) {
            case 10 -> d.canAddResonance();
            case 12 -> d.canAddFlow();
            case 14 -> d.canAddAttackGlyph();
            case 16 -> d.canAddDefenseGlyph();
            default -> false;
        };

        if (!canIncrease) {
            p.sendMessage("§c이미 해당 스탯은 최대치입니다. (150)");
            return;
        }

        if (!d.usePoint()) {
            p.sendMessage("§c스탯 포인트가 부족합니다.");
            return;
        }

        switch (slot) {
            case 10 -> d.addResonance();
            case 12 -> d.addFlow();
            case 14 -> d.addAttackGlyph();
            case 16 -> d.addDefenseGlyph();
        }
        StatApplier.apply(p);
        StatManager.save(p);
        StatGUI.open(p);
    }
}
