package com.yungnickyoung.minecraft.betterportals.event;

import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.init.BPFeatures;
import net.minecraft.world.gen.GenerationStage;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BPSettings.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BiomeLoadingEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void biomeLoadingHandler(BiomeLoadingEvent event) {
        event.getGeneration().getFeatures(GenerationStage.Decoration.LAKES).add(
            () -> BPFeatures.CONFIGURED_PORTAL_LAKE
        );
        event.getGeneration().getFeatures(GenerationStage.Decoration.LAKES).add(
            () -> BPFeatures.CONFIGURED_MONOLITH
        );
    }
}
