package com.github.rehv.jumpplus;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * TO DO:
 * add some jump presets and scheduler for events
 * calculate direction of the smoke
 */

public class JumpPlus extends JavaPlugin {
    private Configuration config;
    protected double hSpeed, vSpeed;
    protected int maxFreeJumps, maxJumps, jumpCost, fallModifier, timer;
    protected boolean particleEffect, usingPEX, defaultState;
    protected Map<String, JumpPlusPlayer> playerConfig;

    @SuppressWarnings("deprecation")
    @Override
    public void onEnable() {
        if (!loadConfig()) {
            getLogger().severe("Something wrong with the config! Disabling!");
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            usingPEX = checkPermissionsEx();
            getServer().getPluginManager().registerEvents(new JumpPlusListener(this), this);
            playerConfig = new HashMap<String, JumpPlusPlayer>();
            loadPlayerConfig();
            if (timer > 0) {
                getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
                    public void run() {
                        loadPlayerConfig();
                    }
                }, (timer * 60 * 20L), (timer * 60 * 20L));
            }
        }
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("jptoggle")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                JumpPlusPlayer jumpPlayer = playerConfig.get(p.getDisplayName());
                if (jumpPlayer.isEnabled()) {
                    jumpPlayer.setEnabled(false);
                    p.sendMessage(ChatColor.RED + "JumpPlus disabled!");
                } else {
                    jumpPlayer.setEnabled(true);
                    p.sendMessage(ChatColor.GREEN + "JumpPlus enabled!");
                }
            } else {
                sender.sendMessage("This command can only be executed by a player!");
            }
        }
        if (cmd.getName().equalsIgnoreCase("jpreload")) {
            if (sender.hasPermission("jumpplus.reload")) {
                if (!loadConfig()) {
                    getLogger().severe("Something wrong with the config! Disabling!");
                    Bukkit.getPluginManager().disablePlugin(this);
                } else {
                    loadPlayerConfig();
                    sender.sendMessage("[" + getName() + "] All configs were reloaded successfuly!");
                }
            }
        }
        return true;
    }

    private boolean loadConfig() {
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }

        reloadConfig();
        config = getConfig();

        if (config.isDouble("speed.horizontal")) {
            hSpeed = config.getDouble("speed.horizontal");
        } else if (config.isInt("speed.horizontal")) {
            hSpeed = config.getInt("speed.horizontal");
        } else {
            return false;
        }
        if (config.isDouble("speed.vertical")) {
            vSpeed = config.getDouble("speed.vertical");
        } else if (config.isInt("speed.vertical")) {
            vSpeed = config.getInt("speed.vertical");
        } else {
            return false;
        }
        if (config.isInt("maxFreeJumps")) {
            maxFreeJumps = config.getInt("maxFreeJumps");
        } else {
            return false;
        }
        if (config.isInt("maxJumps")) {
            maxJumps = config.getInt("maxJumps");
        } else {
            return false;
        }
        if (config.isInt("jumpCost")) {
            jumpCost = config.getInt("jumpCost");
        } else {
            return false;
        }
        if (config.isInt("fallModifier")) {
            fallModifier = config.getInt("fallModifier");
        } else {
            return false;
        }
        if (config.isBoolean("particleEffect")) {
            particleEffect = config.getBoolean("particleEffect");
        } else {
            return false;
        }
        if (config.isInt("reloadTimer")) {
            timer = config.getInt("reloadTimer");
        } else {
            timer = 5;
        }
        if (config.isBoolean("defaultState")) {
            defaultState = config.getBoolean("defaultState");
        } else {
            defaultState = true;
        }
        return true;
    }

    private void loadPlayerConfig() {
        for (Player p : getServer().getOnlinePlayers()) {
            playerConfig.put(p.getDisplayName(), new JumpPlusPlayer(this, p));
        }
    }

    private boolean checkPermissionsEx() {
        if (getServer().getPluginManager().getPlugin("PermissionsEx") != null) {
            return true;
        } else {
            return false;
        }
    }
}
