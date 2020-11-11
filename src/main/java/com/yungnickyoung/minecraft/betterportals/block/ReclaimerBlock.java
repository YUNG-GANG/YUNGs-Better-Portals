package com.yungnickyoung.minecraft.betterportals.block;

import com.yungnickyoung.minecraft.betterportals.init.BPBlocks;
import com.yungnickyoung.minecraft.betterportals.tileentity.ReclaimerTileEntity;
import com.yungnickyoung.minecraft.betterportals.world.variant.MonolithVariantSettings;
import com.yungnickyoung.minecraft.betterportals.world.variant.MonolithVariants;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.ToIntFunction;

@SuppressWarnings("deprecation")
@MethodsReturnNonnullByDefault
public class ReclaimerBlock extends ContainerBlock implements IBeaconBeamColorProvider {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public ReclaimerBlock() {
        super(AbstractBlock.Properties.create(Material.GLASS, MaterialColor.DIAMOND)
            .hardnessAndResistance(3.0F)
            .setLightLevel(getLightValue(15))
            .notSolid()
            .setOpaque((state, reader, pos) -> false)
        );
        this.setDefaultState(this.getDefaultState().with(POWERED, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(POWERED);
    }

    @Override
    public boolean ticksRandomly(BlockState p_149653_1_) {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState blockState, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (shouldBePowered(world, pos, getPowerBlock(world))) {
            world.setBlockState(pos, blockState.func_235896_a_(POWERED), 2);
            world.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 1f, 1f);
        }
    }

    @Override
    public void neighborChanged(BlockState blockState, World world, BlockPos pos, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
        if (!world.isRemote) {
            boolean wasPowered = false;
            try {
                wasPowered = world.getBlockState(pos).get(POWERED);
            } catch (Exception ignored) {
            }

            BlockState powerBlock = getPowerBlock(world);
            boolean powered = shouldBePowered(world, pos, powerBlock);

            if (wasPowered && !powered) {
                // If block has lost power, play deactivation sound
                world.playSound(null, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS, 1f, 1f);
            } else if (!wasPowered && powered) {
                // If block has gained power, play activation sound
                world.playSound(null, pos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.BLOCKS, 1f, 1f);
            }

            // Update block state
            world.setBlockState(pos, blockState.with(POWERED, powered), 2);
        }
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
        if (world.getBlockState(pos).getBlock() == BlockModule.RECLAIMER_BLOCK) {
            if (world.getBlockState(pos).get(ReclaimerBlock.POWERED)) {
                world.playSound(null, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new ReclaimerTileEntity();
    }

    @Override
    public DyeColor getColor() {
        return DyeColor.WHITE;
    }

    private static ToIntFunction<BlockState> getLightValue(int lightValue) {
        return (state) -> state.get(BlockStateProperties.POWERED) ? lightValue : 0;
    }

    private boolean shouldBePowered(World world, BlockPos pos, BlockState powerBlock) {
        boolean powered = true;
        // Determine if block should be powered
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (world.getBlockState(pos.offset(direction)) != powerBlock) {
                powered = false;
                break;
            }
        }
        return powered;
    }

    @Override
    public BlockRenderType getRenderType(BlockState p_149645_1_) {
        return BlockRenderType.MODEL;
    }

    private BlockState getPowerBlock(World world) {
        String dimensionName = world.getDimensionKey().func_240901_a_().toString();
        MonolithVariantSettings settings = MonolithVariants.get().getVariantForDimension(dimensionName);
        return settings.getPowerBlock();
    }
}
