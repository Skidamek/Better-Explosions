package pl.skidam.betterexplosions.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

import static pl.skidam.betterexplosions.BetterExplosions.*;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

    @Final
    @Shadow
    private MinecraftServer server;

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {

        if (explosions.size() > 0) {

            // for loop of explosions map
            for (int i : explosions.keySet()) {

                int tick = explosions.get(i).getTicks() + 1;
                explosions.get(i).setTicks(tick);

                // if explosion is reversing, then continue
                if (explosions.get(i).isReversing()) {

                    List<BlockPos> affectedBlocks = explosions.get(i).getAffectedBlocks();
                    Map<BlockPos, BlockState> blocksToReverse = explosions.get(i).getBlocksToReverse();
                    RegistryKey<World> world = explosions.get(i).getWorld();

                    // remove explosion from map if it's empty
                    if (affectedBlocks.size() == 0 || blocksToReverse.size() == 0 || world == null) {
                        explosions.remove(i);
                        continue;
                    }

                    ServerWorld serverWorld = this.server.getWorld(world);

                    for (BlockPos blockPos : affectedBlocks) {
                        BlockState blockState = blocksToReverse.get(blockPos);
                        if (blockState != null) {

//                            System.out.println("reversing block: " + blockPos.toString() + " to " + blockState);

                            serverWorld.getProfiler().push("better_explosions_reverse_explosion");

                            // TODO check if some entity is in block
                            Box box = new Box(blockPos);
                            if (!serverWorld.isSpaceEmpty(null, box)) {
                                System.out.println("some entity is in block: " + blockPos + " so it will be broken");
                                serverWorld.breakBlock(blockPos, true);
                                continue;
                            }

                            serverWorld.setBlockState(blockPos, blockState);
                            serverWorld.getProfiler().pop();

                            // remove block from map
                            blocksToReverse.remove(blockPos);
                            affectedBlocks.remove(blockPos);
                            break;
                        }
                    }

                    continue;
                }

                // if tick is 160, then add explosion to reversing map
                if (tick == 160) {
                    explosions.get(i).setReversing(true);
                }
            }
        }
    }
}
