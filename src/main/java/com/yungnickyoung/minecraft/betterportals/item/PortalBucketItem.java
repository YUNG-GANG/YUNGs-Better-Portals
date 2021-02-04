package com.yungnickyoung.minecraft.betterportals.item;

import com.yungnickyoung.minecraft.betterportals.BetterPortals;
import com.yungnickyoung.minecraft.betterportals.config.Configuration;
import com.yungnickyoung.minecraft.betterportals.world.variant.PortalLakeVariantSettings;
import com.yungnickyoung.minecraft.betterportals.world.variant.PortalLakeVariants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
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
import java.util.Objects;
import java.util.function.Supplier;

public class PortalBucketItem extends BucketItem {
    public PortalBucketItem(Supplier<? extends Fluid> fluidSupplier, Properties properties) {
        super(fluidSupplier, properties);
    }

    /**
     * Override fluid placement to disallow placing portal fluid above the configured max y-value, or in dimensions without a portal lake variant.
     */
    @Nonnull
    @Override
    @ParametersAreNonnullByDefault
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        BlockRayTraceResult rayTraceResult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.NONE);
        BlockPos targetPos = rayTraceResult.getPos().offset(rayTraceResult.getFace());
        ItemStack itemStack = playerIn.getHeldItem(handIn);

        // Attempt to get dimension name, e.g. "minecraft:the_nether"
        String dimensionName;
        try {
            dimensionName = Objects.requireNonNull(worldIn.getDimensionKey().getLocation()).toString();
        } catch (Exception e) {
            BetterPortals.LOGGER.error("ERROR: Unable to get dimension name when using Plasma Bucket!");
            return super.onItemRightClick(worldIn, playerIn, handIn);
        }

        // Prevent placement in dimensions without a portal lake variant
        PortalLakeVariantSettings settings = PortalLakeVariants.get().getVariantForDimension(dimensionName);
        if (settings == null) {
            // Play sizzle sound
            worldIn.playSound(playerIn, rayTraceResult.getPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

            // Render smoke particles
            for(int l = 0; l < 8; ++l) {
                worldIn.addParticle(ParticleTypes.LARGE_SMOKE, (double)targetPos.getX() + Math.random(), (double)targetPos.getY() + Math.random(), (double)targetPos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
            }

            // Send message to player
            if (!worldIn.isRemote()) {
                playerIn.sendStatusMessage(new TranslationTextComponent("betterportals.portal_fluid_bucket.cannot_place_fluid").setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.RED))), true);
            }

            return ActionResult.resultFail(itemStack);
        }

        // Prevent player from placing fluid above the max placement altitude
        if (rayTraceResult.getType() != RayTraceResult.Type.MISS && targetPos.getY() > Configuration.maxPortalPlacementAltitude.get()) {
            if (!worldIn.isRemote()) {
                playerIn.sendStatusMessage(new TranslationTextComponent("betterportals.portal_fluid_bucket.altitude_error").setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.RED))), true);
            }
            return ActionResult.resultFail(itemStack);
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
