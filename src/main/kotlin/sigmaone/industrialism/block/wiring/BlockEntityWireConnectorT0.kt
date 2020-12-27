package sigmaone.industrialism.block.wiring

import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.item.ItemWireSpool
import team.reborn.energy.EnergyTier


class BlockEntityWireConnectorT0() :
    BlockEntityEnergyWireNode(
        Industrialism.CONNECTOR_T0,
        EnergyTier.LOW,
        0.20,
        16,
        arrayOf(Industrialism.COPPER.wireSpool as ItemWireSpool, Industrialism.DEBUG_LINKER as ItemWireSpool)
    )
{}