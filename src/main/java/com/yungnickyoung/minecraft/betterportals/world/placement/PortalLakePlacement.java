package com.yungnickyoung.minecraft.betterportals.world.placement;

import com.mojang.serialization.Codec;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PortalLakePlacement extends Placement<NoPlacementConfig> {
    public PortalLakePlacement(Codec<NoPlacementConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldDecoratingHelper worldDecoratingHelper, Random random, NoPlacementConfig config, BlockPos pos) {
        int x = random.nextInt(16) + pos.getX();
        int z = random.nextInt(16) + pos.getZ();
        int y = random.nextInt(16);
        return Stream.of(new BlockPos(x, y, z));
    }
}
