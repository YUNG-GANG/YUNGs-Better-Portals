package com.yungnickyoung.minecraft.betterportals.item;

import com.yungnickyoung.minecraft.betterportals.block.BlockModule;
import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.fluid.FluidModule;
import com.yungnickyoung.minecraft.betterportals.module.IModule;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ItemModule implements IModule {
    public static Item PORTAL_BUCKET;
    public static Item RECLAIMER;

    public void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, ItemModule::registerItems);
    }

    /**
     * Registers Portal Fluid Bucket and Reclaimer items.
     */
    private static void registerItems(RegistryEvent.Register<Item> event) {
        PORTAL_BUCKET = Registry.register(
            Registry.ITEM,
            new ResourceLocation(BPSettings.MOD_ID, "portal_fluid_bucket"),
            new BucketItem(
                () -> FluidModule.PORTAL_FLUID,
                new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ItemGroup.MISC)
            )
        );
        RECLAIMER = Registry.register(
            Registry.ITEM,
            new ResourceLocation(BPSettings.MOD_ID, "reclaimer"),
            new BlockItem(BlockModule.RECLAIMER_BLOCK, new Item.Properties().group(ItemGroup.MISC))
        );

    }
}
