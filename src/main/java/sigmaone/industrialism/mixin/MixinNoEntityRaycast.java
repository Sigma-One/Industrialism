package sigmaone.industrialism.mixin;

import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RaycastContext.class)
abstract class MixinNoEntityRaycast {
    @Redirect(
            method="<init>",
            at = @At(
                    value="INVOKE", target = "Lnet/minecraft/block/ShapeContext;of(Lnet/minecraft/entity/Entity;)Lnet/minecraft/block/ShapeContext;"
            )
    )
    ShapeContext ofNoEntity(Entity entity) {
        if (entity == null) {
            return ShapeContext.absent();
        }
        else {
            return ShapeContext.of(entity);
        }
    }
}
