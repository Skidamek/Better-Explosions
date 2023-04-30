package pl.skidam.betterexplosions.data;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class BlockSave {
    private BlockPos pos;
    private BlockState state;
    private NbtCompound nbt;

    public BlockSave(BlockPos blockPos, BlockState blockState, NbtCompound nbtCompound) {
        pos = blockPos;
        state = blockState;
        nbt = nbtCompound;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public BlockState getState() {
        return state;
    }

    public void setState(BlockState state) {
        this.state = state;
    }

    public NbtCompound getNbt() {
        return nbt;
    }

    public void setNbt(NbtCompound nbt) {
        this.nbt = nbt;
    }
}
