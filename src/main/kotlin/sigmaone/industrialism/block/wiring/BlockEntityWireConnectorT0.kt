package sigmaone.industrialism.block.wiring

import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.item.ItemWireSpool
import team.reborn.energy.EnergyTier


class BlockEntityWireConnectorT0(
    energyTier: EnergyTier,
    height: Double,
    maxConnections: Int,
    wireTypes: Array<ItemWireSpool>,
) :
    BlockEntityEnergyWireNode(
        Industrialism.CONNECTOR_T0,
        energyTier,
        height,
        maxConnections,
        wireTypes
    )
{

}