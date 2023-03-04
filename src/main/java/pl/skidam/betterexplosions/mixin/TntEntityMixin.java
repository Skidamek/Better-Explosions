package pl.skidam.betterexplosions.mixin;

import net.minecraft.entity.TntEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.skidam.betterexplosions.api.BetterExplosionsApi;
import pl.skidam.betterexplosions.config.Config;

@Mixin(TntEntity.class)
public class TntEntityMixin {

    @Inject(at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;createExplosion(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/world/World$ExplosionSourceType;)Lnet/minecraft/world/explosion/Explosion;",
            shift = At.Shift.BEFORE,
            remap = false
    ), method = "explode", cancellable = true)
    private void explode(CallbackInfo ci) {
        if (Config.fields.rebuildTntExplosion) {
            var thiz = ((TntEntity) (Object) this);
            // We only need to change this line and cancel the rest
            BetterExplosionsApi.createExplosion(thiz.getWorld(), thiz, thiz.getX(), thiz.getBodyY(0.0625), thiz.getZ(), 4.0F, World.ExplosionSourceType.TNT);

            ci.cancel();
        }
    }
}
