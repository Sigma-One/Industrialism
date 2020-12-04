package sigmaone.industrialism.block.wiring

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.ItemUsageContext
import net.minecraft.nbt.CompoundTag
import net.minecraft.state.property.Properties
import net.minecraft.text.TranslatableText
import net.minecraft.util.math.Direction
import sigmaone.industrialism.block.IBlockEntityConfigurable
import sigmaone.industrialism.component.energy.ComponentEnergyContainer
import sigmaone.industrialism.component.energy.IComponentEnergyContainer
import sigmaone.industrialism.component.wiring.IComponentWireNode
import sigmaone.industrialism.item.ItemWireSpool
import sigmaone.industrialism.util.IO
import team.reborn.energy.Energy
import team.reborn.energy.EnergyTier
import java.util.*

abstract class BlockEntityEnergyWireNode(
    type: BlockEntityType<*>,
    energyTier: EnergyTier,
    height: Double,
    maxConnections: Int,
    wireTypes: Array<ItemWireSpool>
) : BlockEntityWireNode(
        type,
        energyTier,
        height,
        maxConnections,
        wireTypes
    ),
    IBlockEntityConfigurable,
    IComponentEnergyContainer
{
    override val configSpecial: Boolean
        get() = true

    override val componentEnergyContainer = ComponentEnergyContainer(
        this,
        energyTier,
        energyTier.maxInput.toDouble(),
        0.0,
        hashMapOf(
            Direction.NORTH to IO.NONE,
            Direction.SOUTH to IO.NONE,
            Direction.EAST  to IO.NONE,
            Direction.WEST  to IO.NONE,
            Direction.UP    to IO.NONE,
            Direction.DOWN  to IO.NONE
        )
    )

    override fun configure(side : Direction, context: ItemUsageContext?) {
        val side = world!!.getBlockState(pos).get(Properties.FACING).opposite
        componentEnergyContainer.sideConfig[side] = componentEnergyContainer.sideConfig[side]!!.next()
        componentWireNode.isRelay = componentEnergyContainer.sideConfig[side]!! == IO.NONE
        world!!.setBlockState(
            pos,
            world!!.getBlockState(pos).with(
                sigmaone.industrialism.util.Properties.IO,
                componentEnergyContainer.sideConfig[side]!!.ordinal
            )
        )
        val modeStr = when (componentEnergyContainer.sideConfig[side]!!) {
            IO.NONE   -> "none"
            IO.INPUT  -> "output"
            IO.OUTPUT -> "input"
        }
        if (context != null) {
            context.player!!.sendMessage(
                TranslatableText(
                    "popup.industrialism.io.set.unsided",
                    TranslatableText(
                        "variable.industrialism.io."
                        + modeStr
                    )
                ),
                true
            )
        }
    }

    override fun tick() {
        super.tick()
        componentEnergyContainer.tick()
        val acceptors = Stack<BlockEntity>()
        for (point in componentWireNode.networkEndpoints) {
            val endpoint = world!!.getBlockEntity(point)
            if (endpoint is IComponentEnergyContainer && endpoint is IComponentWireNode) {
                if (endpoint
                    .componentEnergyContainer
                    .sideConfig[world!!.getBlockState(endpoint.pos).get(Properties.FACING).opposite]
                ==  IO.OUTPUT
                ) {
                    acceptors.push(endpoint)
                }
            }
        }
        if (componentEnergyContainer
                .sideConfig[world!!.getBlockState(pos).get(Properties.FACING).opposite]
        ==  IO.INPUT
        ) {
            val amount = componentEnergyContainer.energyTier.maxOutput / acceptors.size.toDouble()
            for (acceptor in acceptors) {
                if (acceptor is IComponentEnergyContainer) {
                    Energy.of(componentEnergyContainer)
                        .into(Energy.of(acceptor.componentEnergyContainer))
                        .move(amount)
                }
            }
        }
    }

    override fun fromClientTag(tag: CompoundTag?) {
        componentEnergyContainer.fromClientTag(tag!!)
        super.fromClientTag(tag)
    }

    override fun toClientTag(tag: CompoundTag?): CompoundTag {
        val tag = componentEnergyContainer.toClientTag(tag!!)
        return super.toClientTag(tag)
    }

    override fun fromTag(state: BlockState?, tag: CompoundTag?) {
        componentEnergyContainer.fromTag(state!!, tag!!)
        super.fromTag(state, tag)
    }

    override fun toTag(tag: CompoundTag?): CompoundTag {
        val tag = componentEnergyContainer.toTag(tag!!)
        return super.toTag(tag)
    }
}