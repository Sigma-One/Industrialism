package sigmaone.industrialism.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.block.machine.BlockEntityBattery
import sigmaone.industrialism.item.ItemScrewdriver

open class BlockConfigurableConnectableEnergyContainerBase(settings: Settings?) : Block(settings) {
    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        val blockEntity = world.getBlockEntity(pos) ?: return ActionResult.PASS
        if (!world.isClient) {
            if (blockEntity is BlockEntityBattery) {
                if (player.getStackInHand(hand).item === Industrialism.SCREWDRIVER) {
                    val sideTranslated: String
                    val modeTranslated: String
                    if (!(player.getStackInHand(hand).item as ItemScrewdriver).opposite) {
                        blockEntity.cycleSideConfig(hit.side)
                        sideTranslated = TranslatableText("variable." + Industrialism.MOD_ID + ".side." + hit.side
                                .toString().toLowerCase()).string
                        modeTranslated = TranslatableText("variable." + Industrialism.MOD_ID + ".ioconfig." + blockEntity
                                .sideConfig[hit.side]
                                .toString().toLowerCase()).string
                    }
                    else {
                        blockEntity.cycleSideConfig(hit.side.opposite)
                        sideTranslated = TranslatableText("variable." + Industrialism.MOD_ID + ".side." + hit.side
                                .opposite
                                .toString().toLowerCase()).string
                        modeTranslated = TranslatableText("variable." + Industrialism.MOD_ID + ".ioconfig." + blockEntity
                                .sideConfig[hit.side.opposite]
                                .toString().toLowerCase()).string
                    }
                    player.sendMessage(TranslatableText("popup." + Industrialism.MOD_ID + ".ioconfig.set.sided", sideTranslated, modeTranslated), true)
                }
                else {
                    player.sendMessage(TranslatableText("popup." + Industrialism.MOD_ID + ".energyamount.get", blockEntity.storedEnergy, blockEntity.maxEnergy), true)
                }
            }
        }
        return ActionResult.SUCCESS
    }
}