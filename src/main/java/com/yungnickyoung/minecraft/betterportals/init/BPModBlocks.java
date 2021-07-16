package com.yungnickyoung.minecraft.betterportals.init;

import com.yungnickyoung.minecraft.betterportals.block.PortalFluidBlock;
import com.yungnickyoung.minecraft.betterportals.block.ReclaimerBlock;
import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class BPModBlocks {
    public static FlowingFluidBlock PORTAL_FLUID_BLOCK = new PortalFluidBlock();
    public static Block RECLAIMER_BLOCK = new ReclaimerBlock();

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, BPModBlocks::registerBlocks);
    }

    /**
     * Registers portal block and liquid block.
     */
    private static void registerBlocks(final RegistryEvent.Register<Block> event) {
        event.getRegistry().register(PORTAL_FLUID_BLOCK.setRegistryName(new ResourceLocation(BPSettings.MOD_ID, "portal_fluid_block")));
        event.getRegistry().register(RECLAIMER_BLOCK.setRegistryName(new ResourceLocation(BPSettings.MOD_ID, "reclaimer_block")));
    }
}
