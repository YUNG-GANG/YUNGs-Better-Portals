package com.yungnickyoung.minecraft.betterportals.block;

import com.yungnickyoung.minecraft.betterportals.BetterPortals;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PortalFluidBlock extends FlowingFluidBlock {
    public PortalFluidBlock() {
        super(
            () -> BetterPortals.PORTAL_FLUID,
            AbstractBlock.Properties.create(Material.WATER)
                .doesNotBlockMovement()
                .hardnessAndResistance(100.0F)
                .setLightLevel((state) -> 7)
                .noDrops()
        );
    }

    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
//        if (!entityIn.isPassenger() && !entityIn.isBeingRidden() && entityIn.isNonBoss()) {
//            if (entityIn.func_242280_ah()) {
//                entityIn.func_242279_ag();
//            } else {
//                if (!entityIn.world.isRemote && !pos.equals(entityIn.field_242271_ac)) {
//                    entityIn.field_242271_ac = pos.toImmutable();
//                }
//
//                entityIn.inPortal = true;
//            }
//        }
        if (!entityIn.isPassenger() && !entityIn.isBeingRidden() && entityIn.isNonBoss()) {
            entityIn.setPortal(pos);
        }
    }
}
