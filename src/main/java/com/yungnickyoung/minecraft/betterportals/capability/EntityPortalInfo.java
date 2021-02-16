package com.yungnickyoung.minecraft.betterportals.capability;

public class EntityPortalInfo implements IEntityPortalInfo {
    private boolean isInPortalFluid;
    private boolean isInReclaimer;
    private int teleportCooldown;

    @Override
    public boolean isInPortalFluid() {
        return isInPortalFluid;
    }

    @Override
    public void setInPortalFluid(boolean isInPortalFluid) {
        this.isInPortalFluid = isInPortalFluid;
    }

    @Override
    public boolean isInReclaimer() {
        return isInReclaimer;
    }

    @Override
    public void setInReclaimer(boolean isInReclaimer) {
        this.isInReclaimer = isInReclaimer;
    }

    @Override
    public int getTeleportCooldown() {
        return teleportCooldown;
    }

    @Override
    public void setTeleportCooldown(int teleportCooldown) {
        this.teleportCooldown = teleportCooldown;
    }
}
