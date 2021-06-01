package com.author404e.boom;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

import static com.author404e.boom.BoomEvent.reloadParticle;
import static com.author404e.boom.Tool.*;

public final class Boom extends JavaPlugin {
    public static String newVersion = null;
    public static Boom instance;
    public static String ver;
    public static Map<String, String> transferMap = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        checkUpdate(false, null);
        getServer().getPluginManager().registerEvents(new BoomEvent(), this); //注册爆炸事件监听器
        getServer().getPluginManager().registerEvents(new Stick(), this); //注册调试棒事件监听器
        getServer().getPluginManager().registerEvents(new WeatherGUIEvent(), this); //注册天气gui监听器
        setCommand("boom");
        setCommand("bm");
        saveDefaultConfig();
        reloadConfig();
        Msg.reload();
        getTransferCommand();
        reloadParticle();
        ver = getBoom().getDescription().getVersion();
        new Metrics(this, 11445);
        info("§2防爆插件" + ver + "已启用 §e作者§e404E");
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        info("§2防爆插件" + ver + "已卸载 §e作者§e404E");
    }
}
