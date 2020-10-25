package sigmaone.industrialism.energy

import net.minecraft.util.math.BlockPos

data class WireConnection(
        val coefficient: Float,
        val xShift: Float,
        val yShift: Float,
        val wireThickness: Float,
        val wireColour: IntArray
)