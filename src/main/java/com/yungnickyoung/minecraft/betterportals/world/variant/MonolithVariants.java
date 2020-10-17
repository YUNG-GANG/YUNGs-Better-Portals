package com.yungnickyoung.minecraft.betterportals.world.variant;

import com.yungnickyoung.minecraft.yungsapi.world.BlockSetSelector;
import net.minecraft.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class MonolithVariants {
    /** Singleton stuff **/

    public static MonolithVariants instance; // This technically shouldn't be public, but it is necessary for loading data from JSON
    public static MonolithVariants get() {
        if (instance == null) {
            instance = new MonolithVariants();
        }
        return instance;
    }

    private MonolithVariants() {
        variants = new ArrayList<>();

        // Nether to overworld
        variants.add(new MonolithVariantSettings()
            .setStairSelector(
                new BlockSetSelector(Blocks.BLACKSTONE_STAIRS.getDefaultState())
                    .addBlock(Blocks.BLACKSTONE_STAIRS.getDefaultState(), .5f)
                    .addBlock(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS.getDefaultState(), .5f)
            )
            .setCornerSelector(
                new BlockSetSelector(Blocks.CHISELED_POLISHED_BLACKSTONE.getDefaultState())
            )
            .setInsideSelector(
                new BlockSetSelector(Blocks.NETHER_BRICKS.getDefaultState())
            )
            .setTopSelector(
                new BlockSetSelector(Blocks.BLACKSTONE_WALL.getDefaultState())
                    .addBlock(Blocks.POLISHED_BLACKSTONE_BRICK_WALL.getDefaultState(), .33f)
                    .addBlock(Blocks.BLACKSTONE_WALL.getDefaultState(), .33f)
                    .addBlock(Blocks.POLISHED_BLACKSTONE_WALL.getDefaultState(), .33f)
            )
            .setPowerBlock(Blocks.GOLD_BLOCK.getDefaultState())
            .setDecorationBlock(Blocks.YELLOW_GLAZED_TERRACOTTA.getDefaultState())
            .setMinY(35)
            .setMaxY(110)
            .setSpawnChance(.1)
            .setSpawnDimension("minecraft:the_nether")
            .setTargetDimension("minecraft:overworld")
        );
    }

    /** Instance variables and methods **/

    private List<MonolithVariantSettings> variants;

    public List<MonolithVariantSettings> getVariants() {
        return variants;
    }

    public MonolithVariantSettings getVariantForDimension(String dimension) {
        return variants.stream().filter(settings -> settings.getSpawnDimension().equals(dimension)).findFirst().orElse(null);
    }
}
