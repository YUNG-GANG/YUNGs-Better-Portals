package com.yungnickyoung.minecraft.betterportals.world.variant;

import com.yungnickyoung.minecraft.yungsapi.world.BlockSetSelector;
import net.minecraft.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class PortalLakeVariants {
    /** Singleton stuff **/

    public static PortalLakeVariants instance; // This technically shouldn't be public, but it is necessary for loading data from JSON
    public static PortalLakeVariants get() {
        if (instance == null) {
            instance = new PortalLakeVariants();
        }
        return instance;
    }

    private PortalLakeVariants() {
        variants = new ArrayList<>();

        // Overworld to Nether
        variants.add(new PortalLakeVariantSettings()
            .setBlockSelector(
                new BlockSetSelector()
                    .addBlock(Blocks.NETHERRACK.getDefaultState(), .7f)
                    .addBlock(Blocks.BLACKSTONE.getDefaultState(), .1f)
                    .addBlock(Blocks.GILDED_BLACKSTONE.getDefaultState(), .05f)
                    .addBlock(Blocks.OBSIDIAN.getDefaultState(), .1f)
                    .addBlock(Blocks.CRYING_OBSIDIAN.getDefaultState(), .05f)
            )
            .setFluidColor(0xFF000000)
            .setMinY(5)
            .setMaxY(15)
            .setPlayerTeleportedMinY(100)
            .setPlayerTeleportedMaxY(120)
            .setSpawnChance(.5)
            .setSpawnDimension("minecraft:overworld")
            .setTargetDimension("minecraft:the_nether")
            .setSpawnPlatformBlock(Blocks.NETHERRACK.getDefaultState())
        );
    }

    /** Instance variables and methods **/

    private List<PortalLakeVariantSettings> variants;

    public List<PortalLakeVariantSettings> getVariants() {
        return variants;
    }

    public PortalLakeVariantSettings getVariantForDimension(String dimension) {
        return variants.stream().filter(settings -> settings.getSpawnDimension().equals(dimension)).findFirst().orElse(null);
    }
}
