package pl.skidam.betterexplosions.data;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.List;

public class RebuildExplosion {
    private boolean building;
    private int ticks;
    private List<BlockSave> blockSaves;
    private final RegistryKey<World> world;

    public RebuildExplosion(boolean building, int ticks, List<BlockSave> blockSaves, RegistryKey<World> world) {
        this.building = building;
        this.ticks = ticks;
        this.blockSaves = blockSaves;
        this.world = world;
    }

    public boolean isReBuilding() {
        return building;
    }

    public int getTicks() {
        return ticks;
    }

    public List<BlockSave> getBlockSaves() {
        return blockSaves;
    }
    public void setBlockSaves(List<BlockSave> blockSaves) {
        this.blockSaves = blockSaves;
    }

    public RegistryKey<World> getWorld() {
        return world;
    }

    public void setReBuilding(boolean building) {
        this.building = building;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }
}
