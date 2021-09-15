package sigmaone.industrialism.block.wiring

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.ItemUsageContext
import net.minecraft.nbt.NbtCompound
import net.minecraft.state.property.Properties
import net.minecraft.text.TranslatableText
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import sigmaone.industrialism.block.IBlockEntityConfigurable
import sigmaone.industrialism.block.IBlockEntityRefreshable
import sigmaone.industrialism.component.energy.ComponentEnergyContainer
import sigmaone.industrialism.component.energy.IComponentEnergyContainer
import sigmaone.industrialism.component.wiring.ComponentWireNode
import sigmaone.industrialism.component.wiring.IComponentWireNode
import sigmaone.industrialism.item.ItemWireSpool
import sigmaone.industrialism.util.IO
import team.reborn.energy.Energy
import team.reborn.energy.EnergyTier
import java.util.*

abstract class BlockEntityEnergyWireNode(
    blockPos: BlockPos?,
    blockState: BlockState?,
    type: BlockEntityType<*>,
    energyTier: EnergyTier,
    height: Double,
    maxConnections: Int,
    wireTypes: Array<ItemWireSpool>
):
    BlockEntity(type, blockPos, blockState),
    BlockEntityClientSerializable,
    IBlockEntityConfigurable,
    IBlockEntityRefreshable,
    IComponentEnergyContainer<BlockEntityEnergyWireNode>,
    IComponentWireNode<BlockEntityEnergyWireNode>
{
    override val configSpecial: Boolean
        get() = true

    override val componentWireNode = ComponentWireNode(
        this,
        height,
        maxConnections,
        wireTypes
    )

    override var componentEnergyContainer: ComponentEnergyContainer<BlockEntityEnergyWireNode> = ComponentEnergyContainer(
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
        // Deal with automatically configuring connected face if applicable
        val neighbour = world!!.getBlockEntity(pos.offset(side))
        if (neighbour is IComponentEnergyContainer<*>
        &&  neighbour is IBlockEntityConfigurable
        &&  !neighbour.configSpecial
        &&  side.opposite !in neighbour.componentEnergyContainer.lockedSides
        && (context == null
        ||  !context.player!!.isSneaking)) {
            neighbour.componentEnergyContainer.sideConfig[side.opposite] = when (componentEnergyContainer.sideConfig[side]) {
                IO.INPUT  -> IO.OUTPUT
                IO.OUTPUT -> IO.INPUT
                else      -> IO.NONE
            }
        }
        autoConfigOverridden = true
    }

    var autoConfigOverridden = false
    var tickCounter = 0

    companion object {
        fun tick(entity: BlockEntityEnergyWireNode, pos: BlockPos, state: BlockState, world: World) {
            entity.componentWireNode.tick()
            entity.tickCounter += 1
            if (entity.tickCounter == 10 && !entity.autoConfigOverridden) {
                val side = state.get(Properties.FACING).opposite
                val neighbour = world.getBlockEntity(entity.pos.offset(side))
                if (neighbour is IComponentEnergyContainer<*>) {
                    val targetIO = when (neighbour.componentEnergyContainer.sideConfig[side.opposite]) {
                        IO.INPUT  -> IO.OUTPUT
                        IO.OUTPUT -> IO.INPUT
                        else      -> IO.NONE
                    }
                    if (targetIO != entity.componentEnergyContainer.sideConfig[side]) {
                        entity.componentEnergyContainer.sideConfig[side] = targetIO
                        entity.componentWireNode.isRelay = entity.componentEnergyContainer.sideConfig[side]!! == IO.NONE
                        world.setBlockState(
                            pos,
                            world.getBlockState(pos).with(
                                sigmaone.industrialism.util.Properties.IO,
                                entity.componentEnergyContainer.sideConfig[side]!!.ordinal
                            )
                        )
                    }
                }
                entity.tickCounter = 0
            }

            entity.componentEnergyContainer.tick()
            val acceptors = Stack<BlockEntity>()
            for (point in entity.componentWireNode.networkEndpoints) {
                val endpoint = world!!.getBlockEntity(point)
                if (endpoint is IComponentEnergyContainer<*> && endpoint is IComponentWireNode<*>) {
                    if (endpoint
                            .componentEnergyContainer
                            .sideConfig[world!!.getBlockState(endpoint.pos).get(Properties.FACING).opposite]
                        == IO.OUTPUT
                    ) {
                        acceptors.push(endpoint)
                    }
                }
            }
            if (entity.componentEnergyContainer
                    .sideConfig[world!!.getBlockState(pos).get(Properties.FACING).opposite]
                == IO.INPUT
            ) {
                val amount = entity.componentEnergyContainer.energyTier.maxOutput / acceptors.size.toDouble()
                for (acceptor in acceptors) {
                    if (acceptor is IComponentEnergyContainer<*>) {
                        Energy.of(entity.componentEnergyContainer)
                            .into(Energy.of(acceptor.componentEnergyContainer))
                            .move(amount)
                    }
                }
            }
        }
    }

    override fun refresh() {
        markDirty()
        if (world != null && !world!!.isClient) {
            sync()
        }
    }

    override fun fromClientTag(tag: NbtCompound?) {
        componentEnergyContainer.fromClientTag(tag!!)
        componentWireNode.fromClientTag(tag)
    }

    override fun toClientTag(tag: NbtCompound?): NbtCompound {
        componentEnergyContainer.toClientTag(tag!!)
        componentWireNode.toClientTag(tag)
        return tag
    }

    override fun readNbt(tag: NbtCompound?) {
        componentEnergyContainer.readNbt(tag!!)
        componentWireNode.readNbt(tag)
        autoConfigOverridden = tag.getBoolean("autoconfig_override")
        super.readNbt(tag)
    }

    override fun writeNbt(tag: NbtCompound?): NbtCompound {
        var tag = componentEnergyContainer.writeNbt(tag!!)
        tag = componentWireNode.writeNbt(tag!!)
        tag.putBoolean("autoconfig_override", autoConfigOverridden)
        return super.writeNbt(tag)
    }
}