package pl.skidam.betterexplosions.mixin;

import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.skidam.betterexplosions.api.BetterExplosionsApi;
import pl.skidam.betterexplosions.config.Config;

@Mixin(WitherSkullEntity.class)
public class WitherSkullEntityMixin {

    @Inject(at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFZLnet/minecraft/world/explosion/Explosion$DestructionType;)Lnet/minecraft/world/explosion/Explosion;",
            shift = At.Shift.BEFORE
    ), method = "onCollision", cancellable = true)
    public void onCollision(HitResult hitResult, CallbackInfo ci) {
        if (Config.fields.rebuildWitherSkullExplosion) {
            var thiz = ((WitherSkullEntity) (Object) this);
            // we only need to change this line
            BetterExplosionsApi.createExplosion(thiz.getWorld(), thiz, thiz.getX(), thiz.getY(), thiz.getZ(), 1.0F, true, null);

            thiz.discard();
            ci.cancel();
        }
    }
}
