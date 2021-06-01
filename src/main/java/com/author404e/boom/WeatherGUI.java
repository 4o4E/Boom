package com.author404e.boom;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static com.author404e.boom.Tool.*;

public class WeatherGUI {
    public void weatherSwitchMenu(CommandSender sender) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            Inventory i = Bukkit.createInventory(p, 27, "§b天气切换");
            ItemStack bg = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
            ItemStack sun = new ItemStack(Material.GRAY_DYE);
            ItemStack longsun = new ItemStack(Material.WHITE_DYE);
            ItemStack rain = new ItemStack(Material.BLUE_DYE);
            ItemStack thunder = new ItemStack(Material.YELLOW_DYE);

            ItemMeta im = bg.getItemMeta();
            assert im != null;
            im.setDisplayName("§f");
            bg.setItemMeta(im);

            im = sun.getItemMeta();
            assert im != null;
            im.setDisplayName(msg("gui.sun.name"));
            im.setLore(lore("sun"));
            sun.setItemMeta(im);

            im = rain.getItemMeta();
            assert im != null;
            im.setDisplayName(msg("gui.rain.name"));
            im.setLore(lore("rain"));
            rain.setItemMeta(im);

            im = thunder.getItemMeta();
            assert im != null;
            im.setDisplayName(msg("gui.thunder.name"));
            im.setLore(lore("thunder"));
            thunder.setItemMeta(im);

            im = longsun.getItemMeta();
            assert im != null;
            im.setDisplayName(msg("gui.longsun.name"));
            im.setLore(lore("longsun"));
            longsun.setItemMeta(im);

            i.setItem(10, sun);
            i.setItem(12, rain);
            i.setItem(14, thunder);
            i.setItem(16, longsun);
            for (int a = 0; a < 27; a++) if (i.getItem(a) == null) i.setItem(a, bg);
            p.openInventory(i);
        } else sendNonPlayer(sender);
    }

    private List<String> lore(String name) {
        List<String> list = new ArrayList<>();
        getConfig().getStringList("gui." + name + ".lore").forEach(s -> list.add(color(s)));
        return list;
    }
}
