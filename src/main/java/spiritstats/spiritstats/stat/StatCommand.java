package spiritstats.spiritstats.stat;

import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class StatCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String l, String[] args) {

        if (!(sender instanceof Player p)) return true;

        if (args.length == 0) {
            StatGUI.open(p);
            return true;
        }

        if (args[0].equals("정보")) {
            if (args.length < 2) {
                p.sendMessage("§c/스텟 정보 [닉네임]");
                return true;
            }

            Player target = p.getServer().getPlayer(args[1]);
            if (target == null) {
                p.sendMessage("§c플레이어를 찾을 수 없습니다.");
                return true;
            }

            PlayerStatData d = StatManager.get(target);
            p.sendMessage("§6[ " + target.getName() + " 스텟 정보 ]");
            p.sendMessage("§e공명: §f" + d.getResonance());
            p.sendMessage("§e흐름: §f" + d.getFlow());
            p.sendMessage("§c공격 문양: §f" + d.getAttackGlyph());
            p.sendMessage("§b방어 문양: §f" + d.getDefenseGlyph());
            p.sendMessage("§a포인트: §f" + d.getStatPoint());
            return true;
        }

        if (!p.hasPermission("spiritstats.admin")) {
            p.sendMessage("§c권한이 없습니다.");
            return true;
        }


        if (args[0].equals("리로드")) {
            if (!p.hasPermission("spiritstats.admin")) {
                p.sendMessage("§c권한이 없습니다.");
                return true;
            }

            StatManager.reloadAll();
            p.sendMessage("§a스텟 데이터가 파일 기준으로 리로드되었습니다.");
            return true;
        }

        if (args.length < 4) {
            p.sendMessage("§c명령어 형식이 올바르지 않습니다.");
            return true;
        }

        String stat = args[0];
        String action = args[1];
        Player target = p.getServer().getPlayer(args[2]);

        if (target == null) {
            p.sendMessage("§c플레이어를 찾을 수 없습니다.");
            return true;
        }

        int value;
        try {
            value = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            p.sendMessage("§c수치는 숫자여야 합니다.");
            return true;
        }

        if (!action.equals("추가") && !action.equals("차감")) {
            p.sendMessage("§c추가 또는 차감만 가능합니다.");
            return true;
        }

        PlayerStatData d = StatManager.get(target);

        switch (stat) {

            case "공명" -> {
                if (action.equals("추가"))
                    for (int i = 0; i < value && d.getResonance() < 150; i++) d.addResonance();
                else
                    d.removeResonance(value);
            }

            case "흐름" -> {
                if (action.equals("추가"))
                    for (int i = 0; i < value && d.getFlow() < 150; i++) d.addFlow();
                else
                    d.removeFlow(value);
            }

            case "공격문양" -> {
                if (action.equals("추가"))
                    for (int i = 0; i < value && d.getAttackGlyph() < 150; i++) d.addAttackGlyph();
                else
                    d.removeAttackGlyph(value);
            }

            case "방어문양" -> {
                if (action.equals("추가"))
                    for (int i = 0; i < value && d.getDefenseGlyph() < 150; i++) d.addDefenseGlyph();
                else
                    d.removeDefenseGlyph(value);
            }

            case "포인트" -> {
                if (!action.equals("추가")) {
                    p.sendMessage("§c포인트는 차감할 수 없습니다.");
                    return true;
                }
                d.addPoint(value);
                p.sendMessage("§a" + target.getName() + " 에게 포인트 +" + value);
                return true;
            }

            default -> {
                p.sendMessage("§c알 수 없는 스텟입니다.");
                return true;
            }
        }

        StatApplier.apply(target);
        StatManager.save(target);
        p.sendMessage("§a" + target.getName() + " 의 스텟이 수정되었습니다.");
        return true;
    }
}