package com.yungnickyoung.minecraft.betterportals.init;

import com.yungnickyoung.minecraft.betterportals.world.PortalLakePlacement;
import com.yungnickyoung.minecraft.betterportals.world.feature.MonolithFeature;
import com.yungnickyoung.minecraft.betterportals.world.feature.PortalLakeFeature;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureSpreadConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

public class BPFeatures {
    public static Feature<NoFeatureConfig> PORTAL_LAKE = new PortalLakeFeature(NoFeatureConfig.field_236558_a_);
    public static Placement<NoPlacementConfig> PORTAL_LAKE_PLACEMENT = new PortalLakePlacement(NoPlacementConfig.field_236555_a_);
    public static ConfiguredFeature<?, ?> CONFIGURED_PORTAL_LAKE = PORTAL_LAKE.withConfiguration(new NoFeatureConfig()).withPlacement(PORTAL_LAKE_PLACEMENT.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));

    public static Feature<NoFeatureConfig> MONOLITH = new MonolithFeature(NoFeatureConfig.field_236558_a_);
    public static ConfiguredFeature<?, ?> CONFIGURED_MONOLITH = MONOLITH.withConfiguration(new NoFeatureConfig()).withPlacement(Placement.field_242897_C.configure(new FeatureSpreadConfig(1)));
}
