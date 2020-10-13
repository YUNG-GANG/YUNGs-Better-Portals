package com.yungnickyoung.minecraft.betterportals.world;

import com.yungnickyoung.minecraft.yungsapi.world.BlockSetSelector;

public class PortalLakeVariantSettings {
    public PortalLakeVariantSettings() {}

    private BlockSetSelector blockSelector;
    private int fluidColor;
    private int minY;
    private int maxY;
    private double spawnChance;
    private String spawnDimension;
    private String targetDimension;

    /** Getters **/

    public BlockSetSelector getBlockSelector() {
        return blockSelector;
    }

    public int getFluidColor() {
        return fluidColor;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public double getSpawnChance() {
        return spawnChance;
    }

    public String getSpawnDimension() {
        return spawnDimension;
    }

    public String getTargetDimension() {
        return targetDimension;
    }

    /** Builder-style setters to make it more obvious which settings are being set when creating a new object **/

    public PortalLakeVariantSettings setBlockSelector(BlockSetSelector selector) {
        this.blockSelector = selector;
        return this;
    }

    public PortalLakeVariantSettings setFluidColor(int color) {
        this.fluidColor = color;
        return this;
    }

    public PortalLakeVariantSettings setMinY(int minY) {
        this.minY = minY;
        return this;
    }

    public PortalLakeVariantSettings setMaxY(int maxY) {
        this.maxY = maxY;
        return this;
    }

    public PortalLakeVariantSettings setSpawnChance(double spawnChance) {
        this.spawnChance = spawnChance;
        return this;
    }

    public PortalLakeVariantSettings setSpawnDimension(String spawnDimension) {
        this.spawnDimension = spawnDimension;
        return this;
    }

    public PortalLakeVariantSettings setTargetDimension(String targetDimension) {
        this.targetDimension = targetDimension;
        return this;
    }
}
