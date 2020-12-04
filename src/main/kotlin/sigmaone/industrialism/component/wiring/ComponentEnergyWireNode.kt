package sigmaone.industrialism.component.wiring

import net.minecraft.block.entity.BlockEntity
import sigmaone.industrialism.component.energy.IComponentEnergyContainer
import sigmaone.industrialism.item.ItemWireSpool
import sigmaone.industrialism.util.IO
import sigmaone.industrialism.util.MissingComponentException
import team.reborn.energy.EnergyTier

class ComponentEnergyWireNode(
    owner: BlockEntity,
    height: Double,
    maxConnections:
    Int,
    wireTypes: Array<ItemWireSpool>,
):
    ComponentWireNode(owner, height, maxConnections, wireTypes)
{
    init {
        if (owner !is IComponentEnergyContainer) {
            throw MissingComponentException("ComponentEnergyWireNode requires owner to implement IComponentEnergyContainer")
        }
    }
    var ioState = IO.NONE

    override fun tick() {
        super.tick()


    }
}