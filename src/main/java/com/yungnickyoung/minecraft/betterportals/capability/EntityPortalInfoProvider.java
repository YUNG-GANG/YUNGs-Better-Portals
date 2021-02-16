package com.yungnickyoung.minecraft.betterportals.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityPortalInfoProvider implements ICapabilitySerializable<CompoundNBT> {
    private IEntityPortalInfo entityPortalInfo;


    public EntityPortalInfoProvider(IEntityPortalInfo entityPortalInfo) {
        this.entityPortalInfo = entityPortalInfo;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction direction) {
        return capability == CapabilityModule.ENTITY_PORTAL_INFO  ? LazyOptional.of(() -> (T) entityPortalInfo) : LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putBoolean("entityIsInPortal", entityPortalInfo.isInPortalFluid());
        nbt.putBoolean("entityIsInReclaimer", entityPortalInfo.isInReclaimer());
        nbt.putInt("teleportCooldown", entityPortalInfo.getTeleportCooldown());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        entityPortalInfo.setInPortalFluid(nbt.getBoolean("entityIsInPortal"));
        entityPortalInfo.setInReclaimer(nbt.getBoolean("entityIsInReclaimer"));
        entityPortalInfo.setTeleportCooldown(nbt.getInt("teleportCooldown"));
    }
}
