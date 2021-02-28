package com.yungnickyoung.minecraft.betterportals.capability;

import com.yungnickyoung.minecraft.betterportals.api.IEntityPortalInfo;
import net.minecraft.nbt.CompoundNBT;

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

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putBoolean("entityIsInPortal", this.isInPortalFluid());
        nbt.putBoolean("entityIsInReclaimer", this.isInReclaimer());
        nbt.putInt("teleportCooldown", this.getTeleportCooldown());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.setInPortalFluid(nbt.getBoolean("entityIsInPortal"));
        this.setInReclaimer(nbt.getBoolean("entityIsInReclaimer"));
        this.setTeleportCooldown(nbt.getInt("teleportCooldown"));
    }
}
