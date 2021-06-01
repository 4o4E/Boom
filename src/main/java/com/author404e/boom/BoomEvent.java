package com.author404e.boom;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.author404e.boom.Tool.*;
import static org.bukkit.Bukkit.getServer;
import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_EXPLOSION;

public class BoomEvent implements Listener {
    private static CustomParticle respawn;
    private static CustomParticle bed;

    /**
     * 从配置文件中重新加载粒子效果
     */
    public static void reloadParticle() {
        BoomEvent.respawn = new CustomParticle("respawn-anchor");
        BoomEvent.bed = new CustomParticle("bed");
    }

    /**
     * @param event OP更新提醒
     */
    @EventHandler
    public void opJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (p.isOp() && getBoom().getConfig().getBoolean("update") && Boom.newVersion != null) sendUpdateMsg(p);
    }

    /**
     * 实体生成控制
     *
     * @param event 实体生成
     */
    @EventHandler
    public void entitySpawnDisable(EntitySpawnEvent event) {
        String path = "disable-spawn-entity."
                + event.getEntity().getWorld().getName()
                + "." + event.getEntity().getType().toString().toLowerCase();
        //判断实体在当前世界的禁用列表中 && 获取实体生成概率
        if (!cfgMsg(path).trim().equals("") && getConfig().getInt(path) <= new Random().nextInt(100))
            event.setCancelled(true);
    }

    /**
     * @param event 实体转换
     */
    @EventHandler
    public void transform(EntityTransformEvent event) {
        Entity e = event.getEntity();
        if (!event.isCancelled()
                && getConfig().getStringList("transform." + e.getType().name() + ".world").contains(e.getWorld().getName())) {
            event.setCancelled(true);
            if (Arrays.asList(EntityType.PIGLIN,EntityType.PIGLIN_BRUTE).contains(e.getType())) ((PiglinAbstract) e).setConversionTime(0);
            else if (e.getType().equals(EntityType.HOGLIN)) ((Hoglin) e).setConversionTime(0);
        }
    }

    /**
     * @param event 苦力怕充能
     */
    @EventHandler
    public void creeperPower(CreeperPowerEvent event) {
        if (!event.isCancelled() && getConfig().getStringList("transform.CREEPER.world").contains(event.getEntity().getWorld().getName()))
            event.setCancelled(true);
    }

    /**
     * @param event 玩家踩农田
     */
    @EventHandler
    public void playerFarmland(PlayerInteractEvent event) {
        Block b = event.getClickedBlock();
        if (b == null || event.getAction() != Action.PHYSICAL || b.getType() != Material.FARMLAND) return;
        if (getConfig().getStringList("farmland.world").contains(b.getWorld().getName().toLowerCase()))
            event.setCancelled(true);
    }

    /**
     * @param event 实体踩农田
     */
    @EventHandler
    public void entityFarmland(EntityInteractEvent event) {
        if (event.getEntityType() != EntityType.PLAYER
                && event.getBlock().getType() == Material.FARMLAND
                && getConfig().getStringList("farmland.world").contains(event.getBlock().getWorld().getName().toLowerCase()))
            event.setCancelled(true);
    }

    /**
     * @param event 方块被烧毁
     */
    @EventHandler
    public void bbe(BlockBurnEvent event) {
        if (!event.isCancelled()
                && getConfig().getStringList("fire.world").contains(event.getBlock().getWorld().getName())) {
            Block b = event.getIgnitingBlock();
            if (b != null) b.setType(Material.AIR);
            event.setCancelled(true);
        }
    }

    /**
     * @param event 方块被点燃
     */
    @EventHandler
    public void bie(BlockIgniteEvent event) {
        if (!event.isCancelled()
                && getConfig().getStringList("fire.ignite").contains(event.getCause().name())
                && getConfig().getStringList("fire.world").contains(event.getBlock().getWorld().getName())) {
            event.setCancelled(true);
        }
    }

    /**
     * @param event 实体更改方块
     */
    @EventHandler
    public void changeBlock(EntityChangeBlockEvent event) {
        String worldName = event.getEntity().getWorld().getName();
        //凋灵出生爆炸方块替换
        if (!event.isCancelled()
                && event.getEntityType().equals(EntityType.WITHER)
                && getConfig().getStringList("explosion.WITHER.world").contains(worldName)) {
            event.setCancelled(true);
        }
        //末影人搬起方块
        else if (!event.isCancelled()
                && event.getEntityType().equals(EntityType.ENDERMAN)
                && getConfig().getStringList("enderman.world").contains(worldName)) {
            event.setCancelled(true);
        }
    }

    /**
     * @param event 玩家使用重生锚
     */
    @EventHandler
    public void respawn(PlayerInteractEvent event) {
        Block b = event.getClickedBlock();
        if (b == null) return;
        //当mc版本为1.16 && 交互类型为右键 && 位于阻止世界列表 && 方块类型为重生锚 &&(当充能为4 || 未手持萤石 时阻止所有点击)
        if (Bukkit.getServer().getVersion().contains("1.16")
                && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                && getConfig().getStringList("respawn-anchor.world").contains(b.getWorld().getName())
                && event.getClickedBlock().getBlockData().getAsString().contains("respawn_anchor")
                && ((event.getClickedBlock().getBlockData().getAsString().contains("respawn_anchor[charges=4]"))
                || (!event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.GLOWSTONE)
                && !event.getPlayer().getInventory().getItemInOffHand().getType().equals(Material.GLOWSTONE)))) {
            event.setCancelled(true);
            Player p = event.getPlayer();
            //提示信息
            String msg = cfgMsg("respawn-anchor.message");
            if (!msg.trim().equals("")) send(p, before() + msg);
            //音效
            String sound = getConfig().getString("respawn-anchor.sound");
            if (sound != null && !sound.trim().equals(""))
                p.playSound(p.getLocation(), Sound.valueOf(sound), 1, 1);
            //粒子
            if (getConfig().getString("respawn-anchor.particle.type") != null)
                respawn.spawn(event.getClickedBlock().getLocation());
        }
    }

    /**
     * @param event 玩家使用床
     */
    @EventHandler
    public void bed(PlayerBedEnterEvent event) {
        if (getConfig().getStringList("bed.world").contains(event.getBed().getWorld().getName())) {
            event.setCancelled(true);
            Player p = event.getPlayer();
            //提示信息
            String msg = cfgMsg("bed.message");
            if (!msg.trim().equals(""))
                sendCfgFull(p, "bed.message");
            //音效
            String sound = getConfig().getString("bed.sound");
            if (sound != null && !sound.trim().equals(""))
                p.playSound(p.getLocation(), Sound.valueOf(sound), 1, 1);
            //粒子
            if (getConfig().getString("bed.particle.type") != null)
                bed.spawn(event.getBed().getLocation());
        }
    }

    /**
     * @param event 爆炸实体伤害
     */
    @EventHandler
    public void explosive(EntityDamageByEntityEvent event) {
        if (!event.getCause().equals(ENTITY_EXPLOSION)) return;
        ConfigurationSection c = getConfig().getConfigurationSection("explosion." + event.getDamager().getType().name());
        if (c == null) return;
        System.out.println(event.getEntityType());
        switch (event.getEntityType()) {
            case DROPPED_ITEM:
                if (c.getBoolean("item-damage")) event.setCancelled(true);
                return;
            case PLAYER:
                if (c.getBoolean("player-damage")) event.setCancelled(true);
                return;
            default:
                if (c.getBoolean("other-damage")) event.setCancelled(true);
        }
    }

    /**
     * @param event 爆炸方块伤害
     */
    @EventHandler
    public void entityExplosive(EntityExplodeEvent event) {
        String type = event.getEntityType().name();
        System.out.println(event.getEntityType().name());
        World w = event.getLocation().getWorld();
        if (w == null) return;
        if (!event.isCancelled()
                && getConfig().getStringList("explosion." + type + ".world").contains(w.getName())) {
            event.setCancelled(true);
            if (getConfig().getBoolean("explosion." + type + ".particle"))
                event.getLocation().getWorld().spawnParticle(Particle.EXPLOSION_LARGE, event.getLocation(), 20, 1, 1, 1);
            if (getConfig().getBoolean("explosion." + type + ".sound"))
                event.getLocation().getWorld().playSound(event.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 0);
        }
    }

    /**
     * @param event tab补全控制
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTabComplete(PlayerCommandSendEvent event) {
        //判断禁用命令列表是否为空 && 玩家是否不为OP && 发送的命令是否在禁用命令列表中
        if (event.getPlayer().hasPermission("boom.commandbypass")) return;
        event.getCommands().removeIf(c -> isContain(getConfig().getStringList("disable-command.commands"), c));
    }

    /**
     * 控制台执行简化指令
     *
     * @param event 控制台执行指令
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void ServerCommand(ServerCommandEvent event) {
        List<String> easyCmd = getConfig().getStringList("easy-command." + event.getCommand());
        if (easyCmd.size() == 0) return;
        easyCmd.forEach(Tool::serverCmd);
        event.setCancelled(true);
    }

    /**
     * 指令控制
     *
     * @param event 玩家执行指令
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void disableCommand(PlayerCommandPreprocessEvent event) {
        Player p = event.getPlayer();
        String c = event.getMessage().toLowerCase().replace("/", "");
        //指令转接
        if (Boom.transferMap.containsKey(c)) {
            //无权限
            if (!(p.hasPermission("boom.transfer.*") || p.hasPermission("boom.transfer." + c)))
                return;
            p.performCommand(Boom.transferMap.get(c));
            String log = cfgMsg("transfer-command.info");
            if (!log.trim().equals("")) info(log.replace("{player}", p.getName())
                    .replace("{pic}", event.getMessage()) //玩家输入指令
                    .replace("{pec}", "/" + Boom.transferMap.get(c))); //玩家转接执行指令
            event.setCancelled(true);
        }
        //指令简化
        else if (getConfig().getStringList("easy-command." + c).size() != 0) {
            if (!p.hasPermission("boom.easy.*") && !p.hasPermission("boom.easy." + c)) return;
            getConfig().getStringList("easy-command." + c).forEach(p::performCommand);
            event.setCancelled(true);
        }
        //禁用命令 玩家是否有跳过权限 && 发送的命令在全局禁用命令列表中 || 发送的命令在当前世界禁用命令列表中
        else if (!p.hasPermission("boom.commandbypass")
                && (isContain(getConfig().getStringList("disable-command.commands"), c)
                || isContain(getConfig().getStringList("disable-command.worlds." + p.getWorld().getName()), c))) {
            //取消指令 && 返回假消息
            event.setCancelled(true);
            //玩家触发后执行指令
            getConfig().getStringList("disable-command.command-trigger").forEach(s -> serverCmd(replaceVar(event, s)));
            sendCfgMsg(p, "disable-command.message.user");
            //控制台消息
            String console = disableCmdMsg(event, "disable-command.message.console");
            if (!console.trim().equals("")) info(console);
            //OP消息 & 音效
            if (getConfig().getString("disable-command.message.op") != null)
                getServer().getOnlinePlayers().forEach(op -> {
                    if (op.isOp()) {
                        sendMsg(op, disableCmdMsg(event, "disable-command.message.op"));
                        String sound = cfgMsg("disable-command.op-sound");
                        if (!sound.trim().equals("")) op.playSound(op.getLocation(), Sound.valueOf(sound), 1, 1);
                    }
                });
            //log记录
            if (getConfig().getBoolean("disable-command.log.enable")) {
                //日志格式
                String logFormat = cfgMsg("disable-command.log.log-format");
                if (logFormat.trim().equals("")) logFormat = "{player}: {command} (IP: {ip})";
                //日期格式
                String dtf = cfgMsg("disable-command.log.datetime-format");
                if (dtf.trim().equals("")) dtf = "[yyyy.MM.dd HH:mm:ss]";
                String dt = new SimpleDateFormat(dtf).format(new Date());
                String log = replaceVar(event, logFormat);
                //写入log文件
                String f = cfgMsg("disable-command.log.file");
                if (f.trim().equals("") || f.equalsIgnoreCase("config.yml") || f.equalsIgnoreCase("message.yml"))
                    f = "record.log";
                try (FileWriter writer = new FileWriter(getBoom().getDataFolder() + File.separator + f, true)) {
                    writer.write(dt + ": " + log + "\n");
                } catch (IOException e) {
                    warn("&c写入log文件时发生异常");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param event 玩家执行禁用指令
     * @param path  路径
     * @return 此路径的字符串替换变量后的字符串
     */
    private String disableCmdMsg(PlayerCommandPreprocessEvent event, String path) {
        return replaceVar(event, cfgMsg(path));
    }

    /**
     * 替换变量
     *
     * @param event 玩家执行禁用指令
     * @param s     字符串
     * @return 替换变量后的字符串
     */
    private String replaceVar(PlayerCommandPreprocessEvent event, String s) {
        return s.replace("{player}", event.getPlayer().getName())
                .replace("{command}", event.getMessage())
                .replace("{ip}", getIP(event))
                .replace("&", "§");
    }

    /**
     * 获取玩家IP地址,如地址为空返回"null"
     *
     * @param event 玩家执行禁用指令
     * @return 玩家IP地址
     */
    public static String getIP(PlayerCommandPreprocessEvent event) {
        return event.getPlayer().getAddress() == null ? "null" : event.getPlayer().getAddress().toString().replace("/", "");
    }

    /**
     * 判断列表中是否包含目标字符串
     *
     * @param list    目标列表
     * @param message 完整小写命令不带/
     * @return 是否包含目标字符串
     */
    public static Boolean isContain(List<String> list, String message) {
        if (list.size() == 0) return false;
        String name = message.split(" ")[0]; //命令名字
        for (String s : list) {
            if (s.startsWith("@") && name.equals(s.substring(1))) return true;        //@开头 指令名字匹配返回true
            else if (s.startsWith("$") && name.contains(s.substring(1))) return true; //$开头 指令名字包含key返回true
            else if (message.equalsIgnoreCase(s)) return true;                        //无前缀 指令全文匹配才禁用
        }
        return false;
    }
}
