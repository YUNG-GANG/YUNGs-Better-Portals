package com.yungnickyoung.minecraft.betterportals.block;

import com.google.common.collect.Lists;
import com.yungnickyoung.minecraft.betterportals.init.BPBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class ReclaimerTileEntity extends TileEntity implements ITickableTileEntity {
    private List<BeamPiece> beamSegments = Lists.newArrayList();
    private List<BeamPiece> beamColorSegments = Lists.newArrayList();
    private int beaconSize = -1;

    public ReclaimerTileEntity() {
        super(BPBlocks.RECLAIMER_TILE_ENTITY);
    }

    @Override
    public void tick() {
        // Play ambient sound if powered
        if (this.world.getBlockState(this.pos).getBlock() == BPBlocks.RECLAIMER) {
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
    }

    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
    }

    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        return compound;
    }

    @OnlyIn(Dist.CLIENT)
    public List<BeamPiece> getBeamPieces() {
        return this.beamSegments;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isPowered() {
        if (this.world != null && this.world.getBlockState(this.pos).getBlock() == BPBlocks.RECLAIMER) {
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
