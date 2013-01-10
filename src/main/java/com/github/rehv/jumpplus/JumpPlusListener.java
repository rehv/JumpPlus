package com.github.rehv.jumpplus;

import net.minecraft.server.v1_4_6.Entity;

import org.bukkit.Effect;
import org.bukkit.craftbukkit.v1_4_6.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

public class JumpPlusListener implements Listener {
    JumpPlus plugin;

    public JumpPlusListener(JumpPlus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        JumpPlusPlayer jumpPlayer = plugin.playerConfig.get(p.getDisplayName());
        if (p.hasPermission("jumpplus.use") && jumpPlayer.isEnabled()) {
            if (((CraftPlayer) p).getHandle().onGround) {
                jumpPlayer.setJumps(0);
                // p.sendMessage("Jumps = 0");
            } else if (jumpPlayer.getJumps() == 0) {
                jumpPlayer.setJumps(1);
                // p.sendMessage("Jumps = 1");
            }
        }
    }

    @EventHandler
    public void onPlayerCrouch(PlayerToggleSneakEvent event) {
        Player p = event.getPlayer();
        JumpPlusPlayer jumpPlayer = plugin.playerConfig.get(p.getDisplayName());
        Entity nmsPlayer = ((CraftPlayer) p).getHandle();
        if (p.hasPermission("jumpplus.use") && jumpPlayer.isEnabled() && !nmsPlayer.onGround) {
            if (event.isSneaking()) {
                Integer jumps = jumpPlayer.getJumps();
                if (jumps <= jumpPlayer.getMaxJumps() || p.hasPermission("jumpplus.bypass.limit")) {
                    if (jumps > (jumpPlayer.getMaxFreeJumps()) && !p.hasPermission("jumpplus.bypass.food")) {
                        p.setFoodLevel(p.getFoodLevel() - jumpPlayer.getJumpCost());
                    }
                    Vector velocity = p.getLocation().getDirection().multiply(jumpPlayer.getHorizontalSpeed());
                    velocity.setY(jumpPlayer.getVerticalSpeed());
                    p.setVelocity(velocity);
                    if (jumpPlayer.hasParticleEffect()) {
                        for (int i = 0; i < 9; i++) {
                            p.playEffect(p.getLocation(), Effect.SMOKE, i % 9);
                        }
                    }
                    jumpPlayer.setJumps(jumps + 1);
                    // p.sendMessage("Jumps = " + (jumps+1));
                }
            }
        } else if (nmsPlayer.onGround) {
            jumpPlayer.setJumps(0);
            // p.sendMessage("Jumps = 0");
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            JumpPlusPlayer jumpPlayer = plugin.playerConfig.get(p.getDisplayName());
            if (jumpPlayer != null) {
                Integer jumps = jumpPlayer.getJumps();
                if (event.getCause() == DamageCause.FALL && jumps > 1) {
                    if (p.hasPermission("jumpplus.bypass.gravity")) {
                        event.setCancelled(true);
                    } else {
                        event.setDamage(event.getDamage() - jumpPlayer.getFallModifier());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player p = event.getPlayer();
        plugin.playerConfig.put(p.getDisplayName(), new JumpPlusPlayer(plugin, p));
    }
}
