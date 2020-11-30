package sigmaone.industrialism.energy

import sigmaone.industrialism.item.ItemWireSpool

data class WireConnection(
        val coefficient: Float,
        val xShift: Float,
        val yShift: Float,
        val wireType: ItemWireSpool
)