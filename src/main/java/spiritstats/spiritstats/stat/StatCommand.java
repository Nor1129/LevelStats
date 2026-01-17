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
                p.sendMessage("§c/전투스텟 정보 [닉네임]");
                return true;
            }

            Player target = p.getServer().getPlayer(args[1]);
            if (target == null) {
                p.sendMessage("§c§l[!] 플레이어를 찾을 수 없습니다.");
                return true;
            }

            PlayerStatData d = StatManager.get(target);
            p.sendMessage("§6[ " + target.getName() + " 전투 스텟 정보 ]");
            p.sendMessage("§e공명: §f" + d.getResonance());
            p.sendMessage("§e흐름: §f" + d.getFlow());
            p.sendMessage("§c공격 문양: §f" + d.getAttackGlyph());
            p.sendMessage("§b방어 문양: §f" + d.getDefenseGlyph());
            p.sendMessage("§a포인트: §f" + d.getStatPoint());
            return true;
        }

        if (!p.hasPermission("spiritstats.admin")) {
            p.sendMessage("§c§l[!] 스텟 권한이 없습니다.");
            return true;
        }


        if (args[0].equals("리로드")) {
            if (!p.hasPermission("spiritstats.admin")) {
                p.sendMessage("§c§l[!] 리로드 권한이 없습니다.");
                return true;
            }

            StatManager.reloadAll();
            p.sendMessage("§a§l[!] 스텟 데이터를 리로드했습니다.");
            return true;
        }

        if (args.length < 4) {
            p.sendMessage("§8/전투스텟");
            p.sendMessage("§8ㄴ §7[공명] [추가/차감] [닉네임] [수치]");
            p.sendMessage("§8- §f해당 플레이어에게 공명 스텟 레벨을 수치만큼 추가 또는 차감합니다.");
            p.sendMessage("");
            p.sendMessage("§8ㄴ §7[흐름] [추가/차감] [닉네임] [수치]");
            p.sendMessage("§8- §f해당 플레이어에게 흐름 스텟 레벨을 수치만큼 추가 또는 차감합니다.");
            p.sendMessage("");
            p.sendMessage("§8ㄴ §7[공격문양] [추가/차감] [닉네임] [수치]");
            p.sendMessage("§8- §f해당 플레이어에게 공격문양 스텟 레벨을 수치만큼 추가 또는 차감합니다.");
            p.sendMessage("");
            p.sendMessage("§8ㄴ §7[방어문양] [추가/차감] [닉네임] [수치]");
            p.sendMessage("§8- §f해당 플레이어에게 방어문양 스텟 레벨을 수치만큼 추가 또는 차감합니다.");
            p.sendMessage("");
            p.sendMessage("§8ㄴ §7[포인트] [추가/차감] [닉네임] [수치]");
            p.sendMessage("§8- §f해당 플레이어에게 스텟 포인트를 수치만큼 추가 또는 차감합니다.");
            return true;
        }

        String stat = args[0];
        String action = args[1];
        Player target = p.getServer().getPlayer(args[2]);

        if (target == null) {
            p.sendMessage("§c§l[!] 플레이어를 찾을 수 없습니다.");
            return true;
        }

        int value;
        try {
            value = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            p.sendMessage("§c§l[!] 수치는 숫자여야 합니다.");
            return true;
        }

        if (!action.equals("추가") && !action.equals("차감")) {
            p.sendMessage("§c§l[!] 추가 또는 차감만 가능합니다.");
            return true;
        }

        PlayerStatData d = StatManager.get(target);

        switch (stat) {

            case "공명" -> {
                if (action.equals("추가"))
                    for (int i = 0; i < value && d.getResonance() < 150; i++) d.addResonance();

                else if ((action.equals("차감"))) {
                    d.removeResonance(value);
                    p.sendMessage("§a§l[!] " + value + "만큼 차감되었습니다!");

                } else
                    p.sendMessage("§a§l/전투스텟 [공명] [닉네임] [추가/차감] [수치]");
            }


            case "흐름" -> {
                if (action.equals("추가"))
                    for (int i = 0; i < value && d.getFlow() < 150; i++) d.addFlow();

                else if ((action.equals("차감"))) {
                    d.removeFlow(value);
                    p.sendMessage("§a§l[!] " + value + "만큼 차감되었습니다!");

                } else
                    p.sendMessage("§a§l/전투스텟 [흐름] [닉네임] [추가/차감] [수치]");
            }

            case "공격문양" -> {
                if (action.equals("추가"))
                    for (int i = 0; i < value && d.getAttackGlyph() < 150; i++) d.addAttackGlyph();

                else if ((action.equals("차감"))) {
                    d.removeAttackGlyph(value);
                    p.sendMessage("§a§l[!] " + value + "만큼 차감되었습니다!");

                } else
                    p.sendMessage("§a§l/전투스텟 [공격문양] [닉네임] [추가/차감] [수치]");
            }

            case "방어문양" -> {
                if (action.equals("추가"))
                    for (int i = 0; i < value && d.getDefenseGlyph() < 150; i++) d.addDefenseGlyph();

                else if ((action.equals("차감"))) {
                    d.removeDefenseGlyph(value);
                    p.sendMessage("§a§l[!] " + value + "만큼 차감되었습니다!");

                } else
                    p.sendMessage("§a§l/전투스텟 [방어문양] [닉네임] [추가/차감] [수치]");
            }

            case "포인트" -> {
                if (action.equals("추가"))
                    d.addPoint(value);

                else if ((action.equals("차감"))) {
                    d.removePoint(value);
                    p.sendMessage("§a§l[!] " + value + "만큼 차감되었습니다!");

                } else
                    p.sendMessage("§a§l/전투스텟 [포인트] [닉네임] [추가/차감] [수치]");
            }

            default -> {
                p.sendMessage("§8/전투스텟");
                p.sendMessage("§8ㄴ §7[공명] [추가/차감] [닉네임] [수치]");
                p.sendMessage("§8- §f해당 플레이어에게 공명 스텟 레벨을 수치만큼 추가 또는 차감합니다.");
                p.sendMessage("");
                p.sendMessage("§8ㄴ §7[흐름] [추가/차감] [닉네임] [수치]");
                p.sendMessage("§8- §f해당 플레이어에게 흐름 스텟 레벨을 수치만큼 추가 또는 차감합니다.");
                p.sendMessage("");
                p.sendMessage("§8ㄴ §7[공격문양] [추가/차감] [닉네임] [수치]");
                p.sendMessage("§8- §f해당 플레이어에게 공격문양 스텟 레벨을 수치만큼 추가 또는 차감합니다.");
                p.sendMessage("");
                p.sendMessage("§8ㄴ §7[방어문양] [추가/차감] [닉네임] [수치]");
                p.sendMessage("§8- §f해당 플레이어에게 방어문양 스텟 레벨을 수치만큼 추가 또는 차감합니다.");
                p.sendMessage("");
                p.sendMessage("§8ㄴ §7[포인트] [추가/차감] [닉네임] [수치]");
                p.sendMessage("§8- §f해당 플레이어에게 스텟 포인트를 수치만큼 추가 또는 차감합니다.");
                return true;
            }
        }

        StatApplier.apply(target);
        StatManager.save(target);
        p.sendMessage("§a§l[!] " + target.getName() + "님의 스텟이 추가되었습니다.");
        return true;
    }
}