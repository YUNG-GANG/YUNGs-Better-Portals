package com.yungnickyoung.minecraft.betterportals.world.placement;

import com.mojang.serialization.Codec;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PortalLakePlacement extends Placement<NoPlacementConfig> {
    public PortalLakePlacement() {
        super(NoPlacementConfig.CODEC);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldDecoratingHelper worldDecoratingHelper, Random random, NoPlacementConfig config, BlockPos pos) {
        int x = random.nextInt(16) + pos.getX();
        int z = random.nextInt(16) + pos.getZ();
        int y = 0; // y-value is configured during placement
        return Stream.of(new BlockPos(x, y, z));
    }
}
