package com.yungnickyoung.minecraft.betterportals.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface IPlayerPortalInfo extends INBTSerializable<CompoundNBT> {
    // Getters
    int getPortalCounter();
    int getPortalCooldown();
    boolean isInPortal();

    // Setters
    void setPortalCounter(int counter);
    void setPortalCooldown(int cooldown);
    void setInPortal(boolean inPortal);

    // Misc
    default void offsetCounter(int offset) {
        setPortalCounter(getPortalCounter() + offset);
    }

    default void offsetCooldown(int offset) {
        setPortalCooldown(getPortalCooldown() + offset);
    }
}
