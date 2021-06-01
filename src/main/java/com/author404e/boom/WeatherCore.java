package com.author404e.boom;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.author404e.boom.Tool.*;

public class WeatherCore {

    //短指令天气控制
    public static void weatherControlThis(CommandSender sender, String args) {
        if (isPlayer(sender) && hasPerm(sender, "boom.weather")) {
            World world = ((Player) sender).getWorld();
            args(sender, args, world);
        }
    }

    //长指令天气控制
    public static void weatherControl(CommandSender sender, String args, String w) {
        World world = Bukkit.getServer().getWorld(w);
        if (hasPerm(sender, "boom.weather") && world != null) args(sender, args, world);
        else send(sender, full("no-world").replace("{world}", w));
    }

    private static void args(CommandSender sender, String args, World world) {
        switch (args) {
            case "thunder":
                thunder(sender, world);
                break;
            case "rain":
                rain(sender, world);
                break;
            case "sun":
                sun(sender, world);
                break;
            case "ls":
            case "longsun":
                longsun(sender, world);
                break;
            default:
                send(sender, full("error-args"));
        }
    }

    public static void thunder(CommandSender sender, World world) {
        if (world.isClearWeather() || !world.isThundering()) { //晴天/雨天切换雷暴
            world.setStorm(true);
            world.setThundering(true);
            world.setWeatherDuration(12000);
            sender.sendMessage(worldR("thunder", world));
        } else {
            sender.sendMessage(timeR("thunder", world));
        }
    }

    public static void rain(CommandSender sender, World world) {
        if (world.isClearWeather() || world.isThundering()) { //晴天/雷暴切换雨天
            world.setStorm(true);
            world.setThundering(false);
            world.setWeatherDuration(12000);
            sender.sendMessage(worldR("rain", world));
        } else {
            sender.sendMessage(timeR("rain", world));
        }
    }

    public static void sun(CommandSender sender, World world) {
        if (!world.isClearWeather()) { //非晴天切换晴天
            world.setStorm(false);
            world.setThundering(false);
            world.setWeatherDuration(12000);
            sender.sendMessage(worldR("sun", world));
        } else {
            sender.sendMessage(timeR("sun", world));
        }
    }

    public static void longsun(CommandSender sender, World world) {
        world.setStorm(false);
        world.setThundering(false);
        world.setWeatherDuration(72000);
        sender.sendMessage(worldR("longsun", world));
    }

    private static String time(int tick) {
        int s = tick / 20;
        String second;
        if (s > 60) { //时长大于1分钟
            if (s % 60 != 0) {
                second = s % 60 + "秒";
            } else {
                second = "";
            }
            return tick / 1200 + "分钟" + second;
        } else {
            return s % 60 + "秒";
        }
    }

    private static String worldR(String string, World world) {
        return full("weather." + string).replace("{world}", world.getName());
    }

    private static String timeR(String string, World world) {
        return full("weather-already." + string)
                .replace("{world}", world.getName())
                .replace("{time}", time(world.getWeatherDuration()));
    }
}
