package com.yungnickyoung.minecraft.betterportals.world.variant;

import com.yungnickyoung.minecraft.betterportals.util.RGBAColor;
import com.yungnickyoung.minecraft.yungsapi.world.BlockSetSelector;
import net.minecraft.block.BlockState;

import java.util.List;

public class MonolithVariantSettings {
    public MonolithVariantSettings() {}

    private BlockSetSelector stairSelector;
    private BlockSetSelector cornerSelector;
    private BlockSetSelector insideSelector;
    private BlockSetSelector fenceSelector;
    private BlockState powerBlock;
    private BlockState decorationBlock;
    private List<BlockState> beamStopBlocks;
    private RGBAColor unpoweredBeamColor;
    private RGBAColor poweredBeamColor;
    private int minY;
    private int maxY;
    private int playerTeleportedMinY;
    private int playerTeleportedMaxY;
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

    public BlockSetSelector getFenceSelector() {
        return fenceSelector;
    }

    public BlockState getPowerBlock() {
        return powerBlock;
    }

    public BlockState getDecorationBlock() {
        return decorationBlock;
    }

    public List<BlockState> getBeamStopBlocks() {
        return beamStopBlocks;
    }

    public RGBAColor getUnpoweredBeamColor() {
        return unpoweredBeamColor;
    }

    public RGBAColor getPoweredBeamColor() {
        return poweredBeamColor;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getPlayerTeleportedMinY() {
        return playerTeleportedMinY;
    }

    public int getPlayerTeleportedMaxY() {
        return playerTeleportedMaxY;
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

    public MonolithVariantSettings setFenceSelector(BlockSetSelector fenceSelector) {
        this.fenceSelector = fenceSelector;
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

    public MonolithVariantSettings setBeamStopBlocks(List<BlockState> beamStopBlocks) {
        this.beamStopBlocks = beamStopBlocks;
        return this;
    }

    public MonolithVariantSettings setUnpoweredBeamColor(RGBAColor unpoweredBeamColor) {
        this.unpoweredBeamColor = unpoweredBeamColor;
        return this;
    }

    public MonolithVariantSettings setPoweredBeamColor(RGBAColor poweredBeamColor) {
        this.poweredBeamColor = poweredBeamColor;
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

    public MonolithVariantSettings setPlayerTeleportedMinY(int playerTeleportedMinY) {
        this.playerTeleportedMinY = playerTeleportedMinY;
        return this;
    }

    public MonolithVariantSettings setPlayerTeleportedMaxY(int playerTeleportedMaxY) {
        this.playerTeleportedMaxY = playerTeleportedMaxY;
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
