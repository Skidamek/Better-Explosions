package pl.skidam.betterexplosions.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.skidam.betterexplosions.config.Config;

import java.util.*;
import java.util.function.BooleanSupplier;

import static pl.skidam.betterexplosions.BetterExplosions.*;

/**
 * This mixin is responsible for rebuilding blocks after explosion
 */

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

    @Final
    @Shadow
    private MinecraftServer server;

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {

        if (explosions.size() > 0) {

            // loop of explosions map, we need to use iterator because we are removing elements from map
            Iterator<Integer> explosionsIterator = explosions.keySet().iterator();
            while (explosionsIterator.hasNext()) {
                int i = explosionsIterator.next();

                int tick = explosions.get(i).getTicks() + 1;
                explosions.get(i).setTicks(tick);

                // if explosion is reversing, then continue
                if (explosions.get(i).isRebuilding()) {

                    if (tick % Config.fields.rebuildOneBlockEvery != 0) { // by default every 5 tick, rebuilding one block
                        continue;
                    }

                    Map<BlockPos, BlockState> blocksToRebuild = explosions.get(i).getBlocksToRebuild();
                    Set<BlockPos> affectedBlocks = explosions.get(i).getBlocksToRebuild().keySet();
                    RegistryKey<World> world = explosions.get(i).getWorld();

                    // remove explosion from map if it's empty
                    if (affectedBlocks.size() == 0 || blocksToRebuild.size() == 0 || world == null) {
                        explosionsIterator.remove();
                        continue;
                    }

                    ServerWorld serverWorld = this.server.getWorld(world);
                    if (serverWorld == null) {
                        explosionsIterator.remove();
                        continue;
                    }

                    serverWorld.getProfiler().push("better_explosions_rebuild_explosion");

                    // get lowest block (y) from affected blocks
                    BlockPos blockPos = Collections.min(affectedBlocks, Comparator.comparingInt(BlockPos::getY));
                    BlockState blockState = blocksToRebuild.get(blockPos);

                    if (blockState != null) {

                        int x = blockPos.getX();
                        int y = blockPos.getY();
                        int z = blockPos.getZ();

                        // don't set block if player is near, but drop item
                        if (serverWorld.getPlayers().stream().anyMatch(player -> player.getBlockPos().getX() == x && player.getBlockPos().getY() <= y && player.getBlockPos().getZ() == z)) {
                            Block.dropStacks(blockState, serverWorld, blockPos, null, null, ItemStack.EMPTY);
                            blocksToRebuild.remove(blockPos);
                            affectedBlocks.remove(blockPos);
                            serverWorld.getProfiler().pop();
                            break;
                        }

                        // rebuilding sound
                        RegistryEntry<SoundEvent> registryEntry = RegistryEntry.of(SoundEvent.of(SoundEvents.BLOCK_BUBBLE_COLUMN_BUBBLE_POP.getId()));

                        // check if player is near
                        serverWorld.getPlayers().forEach(player -> {
                            if (player.squaredDistanceTo(x, y, z) < 256) { // 256 == (box) 16^2
                                double e = x - player.getX();
                                double f = y - player.getY();
                                double g = z - player.getZ();
                                double h = e * e + f * f + g * g;
                                double k = Math.sqrt(h);
                                Vec3d vec3d = new Vec3d(player.getX() + e / k * 2.0, player.getY() + f / k * 2.0, player.getZ() + g / k * 2.0);
                                long l = serverWorld.getRandom().nextLong();
                                player.networkHandler.sendPacket(new PlaySoundS2CPacket(registryEntry, SoundCategory.BLOCKS, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 2.0F, 1.0F, l));
                            }
                        });

                        // set block
                        serverWorld.setBlockState(blockPos, blockState);
                    } else {
                        LOGGER.error("BlockState is null! BlockPos: " + blockPos);
                    }

                    // remove block from lists
                    blocksToRebuild.remove(blockPos);
                    affectedBlocks.remove(blockPos);

                    serverWorld.getProfiler().pop();
                    continue;
                }

                // By default, if tick is 200 (10 sec after boom), then start rebuilding explosion
                if (tick >= Config.fields.startRebuildingAfter) {
                    explosions.get(i).setReversing(true);
                }
            }
        }
    }
}
