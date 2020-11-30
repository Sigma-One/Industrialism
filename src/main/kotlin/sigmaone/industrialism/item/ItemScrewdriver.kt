package sigmaone.industrialism.item

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.MiningToolItem
import net.minecraft.item.ToolMaterial
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.world.World
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.block.IConfigurable
import java.util.*

class ItemScrewdriver(material: ToolMaterial?, attackDamage: Int, attackSpeed: Float, settings: Settings?) : MiningToolItem(attackDamage.toFloat(), attackSpeed, material, HashSet(), settings) {
    @JvmField
    var opposite = false
    fun switchMode(world: World, user: PlayerEntity?, hand: Hand?) {
        if (!world.isClient) {
            if (opposite) {
                opposite = false
                user!!.sendMessage(TranslatableText("popup." + Industrialism.MOD_ID + ".item.screwdriver.normal"), true)
                user.getStackInHand(hand).orCreateTag.putInt("CustomModelData", 0)
            } else {
                opposite = true
                user!!.sendMessage(TranslatableText("popup." + Industrialism.MOD_ID + ".item.screwdriver.opposite"), true)
                user.getStackInHand(hand).orCreateTag.putInt("CustomModelData", 1)
            }
        }
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        return if (context.world.getBlockEntity(context.blockPos) is IConfigurable) {
            super.useOnBlock(context)
        } else {
            switchMode(context.world, context.player, context.hand)
            ActionResult.SUCCESS
        }
    }
}