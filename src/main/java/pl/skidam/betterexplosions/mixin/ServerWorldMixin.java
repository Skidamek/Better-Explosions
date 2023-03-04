package pl.skidam.betterexplosions.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.skidam.betterexplosions.ReverseExplosion;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import static pl.skidam.betterexplosions.BetterExplosions.*;

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
                if (explosions.get(i).isReversing()) {

                    if (tick % 5 != 0) { // every 5 tick, reverse one block
                        continue;
                    }

                    List<BlockPos> affectedBlocks = explosions.get(i).getAffectedBlocks();
                    Map<BlockPos, BlockState> blocksToReverse = explosions.get(i).getBlocksToReverse();
                    RegistryKey<World> world = explosions.get(i).getWorld();

                    // remove explosion from map if it's empty
                    if (affectedBlocks.size() == 0 || blocksToReverse.size() == 0 || world == null) {
                        explosionsIterator.remove();
                        continue;
                    }

                    ServerWorld serverWorld = this.server.getWorld(world);
                    if (serverWorld == null) {
                        explosionsIterator.remove();
                        continue;
                    }

                    for (BlockPos blockPos : affectedBlocks) {
                        BlockState blockState = blocksToReverse.get(blockPos);
                        if (blockState != null) {

                            serverWorld.getProfiler().push("better_explosions_reverse_explosion");

                            double x = blockPos.getX();
                            double y = blockPos.getY();
                            double z = blockPos.getZ();

                            // don't set block if player is near, but drop item
                            if (serverWorld.getPlayers().stream().anyMatch(player -> player.squaredDistanceTo(x, y, z) < 4)) { // 4 == (box) 2^2
                                Block.dropStacks(blockState, serverWorld, blockPos, null, null, ItemStack.EMPTY);
                                blocksToReverse.remove(blockPos);
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
                                    player.networkHandler.sendPacket(new PlaySoundS2CPacket(registryEntry, SoundCategory.BLOCKS, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 1.0F, 1.0F, l));
                                }
                            });

                            serverWorld.setBlockState(blockPos, blockState);
                            serverWorld.getProfiler().pop();

                            // remove block from lists
                            blocksToReverse.remove(blockPos);
                            affectedBlocks.remove(blockPos);
                            break; // break loop to create animation and to prevent ConcurrentModificationException
                        }
                    }

                    continue;
                }

                // if tick is 200 (10 sec after boom), then start reversing explosion
                if (tick >= 200) {
                    explosions.get(i).setReversing(true);
                }
            }
        }
    }
}
