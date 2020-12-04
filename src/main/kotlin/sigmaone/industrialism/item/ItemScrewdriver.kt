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
import sigmaone.industrialism.block.IBlockEntityConfigurable
import sigmaone.industrialism.component.energy.IComponentEnergyContainer
import java.util.*

class ItemScrewdriver(material: ToolMaterial?, attackDamage: Int, attackSpeed: Float, settings: Settings?) : MiningToolItem(attackDamage.toFloat(), attackSpeed, material, HashSet(), settings) {
    var opposite = false
    fun switchMode(world: World, user: PlayerEntity?, hand: Hand?) {
        if (!world.isClient) {
            if (opposite) {
                opposite = false
                user!!.sendMessage(TranslatableText("popup." + Industrialism.MOD_ID + ".item.screwdriver.normal"), true)
                user.getStackInHand(hand).orCreateTag.putInt("CustomModelData", 0)
            }
            else {
                opposite = true
                user!!.sendMessage(TranslatableText("popup." + Industrialism.MOD_ID + ".item.screwdriver.opposite"), true)
                user.getStackInHand(hand).orCreateTag.putInt("CustomModelData", 1)
            }
        }
    }

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val entity = (context.world.getBlockEntity(context.blockPos))
        return if (entity is IBlockEntityConfigurable) {
            if (entity is IComponentEnergyContainer) {
                val side = if (opposite) {
                    context.side.opposite
                }
                else {
                    context.side
                }
                entity.configure(side, context)
                if (!entity.configSpecial) {
                    val sideString = TranslatableText("variable.industrialism.side.${side.toString().toLowerCase()}")
                    val modeString = TranslatableText(
                        "variable.industrialism.io.${
                            entity.componentEnergyContainer.sideConfig[side]!!.toString().toLowerCase()
                        }"
                    )
                    context.player!!.sendMessage(
                        TranslatableText(
                            "popup.industrialism.io.set.sided",
                            sideString,
                            modeString
                        ), true
                    )
                }
            }
            ActionResult.SUCCESS
        }
        else {
            switchMode(context.world, context.player, context.hand)
            ActionResult.SUCCESS
        }
    }
}
