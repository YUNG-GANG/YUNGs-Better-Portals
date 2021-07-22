package com.yungnickyoung.minecraft.betterportals.init;

import com.google.common.collect.ImmutableSet;
import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class BPModPOIs {
     /* POIs */
    public static final PointOfInterestType PORTAL_LAKE_POI = PointOfInterestType.registerBlockStates(new PointOfInterestType(
        "portal_lake",
        ImmutableSet.copyOf(BPModBlocks.PORTAL_FLUID_BLOCK.getStateContainer().getValidStates()),
        1,
        1
    ));

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(PointOfInterestType.class, BPModPOIs::register);
    }

    private static void register(final RegistryEvent.Register<PointOfInterestType> event) {
        event.getRegistry().register(PORTAL_LAKE_POI.setRegistryName(new ResourceLocation(BPSettings.MOD_ID, "portal_lake")));
    }
}
