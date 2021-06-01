package com.author404e.boom;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

import static com.author404e.boom.Tool.getConfig;
import static com.author404e.boom.Tool.warn;

public class CustomParticle {
    private Particle type = Particle.REDSTONE; //粒子类型
    private int count = 1; //粒子数量
    private double movex = 0.5; //x轴偏移
    private double movey = 1.0; //y轴偏移
    private double movez = 0.5; //z轴偏移
    private double offsetx = 0.1; //x轴散布
    private double offsety = 0.1; //y轴散布
    private double offsetz = 0.1; //z轴散布
    private Color color = Color.fromRGB(255, 50, 50); //粒子颜色
    private int size = 2; //粒子大小
    private boolean effective = true; //是否有效

    /**
     * @return 获取粒子的DustOption
     */
    public Particle.DustOptions getDustOption() {
        return new Particle.DustOptions(color, size);
    }

    /**
     * 从配置文件中生成粒子
     *
     * @param path 配置文件的具体路径
     */
    public CustomParticle(String path) {
        boolean correct = true;
        ConfigurationSection c = getConfig().getConfigurationSection(path + ".particle");
        if (c == null) {
            this.effective = false;
            return;
        }
        String s = c.getString("type");
        if (s == null) {
            this.effective = false;
            return;
        }
        this.type = Particle.valueOf(s);
        this.count = c.getInt("count");
        List<Double> d = c.getDoubleList("move");
        if (d.size() == 3) {
            this.movex = d.get(0);
            this.movey = d.get(1);
            this.movez = d.get(2);
        } else correct = false;
        d = c.getDoubleList("offset");
        if (d.size() == 3) {
            this.offsetx = d.get(0);
            this.offsety = d.get(1);
            this.offsetz = d.get(2);
        } else correct = false;
        if (!this.type.equals(Particle.REDSTONE)) {
            if (!correct) warn("加载" + path + "粒子时发现有异常配置项,异常配置项将使用默认配置");
            return;
        }
        Color color = getColor(c.getIntegerList("color"));
        if (color != null) this.color = color;
        else correct = false;
        int size = c.getInt("size");
        if (size > 0) this.size = size;
        else correct = false;
        if (!correct) warn("加载" + path + "粒子时发现有异常配置项,异常配置项将使用默认配置");
    }

    /**
     * 获取颜色
     *
     * @param l rgb
     * @return 颜色 错误值返回null
     */
    public Color getColor(List<Integer> l) {
        if (l.size() != 3) return null;
        for (int i : l) if (i < 0 || i > 255) return null;
        return Color.fromRGB(l.get(0), l.get(1), l.get(2));
    }

    /**
     * @param location 在location生成粒子
     */
    public void spawn(Location location) {
        if (!this.effective) return;
        World w = location.getWorld();
        if (w == null) return;
        location = location.add(movex, movey, movez);
        if (type.equals(Particle.REDSTONE))
            w.spawnParticle(type, location, count, offsetx, offsety, offsetz, 0.001, getDustOption());
        else w.spawnParticle(type, location, count, offsetx, offsety, offsetz, 0.001);
    }
}
