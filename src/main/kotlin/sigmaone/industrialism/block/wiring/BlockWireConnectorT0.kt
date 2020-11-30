package sigmaone.industrialism.block.wiring

import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.entity.BlockEntity
import net.minecraft.world.BlockView
import sigmaone.industrialism.Industrialism
import team.reborn.energy.EnergyTier

class BlockWireConnectorT0(settings: Settings?) : BlockWireNode(settings!!, 5), BlockEntityProvider {
    override fun createBlockEntity(world: BlockView): BlockEntity? {
        return BlockEntityWireNode(EnergyTier.LOW, 0.23, 16, arrayOf(Industrialism.COPPER.wireSpool, Industrialism.DEBUG_LINKER))
    }
}