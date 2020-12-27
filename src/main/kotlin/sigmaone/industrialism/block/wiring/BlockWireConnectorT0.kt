package sigmaone.industrialism.block.wiring

import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.entity.BlockEntity
import net.minecraft.world.BlockView
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.item.ItemWireSpool
import team.reborn.energy.EnergyTier

class BlockWireConnectorT0(settings: Settings?) : BlockWireNode(settings!!, 5), BlockEntityProvider {
    override fun createBlockEntity(world: BlockView): BlockEntity? {
        return BlockEntityWireConnectorT0()
    }
}