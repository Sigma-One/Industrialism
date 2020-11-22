package sigmaone.industrialism.material.metal

import net.minecraft.entity.EquipmentSlot
import net.minecraft.item.ArmorMaterial
import net.minecraft.recipe.Ingredient
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Lazy
import java.util.function.Supplier

class MetalArmourMaterial(
        private val name: String,
        private val durability: IntArray,
        private val protection: IntArray,
        private val toughness: Float,
        private val knockbackResistance: Float,
        private val enchantability: Int,
        repairIngredient: Supplier<Ingredient>?
): ArmorMaterial {
    private val repairIngredient: Lazy<Ingredient> = Lazy(repairIngredient)

    override fun getDurability(slot: EquipmentSlot): Int {
        return durability[slot.entitySlotId]
    }

    override fun getProtectionAmount(slot: EquipmentSlot): Int {
        return protection[slot.entitySlotId]
    }

    override fun getEnchantability(): Int {
        return enchantability
    }

    override fun getEquipSound(): SoundEvent {
        return SoundEvents.ITEM_ARMOR_EQUIP_GENERIC
    }

    override fun getRepairIngredient(): Ingredient {
        return repairIngredient.get()
    }

    override fun getName(): String {
        return name
    }

    override fun getToughness(): Float {
        return toughness
    }

    override fun getKnockbackResistance(): Float {
        return knockbackResistance
    }
}