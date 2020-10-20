package com.yungnickyoung.minecraft.betterportals.block;

import com.yungnickyoung.minecraft.betterportals.world.variant.MonolithVariantSettings;
import com.yungnickyoung.minecraft.betterportals.world.variant.MonolithVariants;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.DyeColor;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;
import java.util.function.ToIntFunction;

@SuppressWarnings("deprecation")
@MethodsReturnNonnullByDefault
public class ReclaimerBlock extends ContainerBlock implements IBeaconBeamColorProvider {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public ReclaimerBlock() {
        super(AbstractBlock.Properties.create(Material.GLASS, MaterialColor.DIAMOND).hardnessAndResistance(3.0F).setLightLevel(getLightValue(15)));
        this.setDefaultState(this.getDefaultState().with(POWERED, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(POWERED);
    }

    @Override
    public void tick(BlockState blockState, ServerWorld world, BlockPos pos, Random random) {
        if (blockState.get(POWERED) && !world.isBlockPowered(pos)) {
            world.setBlockState(pos, blockState.func_235896_a_(POWERED), 2);
        }
    }

    @Override
    public void neighborChanged(BlockState blockState, World world, BlockPos pos, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
        if (!world.isRemote) {
            boolean powered = true;
            String dimensionName = world.getDimensionKey().func_240901_a_().toString();
            MonolithVariantSettings settings = MonolithVariants.get().getVariantForDimension(dimensionName);
            BlockState powerBlock = settings.getPowerBlock();

            for (Direction direction : Direction.Plane.HORIZONTAL) {
                if (world.getBlockState(pos.offset(direction)) != powerBlock) {
                    powered = false;
                    break;
                }
            }
            world.setBlockState(pos, blockState.with(POWERED, powered), 2);
        }
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
        return DyeColor.CYAN;
    }

    private static ToIntFunction<BlockState> getLightValue(int lightValue) {
        return (state) -> state.get(BlockStateProperties.POWERED) ? lightValue : 0;
    }
}
