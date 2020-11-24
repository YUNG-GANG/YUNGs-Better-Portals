package com.yungnickyoung.minecraft.betterportals.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class Configuration {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> enableVanillaPortals;

    static {
        BUILDER.push("Better Portals");

        enableVanillaPortals = BUILDER
            .comment(
                " Whether or not vanilla Nether portals can be created.\n" +
                " Default: false")
            .worldRestart()
            .define("Enable Vanilla Nether Portals", false);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}