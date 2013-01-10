package com.github.rehv.jumpplus;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class JumpPlusPlayer {
    private Double hSpeed = null, vSpeed = null;
    private Integer maxFreeJumps = null, maxJumps = null, jumpCost = null, fallModifier = null, jumps = 0;
    private Boolean particleEffect = null, enable = null;

    public JumpPlusPlayer(JumpPlus plugin, Player p) {
        loadPermissions(p, plugin);
        fillConfig(plugin);
    }

    protected void loadPermissions(Player p, JumpPlus plugin) {
        Set<PermissionAttachmentInfo> perms = new HashSet<PermissionAttachmentInfo>();
        if (plugin.usingPEX) {
            PermissionUser user = PermissionsEx.getUser(p);
            String world = p.getWorld().getName();
            PermissionAttachment attach = new PermissionAttachment(plugin, p);
            for (String perm : user.getPermissions(world)) {
                String expression = user.getMatchingExpression(perm, world);
                perms.add(new PermissionAttachmentInfo(p, perm, attach, user.explainExpression(expression)));
            }
        } else {
            perms = p.getEffectivePermissions();
        }
        String[] aux;
        for (PermissionAttachmentInfo attach : perms) {
            String perm = attach.getPermission();
            if (perm.contains("jumpplus.config.")) {
                aux = perm.split("jumpplus.config.");
                aux = aux[1].split("-");
                if (aux[0].equals("hspeed")) {
                    hSpeed = Double.parseDouble(aux[1]);
                } else if (aux[0].equals("vspeed")) {
                    vSpeed = Double.parseDouble(aux[1]);
                } else if (aux[0].equals("maxjumps")) {
                    maxJumps = Integer.parseInt(aux[1]);
                } else if (aux[0].equals("maxfreejumps")) {
                    maxFreeJumps = Integer.parseInt(aux[1]);
                } else if (aux[0].equals("jumpcost")) {
                    jumpCost = Integer.parseInt(aux[1]);
                } else if (aux[0].equals("fallmodifier")) {
                    fallModifier = Integer.parseInt(aux[1]);
                } else if (aux[0].equals("particleeffect")) {
                    particleEffect = Boolean.parseBoolean(aux[1]);
                } else if (aux[0].equals("defaultstate")) {
                    enable = Boolean.parseBoolean(aux[1]);
                }
            }
        }
    }

    protected void fillConfig(JumpPlus plugin) {
        if (hSpeed == null) {
            hSpeed = plugin.hSpeed;
        }
        if (vSpeed == null) {
            vSpeed = plugin.vSpeed;
        }
        if (maxJumps == null) {
            maxJumps = plugin.maxJumps;
        }
        if (maxFreeJumps == null) {
            maxFreeJumps = plugin.maxFreeJumps;
        }
        if (jumpCost == null) {
            jumpCost = plugin.jumpCost;
        }
        if (fallModifier == null) {
            fallModifier = plugin.fallModifier;
        }
        if (particleEffect == null) {
            particleEffect = plugin.particleEffect;
        }
        if (enable == null) {
            enable = plugin.defaultState;
        }
    }

    protected double getHorizontalSpeed() {
        return hSpeed;
    }

    protected double getVerticalSpeed() {
        return vSpeed;
    }

    public int getMaxJumps() {
        return maxJumps;
    }

    public int getMaxFreeJumps() {
        return maxFreeJumps;
    }

    public int getJumpCost() {
        return jumpCost;
    }

    public int getJumps() {
        return jumps;
    }

    public void setJumps(int jumps) {
        this.jumps = jumps;
    }

    public int getFallModifier() {
        return fallModifier;
    }

    public boolean hasParticleEffect() {
        return particleEffect;
    }

    public Boolean isEnabled() {
        return enable;
    }

    public void setEnabled(Boolean enable) {
        this.enable = enable;
    }
}
