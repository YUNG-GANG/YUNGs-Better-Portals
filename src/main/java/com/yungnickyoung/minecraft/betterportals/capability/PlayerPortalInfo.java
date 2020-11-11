package com.yungnickyoung.minecraft.betterportals.capability;

import net.minecraft.nbt.CompoundNBT;

public class PlayerPortalInfo implements IPlayerPortalInfo {
    private int portalCounter;
    private int portalCooldown;
    private boolean isInPortal;

    @Override
    public int getPortalCounter() {
        return portalCounter;
    }

    @Override
    public int getPortalCooldown() {
        return portalCooldown;
    }

    @Override
    public boolean isInPortal() {
        return isInPortal;
    }

    @Override
    public void setPortalCounter(int counter) {
        this.portalCounter = counter;
    }

    @Override
    public void setPortalCooldown(int cooldown) {
        this.portalCooldown = cooldown;
    }

    @Override
    public void setInPortal(boolean inPortal) {
        this.isInPortal = inPortal;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();

        nbt.putInt("betterPortalCounter", this.getPortalCounter());
        nbt.putInt("betterPortalCooldown", this.getPortalCooldown());
        nbt.putBoolean("isInBetterPortal", this.isInPortal());

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.setPortalCounter(nbt.getInt("betterPortalCounter"));
        this.setPortalCooldown(nbt.getInt("betterPortalCooldown"));
        this.setInPortal(nbt.getBoolean("isInBetterPortal"));
    }
}
