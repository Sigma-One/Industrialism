package sigmaone.industrialism.material.metal

import net.minecraft.item.ToolMaterial
import net.minecraft.recipe.Ingredient
import net.minecraft.util.Lazy
import java.util.function.Supplier

class MetalToolMaterial(private val miningLevel: Int, private val durability: Int, private val speedMultiplier: Float, private val attackDamage: Float, private val enchantability: Int, repairIngredient: Supplier<Ingredient>?) : ToolMaterial {
    private val repairIngredient: Lazy<Ingredient> = Lazy(repairIngredient)
    override fun getDurability(): Int {
        return durability
    }

    override fun getMiningSpeedMultiplier(): Float {
        return speedMultiplier
    }

    override fun getAttackDamage(): Float {
        return attackDamage
    }

    override fun getMiningLevel(): Int {
        return miningLevel
    }

    override fun getEnchantability(): Int {
        return enchantability
    }

    override fun getRepairIngredient(): Ingredient {
        return repairIngredient.get()
    }

}