package pl.skidam.betterexplosions.mixin;

import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.skidam.betterexplosions.api.BetterExplosionsApi;
import pl.skidam.betterexplosions.config.Config;

@Mixin(FireballEntity.class)
public class FireballEntityMixin {

    @Shadow
    private int explosionPower;

    @Inject(at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFZLnet/minecraft/world/explosion/Explosion$DestructionType;)Lnet/minecraft/world/explosion/Explosion;",
            shift = At.Shift.BEFORE
    ), method = "onCollision", cancellable = true)
    public void onCollision(HitResult hitResult, CallbackInfo ci) {
        if (Config.fields.rebuildFireballExplosion) {
            var thiz = ((FireballEntity) (Object) this);
            // we only need to change this line
            BetterExplosionsApi.createExplosion(thiz.getWorld(), thiz, thiz.getX(), thiz.getY(), thiz.getZ(), explosionPower, true, null);

            thiz.discard();
            ci.cancel();
        }
    }
}
