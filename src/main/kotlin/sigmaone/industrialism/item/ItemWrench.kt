package sigmaone.industrialism.item

import net.minecraft.item.ItemUsageContext
import net.minecraft.item.MiningToolItem
import net.minecraft.item.ToolMaterial
import net.minecraft.util.ActionResult
import sigmaone.industrialism.util.MultiblockHelper
import kotlin.collections.HashSet

class ItemWrench(material: ToolMaterial?, attackDamage: Int, attackSpeed: Float, settings: Settings?) : MiningToolItem(attackDamage.toFloat(), attackSpeed, material, HashSet(), settings) {
    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val multiblock = MultiblockHelper.testForMultiblock(context.world, context.blockPos, context.side)
        if (multiblock != null) {
            MultiblockHelper.buildMultiblock(context.world, context.blockPos, context.side, multiblock)
        }
        return super.useOnBlock(context)
    }
}