package sigmaone.industrialism.block.machine

import net.minecraft.util.math.Direction
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.Industrialism.InputConfig
import sigmaone.industrialism.block.BlockEntityConnectableEnergyContainer
import sigmaone.industrialism.block.IConfigurable
import team.reborn.energy.EnergySide
import team.reborn.energy.EnergyTier
import java.util.*

class BlockEntityBattery : BlockEntityConnectableEnergyContainer(Industrialism.BATTERY, 16000.toDouble(), EnergyTier.LOW), IConfigurable {
    override var sideConfig: HashMap<Direction, InputConfig> = hashMapOf(
            Direction.NORTH to InputConfig.NONE,
            Direction.SOUTH to InputConfig.NONE,
            Direction.EAST  to InputConfig.NONE,
            Direction.WEST  to InputConfig.NONE,
            Direction.UP    to InputConfig.NONE,
            Direction.DOWN  to InputConfig.NONE
    )
}