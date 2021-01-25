package com.yungnickyoung.minecraft.betterportals.item;

import com.yungnickyoung.minecraft.betterportals.block.BlockModule;
import com.yungnickyoung.minecraft.betterportals.config.BPSettings;
import com.yungnickyoung.minecraft.betterportals.fluid.FluidModule;
import com.yungnickyoung.minecraft.betterportals.module.IModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.ParametersAreNonnullByDefault;

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
    ) {
        @Override
        @ParametersAreNonnullByDefault
        public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
            BlockRayTraceResult raytraceresult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.NONE);
            ItemStack itemstack = playerIn.getHeldItem(handIn);
            if (raytraceresult.getType() != RayTraceResult.Type.MISS && raytraceresult.getPos().getY() > 16) {
                if (!worldIn.isRemote()) {
                    playerIn.sendMessage(new TranslationTextComponent("betterportals.portal_fluid_bucket.altitude_error").setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.RED))), playerIn.getUniqueID());
                }
                return ActionResult.resultFail(itemstack);
            }
            return super.onItemRightClick(worldIn, playerIn, handIn);
        }
    };
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
