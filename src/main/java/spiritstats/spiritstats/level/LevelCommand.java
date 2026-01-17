package spiritstats.spiritstats.level;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class LevelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String l, String[] args) {

        if (!(sender instanceof Player p)) return true;

        if (args.length == 0) {
            PlayerLevelData d = LevelManager.get(p);
            p.sendMessage("§6[ 레벨 정보 ]");
            p.sendMessage("§e레벨: §f" + d.getLevel());
            p.sendMessage("§7경험치: §f" + d.getExp());
            return true;
        }

        if (!p.hasPermission("spiritstats.admin")) {
            p.sendMessage("§c권한이 없습니다.");
            return true;
        }

        if (args[0].equals("정보")) {
            if (args.length < 2) {
                p.sendMessage("§c/레벨 정보 [닉네임]");
                return true;
            }

            Player target = p.getServer().getPlayer(args[1]);
            if (target == null) {
                p.sendMessage("§c플레이어를 찾을 수 없습니다.");
                return true;
            }

            PlayerLevelData d = LevelManager.get(target);
            p.sendMessage("§6[ " + target.getName() + " 레벨 정보 ]");
            p.sendMessage("§e레벨: §f" + d.getLevel());
            p.sendMessage("§7경험치: §f" + d.getExp());
            return true;
        }

        if (args[0].equals("리로드")) {
            LevelManager.reloadAll();
            p.sendMessage("§a모든 플레이어 레벨 데이터를 파일 기준으로 리로드했습니다.");
            return true;
        }

        if (args.length < 2) {
            p.sendMessage("§c/레벨 <레벨추가|레벨차감|경험치추가|경험치차감> [수치]");
            return true;
        }

        PlayerLevelData d = LevelManager.get(p);
        int value;

        try {
            value = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            p.sendMessage("§c수치는 숫자여야 합니다.");
            return true;
        }

        switch (args[0]) {
            case "레벨추가" -> {
                d.addLevel(value);
                p.sendMessage("§a레벨 +" + value);
            }
            case "레벨차감" -> {
                d.addLevel(-value);
                p.sendMessage("§c레벨 -" + value);
            }
            case "경험치추가" -> {
                d.addExp(value);
                LevelSystem.checkLevelUp(p);
                p.sendMessage("§a경험치 +" + value);
            }
            case "경험치차감" -> {
                d.addExp(-value);
                p.sendMessage("§c경험치 -" + value);
            }
            default -> {
                p.sendMessage("§c알 수 없는 명령어입니다.");
                return true;
            }
        }

        LevelManager.save(p);
        return true;
    }
}