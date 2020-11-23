package sigmaone.industrialism.mixin;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

// Lowkey cursed hacky mixin
// #BlameMojang

@Mixin(ArmorItem.class)
abstract class MixinArmourKnockbackFix {
    @ModifyVariable(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/ImmutableMultimap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMultimap$Builder;",
                    ordinal = 1
            )
    )
    private ArmorMaterial returnNetherite(ArmorMaterial material) {
        return ArmorMaterials.NETHERITE;
    }
}
