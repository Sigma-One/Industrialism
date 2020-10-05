package sigmaone.industrialism.block.machine

import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.entity.BlockEntity
import net.minecraft.world.BlockView
import sigmaone.industrialism.block.BlockConfigurableSidedEnergyContainerBase

class BlockBattery(settings: Settings?) : BlockConfigurableSidedEnergyContainerBase(settings), BlockEntityProvider {
    override fun createBlockEntity(world: BlockView): BlockEntity? {
        return BlockEntityBattery()
    }
}