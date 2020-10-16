package com.yungnickyoung.minecraft.betterportals.world;

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
public class MonolithPlacement extends Placement<NoPlacementConfig> {
    public MonolithPlacement(Codec<NoPlacementConfig> codec) {
        super(codec);
    }

    @Override
    public Stream<BlockPos> func_241857_a(WorldDecoratingHelper worldDecoratingHelper, Random random, NoPlacementConfig config, BlockPos pos) {
        int x = random.nextInt(16) + pos.getX();
        int z = random.nextInt(16) + pos.getZ();
        int y = random.nextInt(16);
        return Stream.of(new BlockPos(x, y, z));
    }
}
