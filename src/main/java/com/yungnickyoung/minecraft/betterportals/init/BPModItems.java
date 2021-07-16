package com.yungnickyoung.minecraft.betterportals.init;

import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.item.PortalBucketItem;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@MethodsReturnNonnullByDefault
public class BPModItems {
    /**
     * Creative tab
     */
    public static final ItemGroup BETTERPORTALS_CREATIVE_TAB = new ItemGroup(ItemGroup.GROUPS.length, BPSettings.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(BPModBlocks.RECLAIMER_BLOCK);
        }
    };

    /**
     * Items
     */
    public static Item PORTAL_BUCKET = new PortalBucketItem(() -> BPModFluids.PORTAL_FLUID, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(BETTERPORTALS_CREATIVE_TAB));
    public static Item RECLAIMER = new BlockItem(BPModBlocks.RECLAIMER_BLOCK, new Item.Properties().group(BETTERPORTALS_CREATIVE_TAB));

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, BPModItems::registerItems);
    }

    /**
     * Registers Portal Fluid Bucket and Reclaimer items.
     */
    private static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(PORTAL_BUCKET.setRegistryName(new ResourceLocation(BPSettings.MOD_ID, "portal_fluid_bucket")));
        event.getRegistry().register(RECLAIMER.setRegistryName(new ResourceLocation(BPSettings.MOD_ID, "reclaimer")));
    }
}
