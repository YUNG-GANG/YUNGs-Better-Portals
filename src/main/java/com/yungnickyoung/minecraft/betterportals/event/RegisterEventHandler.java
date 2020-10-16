package com.yungnickyoung.minecraft.betterportals.event;

import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.init.BPFeatures;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BPSettings.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegisterEventHandler {
    @SubscribeEvent
    public static void registerFeatureHandler(RegistryEvent.Register<Feature<?>> event) {
        registerFeature(BPFeatures.PORTAL_LAKE, "portal_lake");
        registerFeature(BPFeatures.MONOLITH, "monolith");
    }

    @SubscribeEvent
    public static void registerDecoratorHandler(RegistryEvent.Register<Placement<?>> event) {
        registerDecorator(BPFeatures.PORTAL_LAKE_PLACEMENT, "portal_lake");
        registerDecorator(BPFeatures.MONOLITH_PLACEMENT, "monolith");
    }

    private static void registerFeature(Feature<?> feature, String name) {
        Registry.register(Registry.FEATURE, new ResourceLocation(BPSettings.MOD_ID, name), feature);
    }


    private static void registerDecorator(Placement<?> placement, String name) {
        Registry.register(Registry.DECORATOR, new ResourceLocation(BPSettings.MOD_ID, name), placement);
    }
}
