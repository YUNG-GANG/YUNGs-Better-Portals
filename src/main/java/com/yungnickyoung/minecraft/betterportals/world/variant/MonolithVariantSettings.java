package com.yungnickyoung.minecraft.betterportals.world.variant;

import com.yungnickyoung.minecraft.yungsapi.world.BlockSetSelector;
import net.minecraft.block.BlockState;

public class MonolithVariantSettings {
    public MonolithVariantSettings() {}

    private BlockSetSelector stairSelector;
    private BlockSetSelector cornerSelector;
    private BlockSetSelector insideSelector;
    private BlockSetSelector topSelector;
    private BlockState powerBlock;
    private BlockState decorationBlock;
    private int minY;
    private int maxY;
    private double spawnChance;
    private String spawnDimension;
    private String targetDimension;

    /** Getters **/

    public BlockSetSelector getStairSelector() {
        return stairSelector;
    }

    public BlockSetSelector getCornerSelector() {
        return cornerSelector;
    }

    public BlockSetSelector getInsideSelector() {
        return insideSelector;
    }

    public BlockSetSelector getTopSelector() {
        return topSelector;
    }

    public BlockState getPowerBlock() {
        return powerBlock;
    }

    public BlockState getDecorationBlock() {
        return decorationBlock;
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

    public MonolithVariantSettings setStairSelector(BlockSetSelector stairSelector) {
        this.stairSelector = stairSelector;
        return this;
    }

    public MonolithVariantSettings setCornerSelector(BlockSetSelector cornerSelector) {
        this.cornerSelector = cornerSelector;
        return this;
    }

    public MonolithVariantSettings setInsideSelector(BlockSetSelector insideSelector) {
        this.insideSelector = insideSelector;
        return this;
    }

    public MonolithVariantSettings setTopSelector(BlockSetSelector topSelector) {
        this.topSelector = topSelector;
        return this;
    }

    public MonolithVariantSettings setPowerBlock(BlockState powerBlock) {
        this.powerBlock = powerBlock;
        return this;
    }

    public MonolithVariantSettings setDecorationBlock(BlockState decorationBlock) {
        this.decorationBlock = decorationBlock;
        return this;
    }


    public MonolithVariantSettings setMinY(int minY) {
        this.minY = minY;
        return this;
    }

    public MonolithVariantSettings setMaxY(int maxY) {
        this.maxY = maxY;
        return this;
    }

    public MonolithVariantSettings setSpawnChance(double spawnChance) {
        this.spawnChance = spawnChance;
        return this;
    }

    public MonolithVariantSettings setSpawnDimension(String spawnDimension) {
        this.spawnDimension = spawnDimension;
        return this;
    }

    public MonolithVariantSettings setTargetDimension(String targetDimension) {
        this.targetDimension = targetDimension;
        return this;
    }
}
