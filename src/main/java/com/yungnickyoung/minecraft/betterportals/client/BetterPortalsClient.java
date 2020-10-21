package com.yungnickyoung.minecraft.betterportals.client;

import com.yungnickyoung.minecraft.betterportals.init.BPBlocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class BetterPortalsClient {
    public static void subscribeClientEvents() {
//        MinecraftForge.EVENT_BUS.addListener(FluidRender::underwaterOverlay);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(BetterPortalsClient::onClientSetup);
    }

    @OnlyIn(Dist.CLIENT)
    public static void onClientSetup(FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntityRenderer(BPBlocks.RECLAIMER_TILE_ENTITY, ReclaimerTileEntityRenderer::new);
        DeferredWorkQueue.runLater(() -> {
            RenderTypeLookup.setRenderLayer(BPBlocks.PORTAL_FLUID_BLOCK, RenderType.getTranslucent());
            RenderTypeLookup.setRenderLayer(BPBlocks.PORTAL_FLUID, RenderType.getTranslucent());
            RenderTypeLookup.setRenderLayer(BPBlocks.PORTAL_FLUID_FLOWING, RenderType.getTranslucent());
            RenderTypeLookup.setRenderLayer(BPBlocks.RECLAIMER, RenderType.getCutout());
        });
    }
}
