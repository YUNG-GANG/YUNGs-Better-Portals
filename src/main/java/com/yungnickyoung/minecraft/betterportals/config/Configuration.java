package com.yungnickyoung.minecraft.betterportals.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class Configuration {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> enableVanillaPortals;
    public static final ForgeConfigSpec.ConfigValue<Integer> maxPortalPlacementAltitude;

    static {
        BUILDER.push("Better Portals");

        enableVanillaPortals = BUILDER
            .comment(
                " Whether or not vanilla Nether portals can be created.\n" +
                " Default: false")
            .worldRestart()
            .define("Enable Vanilla Nether Portals", false);

        maxPortalPlacementAltitude = BUILDER
            .comment(
                " The maximum height at which a Dimensional Plasma Bucket may be used to place Dimensional Plasma.\n" +
                "     This option exists to force users to go underground in order to get to the Nether (or other dimension, if configured).\n" +
                " Default: 15")
            .worldRestart()
            .defineInRange("Max Dimensional Plasma Placement Altitude", 15, 1, 255);


        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}