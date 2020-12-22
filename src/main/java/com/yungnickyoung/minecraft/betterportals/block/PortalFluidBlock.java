package com.yungnickyoung.minecraft.betterportals.block;

import com.yungnickyoung.minecraft.betterportals.BetterPortals;
import com.yungnickyoung.minecraft.betterportals.capability.CapabilityModule;
import com.yungnickyoung.minecraft.betterportals.capability.IPlayerPortalInfo;
import com.yungnickyoung.minecraft.betterportals.fluid.FluidModule;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PortalFluidBlock extends FlowingFluidBlock {
    public PortalFluidBlock() {
        super(
            () -> FluidModule.PORTAL_FLUID,
            AbstractBlock.Properties.create(Material.LAVA)
                .doesNotBlockMovement()
                .hardnessAndResistance(100.0F)
                .setLightLevel((state) -> 7)
                .noDrops()
        );
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entity) {
        if (entity instanceof PlayerEntity && !entity.isPassenger() && !entity.isBeingRidden()) {
            IPlayerPortalInfo playerPortalInfo = entity.getCapability(CapabilityModule.PLAYER_PORTAL_INFO).resolve().orElse(null);
            if (playerPortalInfo == null) {
                return;
            }
            playerPortalInfo.setDEBUGportalCounter(playerPortalInfo.getDEBUGportalCounter() + 1);
            playerPortalInfo.enterPortalFluid();
            BetterPortals.LOGGER.info("");
        }
    }
}
