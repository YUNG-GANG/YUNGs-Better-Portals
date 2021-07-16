package com.yungnickyoung.minecraft.betterportals.init;

import com.google.common.collect.ImmutableSet;
import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.world.feature.MonolithFeature;
import com.yungnickyoung.minecraft.betterportals.world.feature.PortalLake2Feature;
import com.yungnickyoung.minecraft.betterportals.world.feature.PortalLakeFeature;
import com.yungnickyoung.minecraft.betterportals.world.placement.PortalLakePlacement;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class BPModWorldGen {
    // Portal Lake features
    public static Feature<NoFeatureConfig> PORTAL_LAKE = new PortalLakeFeature(NoFeatureConfig.field_236558_a_);
    public static Feature<NoFeatureConfig> PORTAL_LAKE_2 = new PortalLake2Feature(NoFeatureConfig.field_236558_a_);
    public static Placement<NoPlacementConfig> PORTAL_LAKE_PLACEMENT = new PortalLakePlacement(NoPlacementConfig.CODEC);
    public static ConfiguredFeature<?, ?> CONFIGURED_PORTAL_LAKE = PORTAL_LAKE.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(PORTAL_LAKE_PLACEMENT.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
    public static ConfiguredFeature<?, ?> CONFIGURED_PORTAL_LAKE_2 = PORTAL_LAKE_2.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(PORTAL_LAKE_PLACEMENT.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));

    // Portal Lake POI
    public static PointOfInterestType PORTAL_LAKE_POI = PointOfInterestType.registerBlockStates(new PointOfInterestType(
        "portal_lake",
        ImmutableSet.copyOf(BPModBlocks.PORTAL_FLUID_BLOCK.getStateContainer().getValidStates()),
        1,
        1
    ));

    // Monolith
    public static Feature<NoFeatureConfig> MONOLITH = new MonolithFeature(NoFeatureConfig.field_236558_a_);
    public static ConfiguredFeature<?, ?> CONFIGURED_MONOLITH = MONOLITH.withConfiguration(new NoFeatureConfig()).withPlacement(Placement.COUNT_MULTILAYER.configure(new FeatureSpreadConfig(1)));

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Feature.class, BPModWorldGen::registerFeatures);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Placement.class, BPModWorldGen::registerDecorators);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(PointOfInterestType.class, BPModWorldGen::registerPointsofInterest);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(BPModWorldGen::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, BPModWorldGen::onBiomeLoad);
    }

    /**
     * Registers Portal Lake (aka Rift) and Monolith features.
     */
    private static void registerFeatures(final RegistryEvent.Register<Feature<?>> event) {
        event.getRegistry().register(PORTAL_LAKE.setRegistryName(new ResourceLocation(BPSettings.MOD_ID, "portal_lake")));
        event.getRegistry().register(PORTAL_LAKE_2.setRegistryName(new ResourceLocation(BPSettings.MOD_ID, "portal_lake_2")));
        event.getRegistry().register(MONOLITH.setRegistryName(new ResourceLocation(BPSettings.MOD_ID, "monolith")));
    }

    /**
     * Common setup. Registers configured features.
     */
    private static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Registry<ConfiguredFeature<?, ?>> registry = WorldGenRegistries.CONFIGURED_FEATURE;
            Registry.register(registry, new ResourceLocation(BPSettings.MOD_ID, "portal_lake"), CONFIGURED_PORTAL_LAKE);
            Registry.register(registry, new ResourceLocation(BPSettings.MOD_ID, "portal_lake_2"), CONFIGURED_PORTAL_LAKE_2);
            Registry.register(registry, new ResourceLocation(BPSettings.MOD_ID, "monolith"), CONFIGURED_MONOLITH);
        });
    }

    /**
     * Registers Portal Lake (aka Rift) decorator.
     */
    private static void registerDecorators(RegistryEvent.Register<Placement<?>> event) {
        event.getRegistry().register(PORTAL_LAKE_PLACEMENT.setRegistryName(new ResourceLocation(BPSettings.MOD_ID, "portal_lake")));
    }

    /**
     * Registers Portal Lake (aka Rift) Point of Interest for quickly locating the nearest portal fluid block when teleporting.
     */
    private static void registerPointsofInterest(RegistryEvent.Register<PointOfInterestType> event) {
        event.getRegistry().register(PORTAL_LAKE_POI.setRegistryName(new ResourceLocation(BPSettings.MOD_ID, "portal_lake")));
    }

    /**
     * Adds configured Portal Lake (aka Rift) and Monolith features to biomes.
     * Features are added to just about all biomes by default.
     * Spawning is handled later on when we have access to the dimension the feature is attempting to spawn in.
     */
    private static void onBiomeLoad(BiomeLoadingEvent event) {
        // Add dimensional rift features
        event.getGeneration().getFeatures(GenerationStage.Decoration.LAKES).add(() -> CONFIGURED_PORTAL_LAKE);
        event.getGeneration().getFeatures(GenerationStage.Decoration.LAKES).add(() -> CONFIGURED_PORTAL_LAKE_2);
        // Don't add monoliths to basalt deltas biome (there isn't really enough room for them to spawn properly)
        if (event.getName().toString().equals("minecraft:basalt_deltas")) {
            return;
        }
        // Add monolith features
        event.getGeneration().getFeatures(GenerationStage.Decoration.UNDERGROUND_STRUCTURES).add(
            () -> CONFIGURED_MONOLITH
        );
    }
}
