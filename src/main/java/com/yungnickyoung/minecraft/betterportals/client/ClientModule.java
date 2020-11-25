package com.yungnickyoung.minecraft.betterportals.client;

import com.yungnickyoung.minecraft.betterportals.block.BlockModule;
import com.yungnickyoung.minecraft.betterportals.fluid.FluidModule;
import com.yungnickyoung.minecraft.betterportals.item.ItemModule;
import com.yungnickyoung.minecraft.betterportals.module.IModule;
import com.yungnickyoung.minecraft.betterportals.tileentity.TileEntityModule;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.FluidContainerColorer;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientModule implements IModule {
    public void init() {
        registerClientListeners();
    }

    private static void registerClientListeners() {
        //        MinecraftForge.EVENT_BUS.addListener(FluidRender::underwaterOverlay);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientModule::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientModule::addColorHandlers);
    }

    @OnlyIn(Dist.CLIENT)
    private static void addColorHandlers(ColorHandlerEvent.Item event) {
        event.getItemColors().register(new FluidContainerColorer(), ItemModule.PORTAL_BUCKET);
//        event.getItemColors().register((stack, index) -> {
//            int color = FluidUtil.getFluidContained(stack)
//                .map(fstack -> fstack.getFluid().getAttributes().getColor(fstack))
//                .orElse(0xFFFFFFFF);
//            return color;
//        }, ItemModule.PORTAL_BUCKET);
    }

    @OnlyIn(Dist.CLIENT)
    private static void onClientSetup(FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntityRenderer(TileEntityModule.RECLAIMER_TILE_ENTITY, ReclaimerTileEntityRenderer::new);
        DeferredWorkQueue.runLater(() -> {
            RenderTypeLookup.setRenderLayer(BlockModule.PORTAL_FLUID_BLOCK, RenderType.getTranslucent());
            RenderTypeLookup.setRenderLayer(FluidModule.PORTAL_FLUID, RenderType.getTranslucent());
            RenderTypeLookup.setRenderLayer(FluidModule.PORTAL_FLUID_FLOWING, RenderType.getTranslucent());
            RenderTypeLookup.setRenderLayer(BlockModule.RECLAIMER_BLOCK, RenderType.getCutout());
        });
    }
}
