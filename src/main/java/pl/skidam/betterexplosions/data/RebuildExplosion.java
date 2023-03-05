package pl.skidam.betterexplosions.data;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.Map;

public class RebuildExplosion {
    private boolean reversing;
    private int ticks;
    private final Map<BlockPos, BlockState> blocksToRebuild;
    private final RegistryKey<World> world;

    public RebuildExplosion(boolean reversing, int ticks, Map<BlockPos, BlockState> blocksToRebuild, RegistryKey<World> world) {
        this.reversing = reversing;
        this.ticks = ticks;
        this.blocksToRebuild = blocksToRebuild;
        this.world = world;
    }

    public boolean isRebuilding() {
        return reversing;
    }

    public int getTicks() {
        return ticks;
    }

    public Map<BlockPos, BlockState> getBlocksToRebuild() {
        return blocksToRebuild;
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
}
