package com.yungnickyoung.minecraft.betterportals.tileentity;

import com.google.common.collect.Lists;
import com.yungnickyoung.minecraft.betterportals.block.BlockModule;
import com.yungnickyoung.minecraft.betterportals.block.ReclaimerBlock;
import com.yungnickyoung.minecraft.betterportals.capability.CapabilityModule;
import com.yungnickyoung.minecraft.betterportals.capability.IPlayerPortalInfo;
import net.minecraft.block.BlockState;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class ReclaimerTileEntity extends TileEntity implements ITickableTileEntity {
    private List<BeamPiece> beamSegments = Lists.newArrayList();
    private List<BeamPiece> beamColorSegments = Lists.newArrayList();
    private int beaconSize = -1;
    List<PlayerEntity> prevPlayersInBeam = Lists.newArrayList();

    public ReclaimerTileEntity() {
        super(TileEntityModule.RECLAIMER_TILE_ENTITY);
    }

    @Override
    public void tick() {
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

        if (this.beaconSize < posY) {
            currPos = this.pos;
            this.beamColorSegments = Lists.newArrayList();
            this.beaconSize = currPos.getY() - 1;
        } else {
            currPos = new BlockPos(posX, this.beaconSize + 1, posZ);
        }

        BeamPiece beacontileentity$beamsegment = this.beamColorSegments.isEmpty() ? null : this.beamColorSegments.get(this.beamColorSegments.size() - 1);
        int surfaceHeight = this.world.getHeight(Heightmap.Type.WORLD_SURFACE, posX, posZ);

        for (int i = 0; i < 10 && currPos.getY() <= surfaceHeight; i++) {
            BlockState blockstate = this.world.getBlockState(currPos);
            float[] colors = blockstate.getBeaconColorMultiplier(this.world, currPos, this.pos);
            if (colors != null) {
                if (this.beamColorSegments.size() <= 1) {
                    beacontileentity$beamsegment = new BeamPiece(colors);
                    this.beamColorSegments.add(beacontileentity$beamsegment);
                } else if (beacontileentity$beamsegment != null) {
                    if (Arrays.equals(colors, beacontileentity$beamsegment.colors)) {
                        beacontileentity$beamsegment.incrementHeight();
                    } else {
                        beacontileentity$beamsegment = new BeamPiece(new float[]{(beacontileentity$beamsegment.colors[0] + colors[0]) / 2.0F, (beacontileentity$beamsegment.colors[1] + colors[1]) / 2.0F, (beacontileentity$beamsegment.colors[2] + colors[2]) / 2.0F});
                        this.beamColorSegments.add(beacontileentity$beamsegment);
                    }
                }
            } else {
                beacontileentity$beamsegment.incrementHeight();
            }

            currPos = currPos.up();
            ++this.beaconSize;
        }

        if (this.beaconSize >= surfaceHeight) {
            this.beaconSize = -1;
            this.beamSegments = this.beamColorSegments;
        }

        boolean isPowered = this.isPowered();
        List<PlayerEntity> playersInBeam = getPlayersInBeam();

        // Effects and teleportation for players in beam
        int floatAmp = isPowered ? 5 : 2; // Powered beams levitate the player more quickly
        for (PlayerEntity playerEntity : playersInBeam) {
            // Float player up
            playerEntity.addPotionEffect(new EffectInstance(Effects.LEVITATION, 2, floatAmp, false, false));

            // Powered reclaimers act as teleporters
            if (isPowered) {
                // Get portal info capability for player
                IPlayerPortalInfo playerPortalInfo = playerEntity.getCapability(CapabilityModule.PLAYER_PORTAL_INFO).resolve().orElse(null);
                if (playerPortalInfo == null) continue;

                // New player in beam -> set initial teleportation state
                if (!prevPlayersInBeam.contains(playerEntity)) {
                    setPortal(playerPortalInfo);
                }
            }
        }

        // If player was in beam and no longer is, set in portal to false
        prevPlayersInBeam.stream().filter(player -> !playersInBeam.contains(player)).forEach(player -> {
            // Get portal info capability for player
            IPlayerPortalInfo playerPortalInfo = player.getCapability(CapabilityModule.PLAYER_PORTAL_INFO).resolve().orElse(null);
            if (playerPortalInfo != null) {
                playerPortalInfo.setInPortal(false);
            }
        });

        // Update list of players previously in portal
        prevPlayersInBeam = playersInBeam;
    }

    // TODO - use Server-side entity instead?
    private List<PlayerEntity> getPlayersInBeam() {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, this.world.getHeight(), pos.getZ() + 1);
        return this.world.getEntitiesWithinAABB(PlayerEntity.class, axisAlignedBB);
    }

    private void setPortal(IPlayerPortalInfo playerPortalInfo) {
        if (playerPortalInfo.getPortalCooldown() > 0) {
            playerPortalInfo.setPortalCooldown(300);
        } else {
            playerPortalInfo.setInPortal(true);
        }
    }

    public void read(BlockState state, @Nonnull CompoundNBT nbt) {
        super.read(state, nbt);
    }

    @Nonnull
    public CompoundNBT write(@Nonnull CompoundNBT compound) {
        super.write(compound);
        return compound;
    }

    @OnlyIn(Dist.CLIENT)
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

    @OnlyIn(Dist.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 256.0D;
    }

    /**
     * Ensures the beam will always render, regardless of whether or not
     * we can see the source block.
     */
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
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

        /**
         * Returns RGB (0 to 1.0) colors of this beam segment
         */
        @OnlyIn(Dist.CLIENT)
        public float[] getColors() {
            return this.colors;
        }

        @OnlyIn(Dist.CLIENT)
        public int getHeight() {
            return this.height;
        }
    }
}
