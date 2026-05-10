package jobslevelpl.jobslevelpl;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class JobCommand implements CommandExecutor {

    private final Jobslevelpl plugin;
    private static final String PERMISSION = "jobsscoreboard.admin";

    public JobCommand(Jobslevelpl plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(ChatColor.RED + "이 명령어를 사용할 권한이 없습니다.");
            return true;
        }

        if (command.getName().equalsIgnoreCase("직업")) {
            if (args.length < 2) {
                sender.sendMessage("§7────────────────────────────");
                sender.sendMessage("§f/직업");
                sender.sendMessage("§f<닉네임> <직업이름> §7: 닉네임에 직업을 설정합니다.");
                sender.sendMessage("§f<닉네임> <제거> §7: 닉네임에 전체 직업을 제거합니다.");
                sender.sendMessage("§f<닉네임> 제거 <직업이름> §7: 닉네임에 해당 직업을 제거합니다.");
                sender.sendMessage("§7────────────────────────────");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§c§l[!] 플레이어를 찾을 수 없습니다.");
                return true;
            }

            if (args[1].equalsIgnoreCase("제거")) {
                if (args.length == 3) {
                    String jobToRemove = getColoredJob(args[2]);
                    if (jobToRemove == null) {
                        sender.sendMessage("§c§l[!] 유효하지 않은 직업입니다. (0차~4차광부, 0차~4차농부, 0차~4차어부, 0차~4차도축, 초보자)");
                        return true;
                    }

                    List<String> jobs = plugin.getPlayerJobs().getOrDefault(target.getUniqueId().toString(), new ArrayList<>());
                    if (jobs.remove(jobToRemove)) {
                        plugin.getPlayerJobs().put(target.getUniqueId().toString(), jobs);
                        plugin.getJobsConfig().set(target.getUniqueId().toString(), jobs);
                        plugin.saveJobsData();

                        sender.sendMessage("§c§l[!] " + target.getName() + "님의 직업 " + jobToRemove + "이(가) 삭제되었습니다.");
                        target.sendMessage("§c§l[!] " + "당신의 직업 " + jobToRemove + "이(가) 삭제되었습니다.");
                    } else {
                        sender.sendMessage("§c§l[!] " + target.getName() + "님은 해당 직업을 가지고 있지 않습니다.");
                    }
                    return true;
                } else {
                    if (plugin.getPlayerJobs().containsKey(target.getUniqueId().toString())) {
                        plugin.getPlayerJobs().remove(target.getUniqueId().toString());
                        plugin.getJobsConfig().set(target.getUniqueId().toString(), null);
                        plugin.saveJobsData();

                        sender.sendMessage("§c§l[!] " + target.getName() + "님의 직업이 삭제되었습니다.");
                        target.sendMessage("§c§l[!] 당신의 직업이 삭제되었습니다.");
                    } else {
                        sender.sendMessage("§c§l[!] " + target.getName() + "님의 직업이 존재하지 않습니다.");
                    }
                    return true;
                }
            }

            String job = args[1];
            String coloredJob = getColoredJob(job);

            if (coloredJob == null) {
                sender.sendMessage("§c§l[!] " + "유효하지 않은 직업입니다. (0차~4차광부, 0차~4차농부, 0차~4차어부, 0차~4차도축, 초보자)");
                return true;
            }

            List<String> jobs = plugin.getPlayerJobs().getOrDefault(target.getUniqueId().toString(), new ArrayList<>());
            jobs.add(coloredJob);
            plugin.getPlayerJobs().put(target.getUniqueId().toString(), jobs);
            plugin.getJobsConfig().set(target.getUniqueId().toString(), jobs);
            plugin.saveJobsData();

            sender.sendMessage("§a§l[!] " + target.getName() + "님에게 " + coloredJob + " 직업이 설정되었습니다.");
        }
        return true;
    }

    private String getColoredJob(String job) {
        switch (job.toLowerCase()) {
            case "0차광부":
                return "§7초보 광부";
            case "1차광부":
                return "§7견습 광부";
            case "2차광부":
                return "§7숙련 광부";
            case "3차광부":
                return "§7전문 광부";
            case "4차광부":
                return "§7장인 광부";

            case "0차농부":
                return "§6초보 농부";
            case "1차농부":
                return "§6견습 농부";
            case "2차농부":
                return "§6숙련 농부";
            case "3차농부":
                return "§6전문 농부";
            case "4차농부":
                return "§6장인 농부";

            case "0차어부":
                return "§b초보 어부";
            case "1차어부":
                return "§b견습 어부";
            case "2차어부":
                return "§b숙련 어부";
            case "3차어부":
                return "§b전문 어부";
            case "4차어부":
                return "§b장인 어부";

            case "0차도축":
                return "§c초보 도축";
            case "1차도축":
                return "§c견습 도축";
            case "2차도축":
                return "§c숙련 도축";
            case "3차도축":
                return "§c전문 도축";
            case "4차도축":
                return "§c장인 도축";

            case "초보자":
                return "§a초보자";
            default:
                return null;
        }
    }
}