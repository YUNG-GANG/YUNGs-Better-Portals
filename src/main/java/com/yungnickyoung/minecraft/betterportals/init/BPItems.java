package com.yungnickyoung.minecraft.betterportals.init;

import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class BPItems {
    public static Item PORTAL_BUCKET;

    // Initialization
    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, BPItems::registerItems);
    }

    /**
     * Registers Portal Fluid Bucket item.
     */
    private static void registerItems(RegistryEvent.Register<Item> event) {
        BPItems.PORTAL_BUCKET = Registry.register(
            Registry.ITEM,
            new ResourceLocation(BPSettings.MOD_ID, "portal_fluid_bucket"),
            new BucketItem(
                () -> BPBlocks.PORTAL_FLUID,
                new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ItemGroup.MISC)
            )
        );
    }
}
