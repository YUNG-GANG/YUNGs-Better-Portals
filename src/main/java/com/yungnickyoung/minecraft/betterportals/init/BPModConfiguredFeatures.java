package com.yungnickyoung.minecraft.betterportals.init;

import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureSpreadConfig;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

public class BPModConfiguredFeatures {
    // Portal lakes
    public static final ConfiguredFeature<?, ?> CONFIGURED_PORTAL_LAKE = BPModFeatures.PORTAL_LAKE.get()
        .withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG)
        .withPlacement(BPModPlacements.PORTAL_LAKE_PLACEMENT.get()
            .configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
    public static final ConfiguredFeature<?, ?> CONFIGURED_PORTAL_LAKE_2 = BPModFeatures.PORTAL_LAKE_2.get().
        withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).
        withPlacement(BPModPlacements.PORTAL_LAKE_PLACEMENT.get()
            .configure(IPlacementConfig.NO_PLACEMENT_CONFIG));

    // Monoliths
    public static final ConfiguredFeature<?, ?> CONFIGURED_MONOLITH = BPModFeatures.MONOLITH.get()
        .withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG)
        .withPlacement(Placement.COUNT_MULTILAYER
            .configure(new FeatureSpreadConfig(1)));

    public static void registerConfiguredFeatures() {
        Registry<ConfiguredFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_FEATURE;
        Registry.register(registry, new ResourceLocation(BPSettings.MOD_ID, "portal_lake"), CONFIGURED_PORTAL_LAKE);
        Registry.register(registry, new ResourceLocation(BPSettings.MOD_ID, "portal_lake_2"), CONFIGURED_PORTAL_LAKE_2);
        Registry.register(registry, new ResourceLocation(BPSettings.MOD_ID, "monolith"), CONFIGURED_MONOLITH);

    }
}
