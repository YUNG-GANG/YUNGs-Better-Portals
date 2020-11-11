package com.yungnickyoung.minecraft.betterportals.block;

import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.module.IModule;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class BlockModule implements IModule {
    public static FlowingFluidBlock PORTAL_FLUID_BLOCK;
    public static Block RECLAIMER_BLOCK;

    @Override
    public void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, BlockModule::registerBlocks);
    }

    /**
     * Registers portal block and liquid block.
     */
    private static void registerBlocks(final RegistryEvent.Register<Block> event) {
        PORTAL_FLUID_BLOCK = Registry.register(
            Registry.BLOCK,
            new ResourceLocation(BPSettings.MOD_ID, "portal_fluid_block"),
            new PortalFluidBlock()
        );
        RECLAIMER_BLOCK = Registry.register(
            Registry.BLOCK,
            new ResourceLocation(BPSettings.MOD_ID, "reclaimer_block"),
            new ReclaimerBlock()
        );
    }
}
