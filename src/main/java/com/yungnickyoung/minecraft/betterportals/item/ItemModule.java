package com.yungnickyoung.minecraft.betterportals.item;

import com.yungnickyoung.minecraft.betterportals.BetterPortals;
import com.yungnickyoung.minecraft.betterportals.block.BlockModule;
import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.fluid.FluidModule;
import com.yungnickyoung.minecraft.betterportals.module.IModule;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ItemModule implements IModule {
    /**
     * Creative tab
     */
    public static final ItemGroup BETTERPORTALS_CREATIVE_TAB = new ItemGroup(ItemGroup.GROUPS.length, BPSettings.MOD_ID) {
        @Override
        @OnlyIn(Dist.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(BlockModule.RECLAIMER_BLOCK);
        }
    };

    /**
     * Items
     */
    public static Item PORTAL_BUCKET = new BucketItem (
        () -> FluidModule.PORTAL_FLUID,
        new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(BETTERPORTALS_CREATIVE_TAB)
    );
    public static Item RECLAIMER = new BlockItem(BlockModule.RECLAIMER_BLOCK, new Item.Properties().group(BETTERPORTALS_CREATIVE_TAB));

    public void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, ItemModule::registerItems);
    }

    /**
     * Registers Portal Fluid Bucket and Reclaimer items.
     */
    private static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(PORTAL_BUCKET.setRegistryName(new ResourceLocation(BPSettings.MOD_ID, "portal_fluid_bucket")));
        event.getRegistry().register(RECLAIMER.setRegistryName(new ResourceLocation(BPSettings.MOD_ID, "reclaimer")));
    }
}
