package com.yungnickyoung.minecraft.betterportals.client;

import com.yungnickyoung.minecraft.betterportals.BetterPortals;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class BetterPortalsClient {
    public static void subscribeClientEvents() {
//        MinecraftForge.EVENT_BUS.addListener(FluidRender::sugarWaterOverlay);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(BetterPortalsClient::onClientSetup);
    }

    @OnlyIn(Dist.CLIENT)
    public static void onClientSetup(FMLClientSetupEvent event) {
        DeferredWorkQueue.runLater(() -> {
            RenderTypeLookup.setRenderLayer(BetterPortals.PORTAL_FLUID_BLOCK, RenderType.getTranslucent());
            RenderTypeLookup.setRenderLayer(BetterPortals.PORTAL_FLUID, RenderType.getTranslucent());
            RenderTypeLookup.setRenderLayer(BetterPortals.PORTAL_FLUID_FLOWING, RenderType.getTranslucent());
        });
    }
}
