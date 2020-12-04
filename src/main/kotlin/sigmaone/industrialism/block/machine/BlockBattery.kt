package sigmaone.industrialism.block.machine

import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.component.energy.IComponentEnergyContainer
import kotlin.math.roundToInt

class BlockBattery(settings: Settings?) : Block(settings), BlockEntityProvider {
    override fun createBlockEntity(world: BlockView): BlockEntity {
        return BlockEntityBattery()
    }

    override fun onUse(state: BlockState?, world: World?, pos: BlockPos?, player: PlayerEntity?, hand: Hand?, hit: BlockHitResult?): ActionResult {
        val blockEntity = world!!.getBlockEntity(pos!!)
        if (blockEntity is IComponentEnergyContainer) {
            player!!.sendMessage(
                TranslatableText(
                    "popup." + Industrialism.MOD_ID + ".energyamount.get",
                    (blockEntity.componentEnergyContainer.storedEnergy * 10).roundToInt() / 10.0,
                    blockEntity.componentEnergyContainer.maxEnergy
                ), true
            )
        }
        return super.onUse(state, world, pos, player, hand, hit)
    }
}