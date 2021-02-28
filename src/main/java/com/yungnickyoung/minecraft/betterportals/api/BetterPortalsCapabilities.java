package com.yungnickyoung.minecraft.betterportals.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class BetterPortalsCapabilities {
    @CapabilityInject(IPlayerPortalInfo.class)
    public static Capability<IPlayerPortalInfo> PLAYER_PORTAL_INFO = null;

    @CapabilityInject(IEntityPortalInfo.class)
    public static Capability<IEntityPortalInfo> ENTITY_PORTAL_INFO = null;
}
