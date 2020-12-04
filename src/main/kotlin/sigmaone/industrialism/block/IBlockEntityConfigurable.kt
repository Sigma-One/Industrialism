package sigmaone.industrialism.block

import net.minecraft.item.ItemUsageContext
import net.minecraft.util.math.Direction

interface IBlockEntityConfigurable {
    val configSpecial: Boolean
        get() = false
    fun configure(side: Direction, context: ItemUsageContext? = null)
}