package sigmaone.industrialism.item

import net.minecraft.item.ItemUsageContext
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolMaterial
import net.minecraft.util.ActionResult
import sigmaone.industrialism.util.MultiblockHelper

class ItemWrench(
    material: ToolMaterial?,
    attackDamage: Int,
    attackSpeed: Float,
    settings: Settings?
) : SwordItem(material, attackDamage, attackSpeed, settings) {
    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        val multiblock = MultiblockHelper.testForMultiblock(context.world, context.blockPos, context.side)
        if (multiblock != null) {
            MultiblockHelper.buildMultiblock(context.world, context.blockPos, context.side, multiblock)
        }
        return super.useOnBlock(context)
    }
}