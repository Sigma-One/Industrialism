package sigmaone.industrialism.block.machine

import net.minecraft.util.math.Direction
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.Industrialism.InputConfig
import sigmaone.industrialism.block.BlockEntitySidedEnergyContainer
import sigmaone.industrialism.block.IConfigurable

class BlockEntityBattery : BlockEntitySidedEnergyContainer(Industrialism.BATTERY, 800f, sideConfig), IConfigurable {
    companion object {
        val sideConfig: Map<Direction, InputConfig> = hashMapOf(
                Direction.NORTH to InputConfig.NONE,
                Direction.SOUTH to InputConfig.NONE,
                Direction.EAST  to InputConfig.NONE,
                Direction.WEST  to InputConfig.NONE,
                Direction.UP    to InputConfig.NONE,
                Direction.DOWN  to InputConfig.NONE
        )
    }
}