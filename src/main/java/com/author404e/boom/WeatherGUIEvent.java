package com.author404e.boom;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class WeatherGUIEvent implements Listener {
    //GUI点击
    @EventHandler
    public void ClickGui(InventoryClickEvent event) {
        if (event.getView().getTitle().equalsIgnoreCase("§b天气切换")) {
            event.setCancelled(true);
            Player p = (Player) event.getWhoClicked();
            if (event.getCurrentItem() != null) switch (event.getCurrentItem().getType()) {
                case GRAY_DYE:
                    p.performCommand("bm sun");
                    event.getWhoClicked().closeInventory();
                    break;
                case BLUE_DYE:
                    p.performCommand("bm rain");
                    event.getWhoClicked().closeInventory();
                    break;
                case YELLOW_DYE:
                    p.performCommand("bm thunder");
                    event.getWhoClicked().closeInventory();
                    break;
                case WHITE_DYE:
                    p.performCommand("bm ls");
                    event.getWhoClicked().closeInventory();
                    break;
                default:
                    break;
            }
        }
    }
}
