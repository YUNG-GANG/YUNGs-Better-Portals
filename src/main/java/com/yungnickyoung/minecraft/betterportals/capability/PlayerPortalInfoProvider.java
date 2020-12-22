package com.yungnickyoung.minecraft.betterportals.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerPortalInfoProvider implements ICapabilitySerializable<CompoundNBT> {
    private IPlayerPortalInfo playerPortalInfo;

    public PlayerPortalInfoProvider(IPlayerPortalInfo playerPortalInfo) {
        this.playerPortalInfo = playerPortalInfo;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction direction) {
        return capability == CapabilityModule.PLAYER_PORTAL_INFO  ? LazyOptional.of(() -> (T) playerPortalInfo) : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();

        // Reclaimer tags
        nbt.putInt("reclaimerCounter", playerPortalInfo.getReclaimerCounter());
        nbt.putInt("reclaimerCooldown", playerPortalInfo.getReclaimerCooldown());
        nbt.putBoolean("isInReclaimer", playerPortalInfo.isInReclaimer());

        // Portal fluid tags
        nbt.putInt("portalFluidCounter", playerPortalInfo.getPortalFluidCounter());
        nbt.putInt("portalFluidCooldown", playerPortalInfo.getPortalFluidCooldown());
        nbt.putBoolean("isInPortalFluid", playerPortalInfo.isInPortalFluid());

        // Client only
        nbt.putFloat("timeInPortalFluid", playerPortalInfo.getTimeInPortalFluid());
        nbt.putFloat("prevTimeInPortalFluid", playerPortalInfo.getPrevTimeInPortalFluid());
        nbt.putFloat("timeInReclaimer", playerPortalInfo.getTimeInReclaimer());
        nbt.putFloat("prevTimeInReclaimer", playerPortalInfo.getPrevTimeInReclaimer());

        // DEBUG
        nbt.putInt("DEBUGclientTickCounter", playerPortalInfo.getDEBUGclientTickCounter());
        nbt.putInt("DEBUGserverTickCounter", playerPortalInfo.getDEBUGserverTickCounter());
        nbt.putInt("DEBUGportalCounter", playerPortalInfo.getDEBUGportalCounter());

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        // Reclaimer tags
        playerPortalInfo.setReclaimerCounter(nbt.getInt("reclaimerCounter"));
        playerPortalInfo.setReclaimerCooldown(nbt.getInt("reclaimerCooldown"));
        playerPortalInfo.setInReclaimer(nbt.getBoolean("isInReclaimer"));

        // Portal fluid tags
        playerPortalInfo.setPortalFluidCounter(nbt.getInt("portalFluidCounter"));
        playerPortalInfo.setPortalFluidCooldown(nbt.getInt("portalFluidCooldown"));
        playerPortalInfo.setInPortalFluid(nbt.getBoolean("isInPortalFluid"));

        // Client only
        playerPortalInfo.setTimeInPortalFluid(nbt.getFloat("timeInPortalFluid"));
        playerPortalInfo.setPrevTimeInPortalFluid(nbt.getFloat("prevTimeInPortalFluid"));
        playerPortalInfo.setTimeInReclaimer(nbt.getFloat("timeInReclaimer"));
        playerPortalInfo.setPrevTimeInReclaimer(nbt.getFloat("prevTimeInReclaimer"));

        // DEBUG
        playerPortalInfo.setDEBUGclientTickCounter(nbt.getInt("DEBUGclientTickCounter"));
        playerPortalInfo.setDEBUGserverTickCounter(nbt.getInt("DEBUGserverTickCounter"));
        playerPortalInfo.setDEBUGportalCounter(nbt.getInt("DEBUGportalCounter"));
    }
}
