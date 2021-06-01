package com.author404e.boom;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.EulerAngle;

import static com.author404e.boom.Tool.*;

public class Stick implements Listener {
    //生成规则盔甲架
    @EventHandler
    public void es(CreatureSpawnEvent event) {
        if (event.getEntityType().equals(EntityType.ARMOR_STAND)
                && event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.DEFAULT)) {
            ArmorStand as = (ArmorStand) event.getEntity();
            as.setArms(true);
            EulerAngle ea = new EulerAngle(0, 0, 0);
            as.setHeadPose(ea);
            as.setBodyPose(ea);
            as.setLeftArmPose(new EulerAngle(-0.1746, 0, -0.1746));
            as.setRightArmPose(new EulerAngle(-0.1746, 0, 0.1746));
            as.setLeftLegPose(ea);
            as.setRightLegPose(ea);
            Location location = as.getLocation();
            double yaw = location.getYaw();
            if (yaw <= -157.5 || yaw >= 157.5)
                location.setYaw(180);
            else if (yaw <= -112.5)
                location.setYaw(-135);
            else if (yaw <= -67.5)
                location.setYaw(-90);
            else if (yaw <= -22.5)
                location.setYaw(-45);
            else if (yaw >= 112.5)
                location.setYaw(135);
            else if (yaw >= 67.5)
                location.setYaw(90);
            else if (yaw >= 22.5)
                location.setYaw(45);
            else
                location.setYaw(0);
            as.teleport(location);
        }
    }

    //调试棒左键
    @EventHandler
    public void edbee(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player p = (Player) event.getDamager();
        if (event.getEntityType().equals(EntityType.ARMOR_STAND) && p.hasPermission("boom.stick") && isHoldStick(p)) {
            event.setCancelled(true);
            ArmorStand as = (ArmorStand) event.getEntity();
            send(p, full("rotate").replace("{rotate}", rotateArmorstand(as)));
        }
    }

    //盔甲架控制
    @EventHandler
    public void pasme(PlayerArmorStandManipulateEvent event) {
        Player p = event.getPlayer();
        if (p.hasPermission("boom.stick") && isHoldStick(p)) {
            ArmorStand a = event.getRightClicked();
            event.setCancelled(true);
            //潜行交互切换盔甲架为不可交互
            if (p.isSneaking()) {
                a.setMarker(true);
                send(p, full("armorstand.marker.enable"));
            }
            //交互盔甲架切换可见
            else {
                if (a.isVisible()) { //切换为不可见
                    a.setVisible(false);
                    send(p, full("armorstand.visible.enable"));
                } else { //切换为可见
                    a.setVisible(true);
                    send(p, full("armorstand.visible.disable"));
                }
            }
        }
    }

    @EventHandler
    public void pie(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (isHoldStick(p)
                && p.hasPermission("boom.stick")) {
            //潜行交互设置可交互
            if (p.isSneaking()) {
                event.setCancelled(true);
                for (Entity e : p.getNearbyEntities(1D, 1D, 1D)) {
                    if (e instanceof ArmorStand
                            && ((ArmorStand) e).isMarker()) {
                        ((ArmorStand) e).setMarker(false);
                        e.setGravity(false);
                        send(p, full("armorstand.marker.disable"));
                        break;
                    }
                }
            }
            //交互方块摆正其中的盔甲架
            else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                Block b = event.getClickedBlock();
                if (b == null) return;
                event.setCancelled(true);
                Location location = new Location(b.getWorld(), b.getX() + 0.5, b.getY(), b.getZ() + 0.5);
                for (Entity e : b.getWorld().getNearbyEntities(location, 0.5, 0.5, 0.5)) {
                    if (e.getType().equals(EntityType.ARMOR_STAND))
                        send(p, full("rotate").replace("{rotate}", rotateArmorstand((ArmorStand) e)));
                    break;
                }
            }
        }
    }

    //展示框控制
    @EventHandler
    public void piee(PlayerInteractEntityEvent event) {
        Player p = event.getPlayer();
        if (event.getRightClicked().getType().equals(EntityType.ITEM_FRAME)
                && p.hasPermission("boom.stick")
                && isHoldStick(p)) {
            ItemFrame e = (ItemFrame) event.getRightClicked();
            //潜行交互切换展示框可破坏
            if (p.isSneaking()) {
                event.setCancelled(true);
                if (!e.isFixed()) { //切换为不可破坏
                    e.setFixed(true);
                    send(p, full("itemframe.fixed.enable"));
                } else { //切换为可破坏
                    e.setFixed(false);
                    send(p, full("itemframe.fixed.disable"));
                }
            }
            //交互展示框切换可见
            else {
                event.setCancelled(true);
                if (e.isVisible()) { //切换为不可见
                    e.setVisible(false);
                    send(p, full("itemframe.visible.enable"));
                } else { //切换为可见
                    e.setVisible(true);
                    send(p, full("itemframe.visible.disable"));
                }
            }
        }
    }

    //盔甲架旋转
    private String rotateArmorstand(ArmorStand as) {
        Location l = as.getLocation();
        double yaw = l.getYaw();
        boolean b = false;
        for (double d = -135; d <= 180; d += 45)
            if (yaw < d) {
                yaw = d;
                b = true;
                break;
            }
        if (!b) yaw = -135;
        l.setYaw((float) yaw);
        as.teleport(l);
        as.getWorld().spawnParticle(Particle.END_ROD, l, 1, 0, 0, 0, 0);
        return String.valueOf(yaw);
    }
}
