package com.author404e.boom;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tool {
    /**
     * 替换颜色代码
     *
     * @param s 源字符串
     * @return 替换了颜色代码的字符串
     */
    public static String color(String s) {
        return s.replace("&", "§");
    }

    /**
     * WARN消息
     *
     * @param s 消息
     */
    public static void warn(String s) {
        getBoom().getLogger().warning(color(s));
    }

    /**
     * INFO消息
     *
     * @param s 消息
     */
    public static void info(String s) {
        getBoom().getLogger().info(color(s));
    }

    /**
     * 获取message.yml的配置文件
     *
     * @return message.yml的配置文件
     */
    public static Configuration getMsg() {
        return new Msg().getMsgCfg();
    }

    /**
     * 从message.yml中的path位置获取字符串
     *
     * @param path 路径
     * @return 字符串
     */
    public static String msg(String path) {
        String s = getMsg().getString(path);
        return s == null ? "" : color(s);
    }

    /**
     * @return 插件消息前缀
     */
    public static String before() {
        return msg("before");
    }

    /**
     * 从message.yml中的path位置获取字符串并添加前缀
     *
     * @param path 路径
     * @return 字符串
     */
    public static String full(String path) {
        return before() + msg(path);
    }

    /**
     * @return 带前缀的无权限消息
     */
    public static String noPerm() {
        return full("noPerm");
    }

    /**
     * @return 带前缀的未知指令消息
     */
    public static String unknow() {
        return full("unknow");
    }

    /**
     * @return 带前缀的非玩家消息
     */
    public static String nonPlayer() {
        return full("nonPlayer");
    }

    /**
     * 给sender发送带消息
     *
     * @param sender 要发送消息的sender
     * @param msg    要发送的消息
     */
    public static void send(CommandSender sender, String msg) {
        sender.sendMessage(color(msg));
    }

    /**
     * 给sender发送无权限消息
     *
     * @param sender CommandSender
     */
    public static void sendNoPerm(CommandSender sender) {
        send(sender, noPerm());
    }

    /**
     * 给sender发送未知指令消息
     *
     * @param sender CommandSender
     */
    public static void sendUnknow(CommandSender sender) {
        send(sender, unknow());
    }

    /**
     * 给sender发送仅玩家可用消息
     *
     * @param sender CommandSender
     */
    public static void sendNonPlayer(CommandSender sender) {
        send(sender, nonPlayer());
    }

    /**
     * 给sender发送带消息
     *
     * @param sender 要发送消息的sender
     * @param path   要发送的消息的路径
     */
    public static void sendMsg(CommandSender sender, String path) {
        send(sender, msg(path));
    }

    /**
     * 给sender发送带前缀的消息
     *
     * @param sender 要发送消息的sender
     * @param path   要发送的消息的路径
     */
    public static void sendFull(CommandSender sender, String path) {
        send(sender, full(path));
    }

    /**
     * 判断sender是否有此权限
     *
     * @param sender 要判断的CommandSender
     * @param perm   要判断的权限
     * @return 如有权限返回true, 反之发送无权限消息并返回false
     */
    public static Boolean hasPerm(CommandSender sender, String perm) {
        if (sender.hasPermission(perm)) return true;
        sendNoPerm(sender);
        return false;
    }

    /**
     * 判断此sender是否是玩家
     *
     * @param sender 要判断的CommandSender
     * @return 如是玩家返回true, 反之发送仅玩家可用消息并返回false
     */
    public static Boolean isPlayer(CommandSender sender) {
        if (sender instanceof Player) return true;
        sendNonPlayer(sender);
        return false;
    }

    /**
     * 异步更新检查
     *
     * @param b      是否忽略设置中的更新检查开关
     * @param sender 若有更新需要通知的sender
     */
    public static void checkUpdate(boolean b, CommandSender sender) {
        if (!b && getBoom().getConfig().getBoolean("update")) return;
        if (Boom.newVersion == null)
            Bukkit.getScheduler().scheduleAsyncDelayedTask(getBoom(), () -> {
                String newVersion = getUpdate();
                switch (newVersion) {
                    case "":
                        return;
                    case "error":
                        info("&e检查更新失败");
                        return;
                    default:
                        Boom.newVersion = newVersion;
                        sendUpdateMsg(sender);
                }
            });
    }

    /**
     * 检查更新
     * 检查更新失败返回error
     * 无更新返回""
     * 有更新返回新版本版本号
     *
     * @return 是否有新版本
     */
    public static String getUpdate() {
        String s;
        try {
            s = Jsoup.connect("https://api.spigotmc.org/legacy/update.php?resource=89540")
                    .timeout(30000).ignoreContentType(true).get().body().html();
        } catch (IOException e) {
            try {
                s = Jsoup.connect("https://api.spigotmc.org/legacy/update.php?resource=89540")
                        .timeout(30000).ignoreContentType(true).get().body().html();
            } catch (IOException ioException) {
                return "error";
            }
        }
        return version(s) > version(getBoom().getDescription().getVersion()) ? s : "";
    }

    /**
     * 发送更新信息
     *
     * @param sender CommandSender
     */
    public static void sendUpdateMsg(CommandSender sender) {
        if (sender == null) return;
        sendFull(sender, "&e插件有新版本了,当前版本 &c" + getBoom().getDescription().getVersion() + " &e,新版本 &a" + Boom.newVersion);
        sendFull(sender, "&e下载地址: https://www.mcbbs.net/thread-1150139-1-1.html");
        sendFull(sender, "&e备用下载地址: https://www.spigotmc.org/resources/89540");
    }

    /**
     * 将版本号转成数字
     *
     * @param s 版本号
     * @return 数字
     */
    public static int version(String s) {
        return Integer.parseInt(s.replace(".", ""));
    }

    /**
     * 获取插件实例
     *
     * @return 插件实例
     */
    public static Boom getBoom() {
        return Boom.instance;
    }

    /**
     * 获取config.yml配置文件
     *
     * @return 配置文件
     */
    public static ConfigurationSection getConfig() {
        return getBoom().getConfig();
    }

    /**
     * 从config.yml中的path位置获取字符串
     *
     * @param path 路径
     * @return 字符串
     */
    public static String cfgMsg(String path) {
        String s = getConfig().getString(path);
        return s == null ? "" : color(s);
    }

    /**
     * 从config.yml中的path位置获取字符串并添加前缀
     *
     * @param path 路径
     * @return 字符串
     */
    public static String cfgFull(String path) {
        return before() + cfgMsg(path);
    }

    /**
     * 给sender发送带消息
     *
     * @param sender 要发送消息的sender
     * @param path   要发送的消息的路径
     */
    public static void sendCfgMsg(CommandSender sender, String path) {
        sender.sendMessage(cfgMsg(path));
    }

    /**
     * 给sender发送带前缀的消息
     *
     * @param sender 要发送消息的sender
     * @param path   要发送的消息的路径
     */
    public static void sendCfgFull(CommandSender sender, String path) {
        sender.sendMessage(cfgFull(path));
    }

    /**
     * 服务器执行指令
     *
     * @param cmd 指令
     */
    public static void serverCmd(String cmd) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }

    /**
     * 手持调试棒判断
     *
     * @param p 玩家
     * @return 是否手持调试棒
     */
    public static Boolean isHoldStick(Player p) {
        ItemStack i = p.getInventory().getItemInMainHand();
        return i.getType().equals(Material.STICK)
                && i.getItemMeta() != null
                && i.getItemMeta().getLore() != null
                && i.getItemMeta().getLore().contains("§f调试棒");
    }

    /**
     * 加载指令转接
     *
     * @return 指令转接的map
     */
    public static void getTransferCommand() {
        Map<String, String> map = new HashMap<>();
        getConfig().getStringList("transfer-command.list").forEach(a -> {
            String[] s = a.split("-");
            if (s.length == 2) map.put(s[0], s[1]); //键: 玩家输入的指令,值: 玩家转接使用的指令
        });
        Boom.transferMap = map;
    }

    /**
     * @return 调试棒ItemStack
     */
    public static ItemStack getStick() {
        ItemStack is = new ItemStack(Material.STICK);
        ItemMeta im = is.getItemMeta();
        List<String> lore = Arrays.asList(
                "§f调试棒",
                "§f右键点击切换盔甲架和展示框可见性",
                "§f潜行+右键点击切换展示框和盔甲架为切换可交互",
                "§f潜行+右键点击方块或空气将范围1内的",
                "§f不可交互盔甲架为可交互并自动切换无重力"
        );
        assert im != null;
        im.setDisplayName("§e调试棒");
        im.setLore(lore);
        is.setItemMeta(im);
        return is;
    }

    /**
     * 设置命令解析的类
     *
     * @param command 命令
     */
    public static void setCommand(String command) {
        PluginCommand c = getBoom().getCommand(command);
        if (c != null) {
            c.setExecutor(new BoomCommand());
            c.setTabCompleter(new BoomCommand());
        }
    }
}
