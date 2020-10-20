package com.yungnickyoung.minecraft.betterportals.init;

import com.yungnickyoung.minecraft.betterportals.block.PortalFluidBlock;
import com.yungnickyoung.minecraft.betterportals.block.ReclaimerBlock;
import com.yungnickyoung.minecraft.betterportals.block.ReclaimerTileEntity;
import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.fluid.PortalFluid;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.Rarity;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class BPBlocks {
    public static FlowingFluid PORTAL_FLUID;
    public static FlowingFluid PORTAL_FLUID_FLOWING;
    public static FlowingFluidBlock PORTAL_FLUID_BLOCK;
    public static ForgeFlowingFluid.Properties PORTAL_FLUID_PROPERTIES;

    public static Block RECLAIMER;

    public static TileEntityType<ReclaimerTileEntity> RECLAIMER_TILE_ENTITY;

    // Initialization
    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, BPBlocks::registerBlocks);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(TileEntityType.class, BPBlocks::registerTileEntities);
    }

    /**
     * Registers Portal Block and source/flowing Fluids.
     */
    public static void registerBlocks(final RegistryEvent.Register<Block> event) {
        BPBlocks.PORTAL_FLUID_PROPERTIES = new ForgeFlowingFluid.Properties(
            () -> BPBlocks.PORTAL_FLUID, () -> BPBlocks.PORTAL_FLUID_FLOWING,
            PortalFluid.PortalFluidAttributes.builder(
                new ResourceLocation(BPSettings.MOD_ID, "block/portal_fluid_still"),
                new ResourceLocation(BPSettings.MOD_ID, "block/portal_fluid_flowing")
            )
                .overlay(new ResourceLocation(BPSettings.MOD_ID, "block/portal_fluid_overlay"))
                .viscosity(6000)
                .translationKey("block.betterportals.portal_fluid")
                .color(0xEE190040)
                .luminosity(7)
                .rarity(Rarity.RARE)
        )
            .bucket(() -> BPItems.PORTAL_BUCKET)
            .block(() -> BPBlocks.PORTAL_FLUID_BLOCK);

        BPBlocks.PORTAL_FLUID = Registry.register(
            Registry.FLUID,
            new ResourceLocation(BPSettings.MOD_ID, "portal_fluid_still"),
            new PortalFluid.Source(BPBlocks.PORTAL_FLUID_PROPERTIES))
        ;
        BPBlocks.PORTAL_FLUID_FLOWING = Registry.register(
            Registry.FLUID,
            new ResourceLocation(BPSettings.MOD_ID, "portal_fluid_flowing"),
            new PortalFluid.Flowing(BPBlocks.PORTAL_FLUID_PROPERTIES)
        );
        BPBlocks.PORTAL_FLUID_BLOCK = Registry.register(
            Registry.BLOCK,
            new ResourceLocation(BPSettings.MOD_ID, "portal_fluid_block"),
            new PortalFluidBlock()
        );
        BPBlocks.RECLAIMER = Registry.register(
            Registry.BLOCK,
            new ResourceLocation(BPSettings.MOD_ID, "reclaimer_block"),
            new ReclaimerBlock()
        );
    }

    /**
     * Registers Reclaimer tile entity.
     */
    public static void registerTileEntities(final RegistryEvent.Register<TileEntityType<?>> event) {
        RECLAIMER_TILE_ENTITY = TileEntityType.Builder.create(ReclaimerTileEntity::new, BPBlocks.RECLAIMER).build(null);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(BPSettings.MOD_ID, "reclaimer"), RECLAIMER_TILE_ENTITY);
    }
}
