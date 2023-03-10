package pl.skidam.betterexplosions.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;
import pl.skidam.betterexplosions.BetterMinecraftExplosion;

/**
 * This class contains methods which are almost the same as the original ones from {@link World}.
 * The only difference is that they accept {@link World} as the first parameter.
 * Its purpose is to make re-buildable explosions easily just by calling this method.
 */

public class BetterExplosionsApi {
    
    // Our custom createExplosion method.
    public static BetterMinecraftExplosion createExplosion(
            World world, // This is the only difference between this method and the original one.
            @Nullable Entity entity, 
            double x, 
            double y, 
            double z, 
            float power, 
            @Nullable World.ExplosionSourceType explosionSourceType
    ) {
        return createExplosion(world, entity, null, null, x, y, z, power, false, explosionSourceType);
    }

    public static BetterMinecraftExplosion createExplosion(
            World world, // This is the only difference between this method and the original one.
            @Nullable Entity entity, 
            double x, 
            double y, 
            double z, 
            float power, 
            boolean createFire,
            @Nullable World.ExplosionSourceType explosionSourceType
    ) {
        return createExplosion(world, entity, null, null, x, y, z, power, createFire, explosionSourceType);
    }

    public static BetterMinecraftExplosion createExplosion(
            World world, // This is the only difference between this method and the original one.
            @Nullable Entity entity,
            @Nullable DamageSource damageSource,
            @Nullable ExplosionBehavior behavior,
            Vec3d pos,
            float power,
            boolean createFire,
            @Nullable World.ExplosionSourceType explosionSourceType
    ) {
        return createExplosion(world, entity, damageSource, behavior, pos.getX(), pos.getY(), pos.getZ(), power, createFire, explosionSourceType);
    }

    public static BetterMinecraftExplosion createExplosion(
            World world, // This is the only difference between this method and the original one.
            @Nullable Entity entity,
            @Nullable DamageSource damageSource,
            @Nullable ExplosionBehavior behavior,
            double x,
            double y,
            double z,
            float power,
            boolean createFire,
            @Nullable World.ExplosionSourceType explosionSourceType
    ) {
        return createExplosion(world, entity, damageSource, behavior, x, y, z, power, createFire, explosionSourceType, true);
    }

    public static BetterMinecraftExplosion createExplosion(
            World world, // This is the only difference between this method and the original one.
            @Nullable Entity entity,
            @Nullable DamageSource damageSource,
            @Nullable ExplosionBehavior behavior,
            double x,
            double y,
            double z,
            float power,
            boolean createFire,
            @Nullable World.ExplosionSourceType explosionSourceType,
            boolean particles
    ) {
        BetterMinecraftExplosion.DestructionType destructionType = BetterMinecraftExplosion.DestructionType.DESTROY;
        BetterMinecraftExplosion explosion = new BetterMinecraftExplosion(world, entity, damageSource, behavior, x, y, z, power, createFire, destructionType);
        explosion.collectBlocksAndDamageEntities();
        explosion.affectWorld(particles);
        return explosion;
    }
}
