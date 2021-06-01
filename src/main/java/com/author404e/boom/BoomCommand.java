package com.author404e.boom;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.author404e.boom.BoomEvent.reloadParticle;
import static com.author404e.boom.Tool.*;
import static com.author404e.boom.WeatherCore.weatherControl;
import static com.author404e.boom.WeatherCore.weatherControlThis;

public class BoomCommand implements TabExecutor {
    /**
     * tab补全
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> options = new ArrayList<>();
        switch (args.length) {
            case 1:
                if (sender.hasPermission("boom.admin")) {
                    options.add("reload");
                    options.add("update");
                    options.add("help");
                }
                if (sender instanceof Player && (sender.hasPermission("boom.admin") || sender.hasPermission("boom.stick")))
                    options.add("getstick");
                if (sender instanceof Player && (sender.hasPermission("boom.admin") || sender.hasPermission("boom.weather"))) {
                    options.add("weather");
                    options.add("ls");
                    options.add("longsun");
                    options.add("sun");
                    options.add("rain");
                    options.add("thunder");
                }
                Collections.sort(options);
                return options;
            case 2:
                switch (args[0]) {
                    case "longsun":
                    case "ls":
                    case "sun":
                    case "rain":
                    case "thunder":
                        if (sender.hasPermission("boom.admin") || sender.hasPermission("boom.weather")) {
                            for (World w : getBoom().getServer().getWorlds()) options.add(w.getName());
                            return options;
                        }
                    default:
                        return options;
                }
            default:
                return options;
        }
    }

    /**
     * 命令
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0:
                send(sender, before() + "&aBoom &7" + Boom.ver + " &eBy 404E");
                send(sender, before() + "&a防爆 &7" + Boom.ver + " &e作者404E");
                send(sender, before() + "&f查看帮助请使用§a/boom help");
                send(sender, before() + "&f插件问题反馈请在MCBBS留言,帖子地址: ");
                send(sender, before() + "&f&nhttps://www.mcbbs.net/thread-1150139-1-1.html");
                return true;
            case 1:
                switch (args[0].toLowerCase()) {
                    case "weather":
                        if (isPlayer(sender) && hasPerm(sender, "boom.weather")) {
                            new WeatherGUI().weatherSwitchMenu(sender);
                        }
                        return true;
                    case "ls":
                    case "longsun":
                    case "sun":
                    case "rain":
                    case "thunder":
                        weatherControlThis(sender, args[0]);
                        return true;
                    case "getstick":
                        if (isPlayer(sender)
                                && (sender.hasPermission("boom.admin")
                                || sender.hasPermission("boom.stick"))) {
                            Inventory i = ((Player) sender).getInventory();
                            int b = 0;
                            for (int a = 0; a < 40; a++)
                                if (i.getItem(a) == null) {
                                    b++;
                                    i.setItem(a, getStick());
                                    sendFull(sender, "stick");
                                    break;
                                }
                            if (b == 0)
                                sendFull(sender, "fullinv");
                        }
                        return true;
                    case "reload":
                        if (hasPerm(sender, "boom.admin")) {
                            getBoom().saveDefaultConfig();
                            getBoom().reloadConfig();
                            Msg.reload();
                            reloadParticle();
                            getTransferCommand();
                            sendFull(sender, "reload");
                            send(sender, full("transfer").replace("{amount}", String.valueOf(Boom.transferMap.size())));
                            ConfigurationSection cs = getMsg().getConfigurationSection("easy-command");
                            if (cs == null) send(sender, full("easy").replace("{amount}", "0"));
                            else send(sender, full("easy").replace("{amount}", cs.getKeys(false).size() + ""));
                        }
                        return true;
                    case "update":
                        if (hasPerm(sender, "boom.admin")) {
                            send(sender,before() + "&f开始检查更新");
                            checkUpdate(true, sender);
                        }
                        return true;
                    case "help":
                        if (hasPerm(sender, "boom.admin"))
                            getMsg().getStringList("help").forEach(a -> sendMsg(sender, a));
                        return true;
                    default:
                        sendUnknow(sender);
                        return true;
                }
            case 2:
                switch (args[0]) {
                    case "ls":
                    case "longsun":
                    case "sun":
                    case "rain":
                    case "thunder":
                        weatherControl(sender, args[0], args[1]);
                        return true;
                    default:
                        sendFull(sender, "error-args");
                        return true;
                }
            default:
                sendUnknow(sender);
                return true;
        }
    }
}
