package sigmaone.industrialism.block.machine

import net.minecraft.util.math.Direction
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.Industrialism.InputConfig
import sigmaone.industrialism.block.BlockEntitySidedEnergyContainer
import java.util.*

class BlockEntityManualGenerator : BlockEntitySidedEnergyContainer(Industrialism.MANUAL_GENERATOR, 100f, sideConfig) {
    companion object {
        var sideConfig: Map<Direction, InputConfig> = hashMapOf(
            Direction.NORTH to InputConfig.OUTPUT,
            Direction.SOUTH to InputConfig.OUTPUT,
            Direction.EAST  to InputConfig.OUTPUT,
            Direction.WEST  to InputConfig.OUTPUT,
            Direction.UP    to InputConfig.NONE,
            Direction.DOWN  to InputConfig.NONE
        )
    }
}