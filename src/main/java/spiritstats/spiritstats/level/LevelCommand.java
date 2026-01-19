package spiritstats.spiritstats.level;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import spiritstats.spiritstats.stat.StatApplier;

import java.text.DecimalFormat;

public class LevelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player p)) return true;


        if (args.length == 0) {
            PlayerLevelData d = LevelManager.get(p);
            DecimalFormat df = new DecimalFormat("#,###");
            String getExp = df.format(d.getExp());
            p.sendMessage("");
            p.sendMessage("§c[ 전투 레벨 정보 ]");
            p.sendMessage("§e전투레벨: §f" + d.getLevel());
            p.sendMessage("§7전투경험치: §f" + getExp);
            p.sendMessage("");
            return true;
        }

        if (!p.hasPermission("spiritstats.admin")) {
            p.sendMessage("§c§l[!] 레벨 권한이 없습니다.");
            return true;
        }

        String sub = args[0];

        if (sub.equals("정보")) {
            if (args.length < 2) {
                p.sendMessage("§c§l[!] /전투레벨 정보 [닉네임]");
                return true;
            }

            Player target = p.getServer().getPlayer(args[1]);
            if (target == null) {
                p.sendMessage("§c§l[!] 플레이어를 찾을 수 없습니다.");
                return true;
            }

            PlayerLevelData d = LevelManager.get(target);
            DecimalFormat df = new DecimalFormat("#,###");
            String getExp = df.format(d.getExp());
            p.sendMessage("");
            p.sendMessage("§c[ " + target.getName() + " 전투 레벨 정보 ]");
            p.sendMessage("§e전투레벨: §f" + d.getLevel());
            p.sendMessage("§7전투경험치: §f" + getExp);
            p.sendMessage("");
            return true;
        }

        if (sub.equals("리로드")) {
            LevelManager.reloadAll();
            p.sendMessage("§a§l[!] 전투레벨 데이터를 리로드했습니다.");
            return true;
        }

        if (args.length < 3) {
            p.sendMessage("§8/전투레벨");
            p.sendMessage("§8ㄴ §7[정보] [닉네임]");
            p.sendMessage("§8- §f해당 플레이어에게 전투 정보를 보여준다.");
            p.sendMessage("");
            p.sendMessage("§8ㄴ §7[전투레벨추가] [닉네임] [수치]");
            p.sendMessage("§8- §f해당 플레이어에게 전투 레벨을 추가한다.");
            p.sendMessage("");
            p.sendMessage("§8ㄴ §7[전투레벨차감] [닉네임] [수치]");
            p.sendMessage("§8- §f해당 플레이어에게 전투 레벨을 차감한다.");
            p.sendMessage("");
            p.sendMessage("§8ㄴ §7[전투경험치추가] [닉네임] [수치]");
            p.sendMessage("§8- §f해당 플레이어에게 전투 경험치를 추가한다.");
            p.sendMessage("");
            p.sendMessage("§8ㄴ §7[전투경험치차감] [닉네임] [수치]");
            p.sendMessage("§8- §f해당 플레이어에게 전투 경험치를 차감한다.");
            return true;
        }

        String targetName = args[1];
        Player target = p.getServer().getPlayer(targetName);
        if (target == null) {
            p.sendMessage("§c§l[!] 플레이어를 찾을 수 없습니다.");
            return true;
        }

        int value;
        try {
            value = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            p.sendMessage("§c§l[!] 수치는 숫자여야 합니다.");
            return true;
        }

        PlayerLevelData d = LevelManager.get(target);

        switch (sub) {
            case "전투레벨추가" -> {
                d.addLevel(value);
                LevelSystem.checkLevelUp(target);

                d.addLevelHpBonus(value * LevelManager.HP_PER_LEVEL);

                StatApplier.apply(target);
                LevelManager.save(target);

                p.sendMessage("§a§l[!] " + target.getName() + " 전투레벨 +" + value + " 만큼 추가되었습니다!");
            }

            case "전투레벨차감" -> {
                d.addLevel(-value);
                d.addLevelHpBonus(-value * LevelManager.HP_PER_LEVEL);

                StatApplier.apply(target);
                LevelManager.save(target);
                p.sendMessage("§a§l[!] " + target.getName() + " 전투레벨 -" + value + " 만큼 차감되었습니다!");
            }

            case "전투경험치추가" -> {
                d.addExp(value);
                LevelSystem.checkLevelUp(target);
                p.sendMessage("§a§l[!] " + target.getName() + " 전투경험치 +" + value + " 만큼 추가되었습니다!");
            }

            case "전투경험치차감" -> {
                d.addExp(-value);
                p.sendMessage("§a§l[!] " + target.getName() + " 전투경험치 -" + value + " 만큼 차감되었습니다!");
            }

            default -> {
                p.sendMessage("§8/전투레벨");
                p.sendMessage("§8ㄴ §7[정보] [닉네임]");
                p.sendMessage("§8- §f해당 플레이어에게 전투 정보를 보여준다.");
                p.sendMessage("");
                p.sendMessage("§8ㄴ §7[전투레벨추가] [닉네임] [수치]");
                p.sendMessage("§8- §f해당 플레이어에게 전투 레벨을 추가한다.");
                p.sendMessage("");
                p.sendMessage("§8ㄴ §7[전투레벨차감] [닉네임] [수치]");
                p.sendMessage("§8- §f해당 플레이어에게 전투 레벨을 차감한다.");
                p.sendMessage("");
                p.sendMessage("§8ㄴ §7[전투경험치추가] [닉네임] [수치]");
                p.sendMessage("§8- §f해당 플레이어에게 전투 경험치를 추가한다.");
                p.sendMessage("");
                p.sendMessage("§8ㄴ §7[전투경험치차감] [닉네임] [수치]");
                p.sendMessage("§8- §f해당 플레이어에게 전투 경험치를 차감한다.");
                return true;
            }
        }

        LevelManager.save(target);
        return true;
    }
}
