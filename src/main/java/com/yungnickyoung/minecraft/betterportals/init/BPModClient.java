package com.yungnickyoung.minecraft.betterportals.init;

import com.yungnickyoung.minecraft.betterportals.client.OverlayRenderer;
import com.yungnickyoung.minecraft.betterportals.client.ReclaimerTileEntityRenderer;
import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class BPModClient {
    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(BPModClient::onClientSetup);
        MinecraftForge.EVENT_BUS.addListener(OverlayRenderer::renderUnderwaterOverlay);
        MinecraftForge.EVENT_BUS.addListener(OverlayRenderer::renderPortalOverlay);
        MinecraftForge.EVENT_BUS.addListener(OverlayRenderer::renderReclaimerOverlay);
//        MinecraftForge.EVENT_BUS.addListener(PortalPerturbRenderer::renderPortalPerturb);

        if (BPSettings.DEBUG_MODE) {
            MinecraftForge.EVENT_BUS.addListener(OverlayRenderer::renderDebugOverlay);
        }
    }

    /**
     * Binds TE renderer(s) and adds necessary render layers for portal fluid and reclaimer blocks.
     */
    private static void onClientSetup(FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntityRenderer(BPModTileEntities.RECLAIMER_TILE_ENTITY, ReclaimerTileEntityRenderer::new);
        event.enqueueWork(() -> {
            RenderTypeLookup.setRenderLayer(BPModFluids.PORTAL_FLUID, RenderType.getTranslucent());
            RenderTypeLookup.setRenderLayer(BPModFluids.PORTAL_FLUID_FLOWING, RenderType.getTranslucent());
            RenderTypeLookup.setRenderLayer(BPModBlocks.RECLAIMER_BLOCK, RenderType.getCutout()); // Cutout renders the TE even when the block is out of sight
        });
    }
}
