package sigmaone.industrialism.block.battery

import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.component.energy.IComponentEnergyContainer
import kotlin.math.roundToInt

class BlockBattery(settings: Settings?) : BlockWithEntity(settings), BlockEntityProvider {
    override fun createBlockEntity(blockPos: BlockPos?, blockState: BlockState?): BlockEntity {
        return BlockEntityBattery(blockPos, blockState)
    }

    override fun onUse(state: BlockState?, world: World?, pos: BlockPos?, player: PlayerEntity?, hand: Hand?, hit: BlockHitResult?): ActionResult {
        val blockEntity = world!!.getBlockEntity(pos!!)
        if (blockEntity is IComponentEnergyContainer<*>) {
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

    override fun getRenderType(state: BlockState?): BlockRenderType {
        return BlockRenderType.MODEL
    }

    override fun <T : BlockEntity?> getTicker(
        blockWorld: World?,
        blockState: BlockState?,
        type: BlockEntityType<T>?
    ): BlockEntityTicker<T>? {
        return BlockWithEntity.checkType(
            type, Industrialism.BATTERY
        ) { _: World, _: BlockPos, _: BlockState, entity: BlockEntityBattery ->
            BlockEntityBattery.tick(
                entity
            )
        }
    }
}