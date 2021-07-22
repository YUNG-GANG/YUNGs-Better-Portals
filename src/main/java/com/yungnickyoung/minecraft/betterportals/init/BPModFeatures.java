package com.yungnickyoung.minecraft.betterportals.init;

import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.world.feature.MonolithFeature;
import com.yungnickyoung.minecraft.betterportals.world.feature.PortalLake2Feature;
import com.yungnickyoung.minecraft.betterportals.world.feature.PortalLakeFeature;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class BPModFeatures {
    /* Registry for deferred registration */
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, BPSettings.MOD_ID);

    /* Features */
    public static final RegistryObject<Feature<NoFeatureConfig>> PORTAL_LAKE = register("portal_lake", PortalLakeFeature::new);
    public static final RegistryObject<Feature<NoFeatureConfig>> PORTAL_LAKE_2 = register("portal_lake_2", PortalLake2Feature::new);
    public static final RegistryObject<Feature<NoFeatureConfig>> MONOLITH = register("monolith", MonolithFeature::new);

    public static void init() {
        FEATURES.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(BPModFeatures::commonSetup);
        MinecraftForge.EVENT_BUS.addListener(BPModFeatures::onBiomeLoad);
    }

    /**
     * Common setup. Registers configured features.
     */
    private static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(BPModConfiguredFeatures::registerConfiguredFeatures);
    }

    /**
     * Adds configured Portal Lake (aka Rift) and Monolith features to biomes.
     * Features are added to just about all biomes by default.
     * Spawning is handled later on when we have access to the dimension the feature is attempting to spawn in.
     */
    private static void onBiomeLoad(BiomeLoadingEvent event) {
        // Add dimensional rift features
        event.getGeneration().getFeatures(GenerationStage.Decoration.LAKES).add(() -> BPModConfiguredFeatures.CONFIGURED_PORTAL_LAKE);
        event.getGeneration().getFeatures(GenerationStage.Decoration.LAKES).add(() -> BPModConfiguredFeatures.CONFIGURED_PORTAL_LAKE_2);
        // Don't add monoliths to basalt deltas biome (there isn't really enough room for them to spawn properly)
        if (event.getName().toString().equals("minecraft:basalt_deltas")) {
            return;
        }
        // Add monolith features
        event.getGeneration().getFeatures(GenerationStage.Decoration.UNDERGROUND_STRUCTURES).add(() -> BPModConfiguredFeatures.CONFIGURED_MONOLITH);
    }

    private static <T extends Feature<?>> RegistryObject<T> register(String name, Supplier<T> feature) {
        return FEATURES.register(name, feature);
    }
}
