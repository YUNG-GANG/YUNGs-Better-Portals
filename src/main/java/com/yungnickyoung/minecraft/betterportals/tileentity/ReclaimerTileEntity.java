package com.yungnickyoung.minecraft.betterportals.tileentity;

import com.google.common.collect.Lists;
import com.yungnickyoung.minecraft.betterportals.block.BlockModule;
import com.yungnickyoung.minecraft.betterportals.block.ReclaimerBlock;
import com.yungnickyoung.minecraft.betterportals.capability.CapabilityModule;
import com.yungnickyoung.minecraft.betterportals.capability.IPlayerPortalInfo;
import com.yungnickyoung.minecraft.betterportals.world.variant.MonolithVariantSettings;
import com.yungnickyoung.minecraft.betterportals.world.variant.MonolithVariants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class ReclaimerTileEntity extends TileEntity implements ITickableTileEntity {
    private List<BeamPiece> beamSegments = Lists.newArrayList();
    private List<BeamPiece> tempBeamSegments = Lists.newArrayList(); // temporary list used for building the beamSegments
    private int tempBeamMaxY = -1; // temporary value to store the top beam y position as we build the list of beamSegments
    private int beamMaxY; // stored value for topmost y-coordinate of beam
    public int ticksExisted;
    private float activeRotation;

    public ReclaimerTileEntity() {
        super(TileEntityModule.RECLAIMER_TILE_ENTITY);
    }

    @Override
    public void tick() {
        this.ticksExisted++;
        if (this.world != null && this.world.isRemote) {
            ++this.activeRotation;
        }

        // Play ambient sound if powered
        if (this.world.getBlockState(this.pos).getBlock() == BlockModule.RECLAIMER_BLOCK) {
            if (this.world.getBlockState(this.pos).get(ReclaimerBlock.POWERED)) {
                if (world.getGameTime() % 80L == 0L) {
                    world.playSound(null, pos, SoundEvents.BLOCK_BEACON_AMBIENT, SoundCategory.BLOCKS, 1f, 1f);
                }
            }
        }

        int posX = this.pos.getX();
        int posY = this.pos.getY();
        int posZ = this.pos.getZ();
        BlockPos currPos;

        // Begin generating beams
        if (this.tempBeamMaxY < posY) {
            currPos = this.pos;
            this.tempBeamSegments = Lists.newArrayList();
            this.tempBeamMaxY = currPos.getY() - 1;
        } else {
            currPos = new BlockPos(posX, this.tempBeamMaxY + 1, posZ);
        }

        boolean hasHitBeamBlocker = false;

        BeamPiece beamSegment = this.tempBeamSegments.isEmpty() ? null : this.tempBeamSegments.get(this.tempBeamSegments.size() - 1);
        int surfaceHeight = this.world.getHeight(Heightmap.Type.WORLD_SURFACE, posX, posZ);

        for (int i = 0; i < 10 && currPos.getY() <= surfaceHeight; i++) {
            BlockState blockstate = this.world.getBlockState(currPos);

            // Determine color of this piece
            float[] colors;
            if (blockstate.getBlock() == BlockModule.RECLAIMER_BLOCK) {
                // Reclaimers use a special method that allows for dimension-specific beam coloring
                colors = ((ReclaimerBlock)blockstate.getBlock()).getColor(world, this.isPowered());
            } else {
                colors = blockstate.getBeaconColorMultiplier(this.world, currPos, this.pos);
            }

            // Check for beam blockers
            String dimensionName = world.getDimensionKey().getLocation().toString();
            MonolithVariantSettings settings = MonolithVariants.get().getVariantForDimension(dimensionName);
            List<BlockState> beamBlockers = settings == null
                ? Lists.newArrayList(Blocks.OBSIDIAN.getDefaultState()) // Use obsidian by default
                : settings.getBeamStopBlocks();
            if (beamBlockers.contains(blockstate)) {
                hasHitBeamBlocker = true;
                break;
            }

            // If this block is colored, we need to use its colors
            if (colors != null) {
                if (this.tempBeamSegments.size() <= 1) { // No beam yet -> create new piece with these colors
                    beamSegment = new BeamPiece(colors);
                    this.tempBeamSegments.add(beamSegment);
                } else if (beamSegment != null) { // Beam already exists -> new beam's color is average of curr color with new color
                    if (Arrays.equals(colors, beamSegment.colors)) { // Curr beam color equals new color -> no need to compute average
                        beamSegment.incrementHeight();
                    } else {
                        beamSegment = new BeamPiece(new float[] {
                            (beamSegment.colors[0] + colors[0]) / 2.0F, // Red
                            (beamSegment.colors[1] + colors[1]) / 2.0F, // Green
                            (beamSegment.colors[2] + colors[2]) / 2.0F  // Blue
                        });
                        this.tempBeamSegments.add(beamSegment);
                    }
                }
            } else {
                beamSegment.incrementHeight();
            }

            currPos = currPos.up();
            ++this.tempBeamMaxY;
        }

        if (this.tempBeamMaxY >= surfaceHeight || hasHitBeamBlocker) {
            // If the beam didn't hit a beam blocker, extend its height into the sky
            if (!hasHitBeamBlocker) {
                this.tempBeamMaxY = 1024;
                this.tempBeamSegments.get(this.tempBeamSegments.size() - 1).setHeight(1024);
            }
            this.beamMaxY = this.tempBeamMaxY;
            this.tempBeamMaxY = -1;
            this.beamSegments = this.tempBeamSegments;
        }

        // TODO - limit remaining logic to server side only?

        boolean isPowered = this.isPowered();
        int floatAmp = isPowered ? 5 : 2; // Powered beams levitate entities more quickly
        List<PlayerEntity> playersInBeam = getPlayersInBeam();
        List<CreatureEntity> nonPlayersInBeam = getNonPlayersInBeam();
        List<ItemEntity> itemsInBeam = getItemsInBeam();

        // Float up items in beam
        for (ItemEntity entity : itemsInBeam) {
            double floatSpeed = isPowered() ? .2 : .08;
            entity.setMotion(entity.getMotion().getX() / 1.5, floatSpeed, entity.getMotion().getZ() / 1.5);
        }

        // Float up non player entities in beam
        for (CreatureEntity entity : nonPlayersInBeam) {
            entity.addPotionEffect(new EffectInstance(Effects.LEVITATION, 2, floatAmp, false, false));
        }

        // Effects and teleportation for players in beam
        for (PlayerEntity playerEntity : playersInBeam) {
            // Float player up if not crouching, or down if crouching
            if (!playerEntity.isPotionActive(Effects.LEVITATION) || (playerEntity.isPotionActive(Effects.LEVITATION) && playerEntity.getActivePotionEffect(Effects.LEVITATION).getDuration() <= 2)) { // Don't overwrite existing levitation effect from potion
                if (playerEntity.isCrouching()) {
                    playerEntity.addPotionEffect(new EffectInstance(Effects.LEVITATION, 2, -5, false, false));
                } else {
                    playerEntity.addPotionEffect(new EffectInstance(Effects.LEVITATION, 2, floatAmp, false, false));
                }
            }

            // Powered reclaimers act as teleporters
            if (isPowered) {
                IPlayerPortalInfo playerPortalInfo = playerEntity.getCapability(CapabilityModule.PLAYER_PORTAL_INFO).resolve().orElse(null);
                if (playerPortalInfo == null) {
                    continue;
                }
                playerPortalInfo.enterReclaimer();
            }
        }
    }

    // TODO - use Server-side entity instead?
    private List<PlayerEntity> getPlayersInBeam() {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, this.beamMaxY, pos.getZ() + 1);
        return this.world.getEntitiesWithinAABB(PlayerEntity.class, axisAlignedBB);
    }

    private List<CreatureEntity> getNonPlayersInBeam() {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, this.beamMaxY, pos.getZ() + 1);
        return this.world.getEntitiesWithinAABB(CreatureEntity.class, axisAlignedBB);
    }

    private List<ItemEntity> getItemsInBeam() {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, this.beamMaxY, pos.getZ() + 1);
        return this.world.getEntitiesWithinAABB(ItemEntity.class, axisAlignedBB);
    }

    public void read(BlockState state, @Nonnull CompoundNBT nbt) {
        super.read(state, nbt);
    }

    @Nonnull
    public CompoundNBT write(@Nonnull CompoundNBT compound) {
        super.write(compound);
        return compound;
    }

    public List<BeamPiece> getBeamPieces() {
        return this.beamSegments;
    }

    public boolean isPowered() {
        if (this.world != null && this.world.getBlockState(this.pos).getBlock() == BlockModule.RECLAIMER_BLOCK) {
            return this.world.getBlockState(this.pos).get(ReclaimerBlock.POWERED);
        }
        return false;
    }

    /**
     * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
     * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
     */
    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
    }

    @Nonnull
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    public double getMaxRenderDistanceSquared() {
        return 256.0D;
    }

    /**
     * Ensures the beam will always render, regardless of whether or not
     * we can see the source block.
     */
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    public float getActiveRotation(float partialTicks) {
        return (this.activeRotation + partialTicks) * (this.isPowered() ? -.2F : -0.0375F);
    }

    public static class BeamPiece {
        /** RGB (0 to 1.0) colors of this beam segment */
        private final float[] colors;
        private int height;

        public BeamPiece(float[] colorsIn) {
            this.colors = colorsIn;
            this.height = 1;
        }

        protected void incrementHeight() {
            ++this.height;
        }

        protected void setHeight(int height) {
            this.height = height;
        }

        /**
         * Returns RGB (0 to 1.0) colors of this beam segment
         */
        public float[] getColors() {
            return this.colors;
        }

        public int getHeight() {
            return this.height;
        }
    }
}
