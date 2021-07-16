package com.yungnickyoung.minecraft.betterportals.init;

import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.tileentity.ReclaimerTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class BPModTileEntities {
    public static TileEntityType<ReclaimerTileEntity> RECLAIMER_TILE_ENTITY;

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(TileEntityType.class, BPModTileEntities::registerTileEntities);
    }

    /**
     * Registers Reclaimer tile entity.
     */
    private static void registerTileEntities(final RegistryEvent.Register<TileEntityType<?>> event) {
        RECLAIMER_TILE_ENTITY = TileEntityType.Builder.create(ReclaimerTileEntity::new, BPModBlocks.RECLAIMER_BLOCK).build(null);
        event.getRegistry().register(RECLAIMER_TILE_ENTITY.setRegistryName(new ResourceLocation(BPSettings.MOD_ID, "reclaimer")));
    }
}
