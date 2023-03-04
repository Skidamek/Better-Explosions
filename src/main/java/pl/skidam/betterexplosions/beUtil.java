package pl.skidam.betterexplosions;

import net.minecraft.util.math.BlockPos;

import java.util.Comparator;
import java.util.List;

public class beUtil {

    // sort list of affected block to be from most low layer to most high layer
    public static void sortAffectedBlocks(List<BlockPos> affectedBlocks) {
        affectedBlocks.sort(Comparator.comparingInt(BlockPos::getY));
    }
}
