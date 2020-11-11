package com.yungnickyoung.minecraft.betterportals.capability;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlayerPortalInfoProvider implements ICapabilityProvider {
    private IPlayerPortalInfo playerPortalInfo;

    public PlayerPortalInfoProvider(IPlayerPortalInfo playerPortalInfo) {
        this.playerPortalInfo = playerPortalInfo;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction direction) {
        return capability == CapabilityModule.PLAYER_PORTAL_INFO  ? LazyOptional.of(() -> (T) playerPortalInfo) : LazyOptional.empty();
//        if (capability == BPCapabilities.PLAYER_PORTAL_INFO) {
//            if (instance == null) {
//                instance = new PlayerPortalInfo();
//            }
//            return LazyOptional.of(() -> instance).cast();
//        } else {
//            return LazyOptional.empty();
//        }
    }
}
