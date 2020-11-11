package com.yungnickyoung.minecraft.betterportals.tileentity;

import com.yungnickyoung.minecraft.betterportals.block.BlockModule;
import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.module.IModule;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class TileEntityModule implements IModule {
    public static TileEntityType<ReclaimerTileEntity> RECLAIMER_TILE_ENTITY;

    @Override
    public void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(TileEntityType.class, TileEntityModule::registerTileEntities);
    }

    /**
     * Registers Reclaimer tile entity.
     */
    private static void registerTileEntities(final RegistryEvent.Register<TileEntityType<?>> event) {
        RECLAIMER_TILE_ENTITY = TileEntityType.Builder.create(ReclaimerTileEntity::new, BlockModule.RECLAIMER_BLOCK).build(null);
        Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(BPSettings.MOD_ID, "reclaimer"), RECLAIMER_TILE_ENTITY);
    }
}
