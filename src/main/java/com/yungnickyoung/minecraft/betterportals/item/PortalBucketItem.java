package com.yungnickyoung.minecraft.betterportals.item;

import com.yungnickyoung.minecraft.betterportals.config.Configuration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

public class PortalBucketItem extends BucketItem {
    public PortalBucketItem(Supplier<? extends Fluid> fluidSupplier, Properties properties) {
        super(fluidSupplier, properties);
    }

    /**
     * Override fluid placement to disallow placing portal fluid above the configured max y-value.
     */
    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        BlockRayTraceResult rayTraceResult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.NONE);
        ItemStack itemStack = playerIn.getHeldItem(handIn);
        if (rayTraceResult.getType() != RayTraceResult.Type.MISS && rayTraceResult.getPos().getY() > Configuration.maxPortalPlacementAltitude.get()) {
            if (!worldIn.isRemote()) {
                playerIn.sendMessage(new TranslationTextComponent("betterportals.portal_fluid_bucket.altitude_error").setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.RED))), playerIn.getUniqueID());
            }
            return ActionResult.resultFail(itemStack);
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
