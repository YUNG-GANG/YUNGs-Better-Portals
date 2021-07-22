package com.yungnickyoung.minecraft.betterportals.init;

import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.world.placement.PortalLakePlacement;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class BPModPlacements {
    /* Registry for deferred registration */
    public static final DeferredRegister<Placement<?>> DECORATORS = DeferredRegister.create(ForgeRegistries.DECORATORS, BPSettings.MOD_ID);

    /* Placements */
    public static final RegistryObject<Placement<NoPlacementConfig>> PORTAL_LAKE_PLACEMENT = register("portal_lake", PortalLakePlacement::new);

    public static void init() {
        DECORATORS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private static <T extends Placement<?>> RegistryObject<T> register(String name, Supplier<T> feature) {
        return DECORATORS.register(name, feature);
    }
}
