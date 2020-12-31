package sigmaone.industrialism.block.machine

import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World
import sigmaone.industrialism.component.mechanical.IComponentMechanicalDevice

class BlockCrankHandle(settings: Settings?) : Block(settings), BlockEntityProvider {
    override fun createBlockEntity(world: BlockView?): BlockEntity {
        return BlockEntityCrankHandle()
    }

    override fun onUse(
        state: BlockState?,
        world: World?,
        pos: BlockPos?,
        player: PlayerEntity?,
        hand: Hand?,
        hit: BlockHitResult?
    ): ActionResult {
        val entity = world!!.getBlockEntity(pos)
        if (entity is IComponentMechanicalDevice) {
            entity.componentMechanicalDevice.rpm += 10.0
            if (entity.componentMechanicalDevice.rpm > entity.componentMechanicalDevice.maxRpm) {
                entity.componentMechanicalDevice.rpm = entity.componentMechanicalDevice.maxRpm
            }
        }
        return ActionResult.SUCCESS
    }
}