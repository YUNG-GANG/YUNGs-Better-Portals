package com.yungnickyoung.minecraft.betterportals.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NBTCapabilityProvider<T extends INBTSerializable<CompoundNBT>> implements ICapabilitySerializable<CompoundNBT> {
    private final Capability<T> capability;
    private final T value;
    private final LazyOptional<T> lazyValue;

    public NBTCapabilityProvider(Capability<T> capability, T value) {
        this.capability = capability;
        this.value = value;
        this.lazyValue = LazyOptional.of(() -> this.value);
    }

    @Nonnull
    @Override
    public <U> LazyOptional<U> getCapability(@Nonnull Capability<U> capability, final @Nullable Direction side) {
        return this.capability.orEmpty(capability, lazyValue);
    }

    @Override
    public CompoundNBT serializeNBT() {
        return value.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        value.deserializeNBT(nbt);
    }
}
