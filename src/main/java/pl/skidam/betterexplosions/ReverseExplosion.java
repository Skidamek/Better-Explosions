package pl.skidam.betterexplosions;

import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;

public class ReverseExplosion {
    private boolean reversing;
    private int ticks;
    private List<BlockPos> affectedBlocks;
    private Map<BlockPos, BlockState> blocksToReverse;
    private RegistryKey<World> world;

    public ReverseExplosion(boolean reversing, int ticks, List<BlockPos> affectedBlocks, Map<BlockPos, BlockState> blocksToReverse, RegistryKey<World> world) {
        this.reversing = reversing;
        this.ticks = ticks;
        this.affectedBlocks = affectedBlocks;
        this.blocksToReverse = blocksToReverse;
        this.world = world;
    }

    public boolean isReversing() {
        return reversing;
    }

    public int getTicks() {
        return ticks;
    }

    public List<BlockPos> getAffectedBlocks() {
        return affectedBlocks;
    }

    public Map<BlockPos, BlockState> getBlocksToReverse() {
        return blocksToReverse;
    }

    public RegistryKey<World> getWorld() {
        return world;
    }

    public void setReversing(boolean reversing) {
        this.reversing = reversing;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    public void setAffectedBlocks(List<BlockPos> affectedBlocks) {
        this.affectedBlocks = affectedBlocks;
    }

    public void setBlocksToReverse(Map<BlockPos, BlockState> blocksToReverse) {
        this.blocksToReverse = blocksToReverse;
    }

    public void setWorld(RegistryKey<World> world) {
        this.world = world;
    }

    public void addAffectedBlock(BlockPos blockPos) {
        this.affectedBlocks.add(blockPos);
    }

    public void addBlockToReverse(BlockPos blockPos, BlockState blockState) {
        this.blocksToReverse.put(blockPos, blockState);
    }

    public void removeAffectedBlock(BlockPos blockPos) {
        this.affectedBlocks.remove(blockPos);
    }

    public void removeBlockToReverse(BlockPos blockPos) {
        this.blocksToReverse.remove(blockPos);
    }
}
