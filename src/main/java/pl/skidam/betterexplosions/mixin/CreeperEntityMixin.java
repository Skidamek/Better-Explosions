package pl.skidam.betterexplosions.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import pl.skidam.betterexplosions.MinecraftExplosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin extends HostileEntity {

    @Shadow
    private int explosionRadius;

    @Shadow
    protected abstract void spawnEffectsCloud();

    @Shadow
    public abstract boolean shouldRenderOverlay();

    protected CreeperEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "explode", at = @At("HEAD"), cancellable = true)
    private void explode(CallbackInfo ci) {
        if (!this.world.isClient) {
            float f = this.shouldRenderOverlay() ? 2.0F : 1.0F;
            this.dead = true;

            // that's instead of `this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), (float)this.explosionRadius * f, ExplosionSourceType.MOB);`
            MinecraftExplosion minecraftExplosion = new MinecraftExplosion(this.getWorld(), null, null, null,  this.getBlockX(), this.getBlockY(), this.getBlockZ(), (float) this.explosionRadius * f, false, Explosion.DestructionType.DESTROY);
            minecraftExplosion.collectBlocksAndDamageEntities();
            minecraftExplosion.affectWorld(true);

            this.discard();
            this.spawnEffectsCloud();
        }
        ci.cancel();
    }
}