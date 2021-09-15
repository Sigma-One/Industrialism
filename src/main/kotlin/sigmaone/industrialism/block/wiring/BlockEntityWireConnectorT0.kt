package sigmaone.industrialism.block.wiring

import net.minecraft.block.BlockState
import net.minecraft.util.math.BlockPos
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.item.ItemWireSpool
import team.reborn.energy.EnergyTier


class BlockEntityWireConnectorT0(
    blockPos: BlockPos?,
    blockState: BlockState?
) :
    BlockEntityEnergyWireNode(
        blockPos,
        blockState,
        Industrialism.CONNECTOR_T0,
        EnergyTier.LOW,
        0.20,
        16,
        arrayOf(Industrialism.COPPER.wireSpool as ItemWireSpool, Industrialism.DEBUG_LINKER as ItemWireSpool)
    )
{}