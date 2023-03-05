package pl.skidam.betterexplosions.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.skidam.betterexplosions.api.BetterExplosionsApi;
import pl.skidam.betterexplosions.config.Config;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin extends HostileEntity {

    @Shadow
    private int explosionRadius;

    @Shadow
    protected abstract void spawnEffectsCloud();

    protected CreeperEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/world/explosion/Explosion$DestructionType;)Lnet/minecraft/world/explosion/Explosion;",
            shift = At.Shift.BEFORE,
            remap = false
    ), method = "explode", cancellable = true)
    private void explode(CallbackInfo ci) {
        if (Config.fields.rebuildCreeperExplosion) {
            var thiz = ((CreeperEntity) (Object) this);
            float f = thiz.shouldRenderOverlay() ? 2.0F : 1.0F;
            // We only need to change this line
            BetterExplosionsApi.createExplosion(thiz.getWorld(), thiz, thiz.getX(), thiz.getY(), thiz.getZ(), (float)explosionRadius * f, null);

            thiz.discard();
            this.spawnEffectsCloud();

            ci.cancel();
        }
    }
}