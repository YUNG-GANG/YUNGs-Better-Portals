package com.yungnickyoung.minecraft.betterportals.client;

import com.yungnickyoung.minecraft.betterportals.block.BlockModule;
import com.yungnickyoung.minecraft.betterportals.fluid.FluidModule;
import com.yungnickyoung.minecraft.betterportals.item.ItemModule;
import com.yungnickyoung.minecraft.betterportals.module.IModule;
import com.yungnickyoung.minecraft.betterportals.tileentity.TileEntityModule;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientModule implements IModule {
    public void init() {
        registerClientListeners();
    }

    private static void registerClientListeners() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientModule::onClientSetup);
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientModule::addColorHandlers);
        MinecraftForge.EVENT_BUS.addListener(OverlayRenderer::renderUnderwaterOverlay);
        MinecraftForge.EVENT_BUS.addListener(OverlayRenderer::renderPortalOverlay);
        MinecraftForge.EVENT_BUS.addListener(OverlayRenderer::renderReclaimerOverlay);
//        MinecraftForge.EVENT_BUS.addListener(PortalPerturbRenderer::renderPortalPerturb);
        MinecraftForge.EVENT_BUS.addListener(OverlayRenderer::renderDebugOverlay);
    }

    /**
     * Adds color mask to the dimensional plasma fluid bucket item.
     */
//    private static void addColorHandlers(ColorHandlerEvent.Item event) {
//        event.getItemColors().register(
//            (stack, index) -> FluidUtil.getFluidContained(stack)
//                .map(fstack -> fstack.getFluid().getAttributes().getColor(fstack))
//                .orElse(0xFFFFFFFF),
//            ItemModule.PORTAL_BUCKET
//        );
//    }

    /**
     * Binds TE renderer(s) and adds necessary render layers for portal fluid and reclaimer blocks.
     */
    private static void onClientSetup(FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntityRenderer(TileEntityModule.RECLAIMER_TILE_ENTITY, ReclaimerTileEntityRenderer::new);
        event.enqueueWork(() -> {
            RenderTypeLookup.setRenderLayer(BlockModule.PORTAL_FLUID_BLOCK, RenderType.getTranslucent());
            RenderTypeLookup.setRenderLayer(FluidModule.PORTAL_FLUID, RenderType.getTranslucent());
            RenderTypeLookup.setRenderLayer(FluidModule.PORTAL_FLUID_FLOWING, RenderType.getTranslucent());
            RenderTypeLookup.setRenderLayer(BlockModule.RECLAIMER_BLOCK, RenderType.getCutout()); // Cutout renders the TE even when the block is out of sight
        });
    }
}
