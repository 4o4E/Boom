package com.author404e.boom;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import static com.author404e.boom.Tool.getBoom;
import static com.author404e.boom.Tool.warn;

public class Msg {
    private FileConfiguration msgCfg;
    private File msgCfgFile;

    /**
     * reload
     */
    public static void reload(){
        new Msg().reloadMsgCfg();
    }

    /**
     * 重载message.yml
     */
    public void reloadMsgCfg() {
        saveDefaultStoreConfig();
        if (msgCfgFile == null)
            msgCfgFile = new File(getBoom().getDataFolder(), "message.yml");
        msgCfg = YamlConfiguration.loadConfiguration(msgCfgFile);
        // 从本体加载默认文件
        InputStream is = getBoom().getResource("message.yml");
        if (is != null) {
            Reader defConfigStream = new InputStreamReader(is, StandardCharsets.UTF_8);
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            msgCfg.setDefaults(defConfig);
        } else warn("§c加载message.yml失败");
    }

    public void saveDefaultStoreConfig() {
        if (msgCfgFile == null) msgCfgFile = new File(getBoom().getDataFolder(), "message.yml");
        if (!msgCfgFile.exists()) {
            getBoom().saveResource("message.yml", false);
            getBoom().saveResource("示例config.yml", false);
        }
    }

    /**
     * @return Message的FileConfiguration
     */
    public FileConfiguration getMsgCfg() {
        if (msgCfg == null) reloadMsgCfg();
        return msgCfg;
    }
}
