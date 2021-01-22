package com.yungnickyoung.minecraft.betterportals.world;

import com.google.common.collect.ImmutableSet;
import com.yungnickyoung.minecraft.betterportals.block.BlockModule;
import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.module.IModule;
import com.yungnickyoung.minecraft.betterportals.world.feature.MonolithFeature;
import com.yungnickyoung.minecraft.betterportals.world.feature.PortalLakeFeature;
import com.yungnickyoung.minecraft.betterportals.world.placement.PortalLakePlacement;
import net.minecraft.util.ResourceLocation;
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
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class WorldGenModule implements IModule {
    // Portal Lake
    public static Feature<BlockStateFeatureConfig> PORTAL_LAKE = new PortalLakeFeature(BlockStateFeatureConfig.field_236455_a_);
    public static Placement<NoPlacementConfig> PORTAL_LAKE_PLACEMENT = new PortalLakePlacement(NoPlacementConfig.CODEC);
    public static ConfiguredFeature<?, ?> CONFIGURED_PORTAL_LAKE = PORTAL_LAKE.withConfiguration(new BlockStateFeatureConfig(BlockModule.PORTAL_FLUID_BLOCK.getDefaultState())).withPlacement(PORTAL_LAKE_PLACEMENT.configure(IPlacementConfig.NO_PLACEMENT_CONFIG));
    public static PointOfInterestType PORTAL_LAKE_POI = PointOfInterestType.registerBlockStates(new PointOfInterestType(
        "portal_lake",
        ImmutableSet.copyOf(BlockModule.PORTAL_FLUID_BLOCK.getStateContainer().getValidStates()),
        1,
        1
    ));

    // Monolith
    public static Feature<NoFeatureConfig> MONOLITH = new MonolithFeature(NoFeatureConfig.field_236558_a_);
    public static ConfiguredFeature<?, ?> CONFIGURED_MONOLITH = MONOLITH.withConfiguration(new NoFeatureConfig()).withPlacement(Placement.COUNT_MULTILAYER.configure(new FeatureSpreadConfig(1)));

    public void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Feature.class, WorldGenModule::registerFeatures);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Placement.class, WorldGenModule::registerDecorators);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(PointOfInterestType.class, WorldGenModule::registerPointsofInterest);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, WorldGenModule::onBiomeLoad);
    }

    /**
     * Registers Portal Lake (aka Rift) and Monolith features.
     */
    private static void registerFeatures(final RegistryEvent.Register<Feature<?>> event) {
        event.getRegistry().register(PORTAL_LAKE.setRegistryName(new ResourceLocation(BPSettings.MOD_ID, "portal_lake")));
        event.getRegistry().register(MONOLITH.setRegistryName(new ResourceLocation(BPSettings.MOD_ID, "monolith")));
    }

    /**
     * Registers Portal Lake (aka Rift) decorator.
     */
    private static void registerDecorators(RegistryEvent.Register<Placement<?>> event) {
        event.getRegistry().register(PORTAL_LAKE_PLACEMENT.setRegistryName(new ResourceLocation(BPSettings.MOD_ID, "portal_lake")));
    }

    /**
     * Registers Portal Lake (aka Rift) decorator.
     */
    private static void registerPointsofInterest(RegistryEvent.Register<PointOfInterestType> event) {
        event.getRegistry().register(PORTAL_LAKE_POI.setRegistryName(new ResourceLocation(BPSettings.MOD_ID, "portal_lake")));
    }

    /**
     * Adds configured Portal Lake (aka Rift) and Monolith features to biomes.
     */
    private static void onBiomeLoad(BiomeLoadingEvent event) {
        // Add dimensional rift features
        event.getGeneration().getFeatures(GenerationStage.Decoration.LAKES).add(
            () -> CONFIGURED_PORTAL_LAKE
        );
        // Don't add monoliths to basalt deltas biome (there isn't really enough room for them to spawn)
        if (event.getName().toString().equals("minecraft:basalt_deltas")) {
            return;
        }
        // Add monolith features
        event.getGeneration().getFeatures(GenerationStage.Decoration.UNDERGROUND_STRUCTURES).add(
            () -> CONFIGURED_MONOLITH
        );
    }
}
