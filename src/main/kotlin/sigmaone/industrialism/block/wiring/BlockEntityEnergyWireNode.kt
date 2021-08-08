package sigmaone.industrialism.block.wiring

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.ItemUsageContext
import net.minecraft.nbt.NbtCompound
import net.minecraft.state.property.Properties
import net.minecraft.text.TranslatableText
import net.minecraft.util.math.BlockPos
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
    blockPos: BlockPos?,
    blockState: BlockState?,
    type: BlockEntityType<*>,
    energyTier: EnergyTier,
    height: Double,
    maxConnections: Int,
    wireTypes: Array<ItemWireSpool>
) : BlockEntityWireNode(
    blockPos,
    blockState,
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

    override var componentEnergyContainer: ComponentEnergyContainer = ComponentEnergyContainer(
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
        if (neighbour is IComponentEnergyContainer
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

    override fun tick() {
        super.tick()
        tickCounter += 1
        if (tickCounter == 10 && !autoConfigOverridden && !world!!.isClient) {
            val side = world!!.getBlockState(pos).get(Properties.FACING).opposite
            val neighbour = world!!.getBlockEntity(pos.offset(side))
            if (neighbour is IComponentEnergyContainer) {
                val targetIO = when (neighbour.componentEnergyContainer.sideConfig[side.opposite]) {
                    IO.INPUT  -> IO.OUTPUT
                    IO.OUTPUT -> IO.INPUT
                    else      -> IO.NONE
                }
                if (targetIO != componentEnergyContainer.sideConfig[side]) {
                    componentEnergyContainer.sideConfig[side] = targetIO
                    componentWireNode.isRelay = componentEnergyContainer.sideConfig[side]!! == IO.NONE
                    world!!.setBlockState(
                        pos,
                        world!!.getBlockState(pos).with(
                            sigmaone.industrialism.util.Properties.IO,
                            componentEnergyContainer.sideConfig[side]!!.ordinal
                        )
                    )
                }
            }
            tickCounter = 0
        }

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

    override fun fromClientTag(tag: NbtCompound?) {
        componentEnergyContainer.fromClientTag(tag!!)
        super.fromClientTag(tag)
    }

    override fun toClientTag(tag: NbtCompound?): NbtCompound {
        val tag = componentEnergyContainer.toClientTag(tag!!)
        return super.toClientTag(tag)
    }

    override fun readNbt(tag: NbtCompound?) {
        componentEnergyContainer.readNbt(tag!!)
        autoConfigOverridden = tag.getBoolean("autoconfig_override")
        super.readNbt(tag)
    }

    override fun writeNbt(tag: NbtCompound?): NbtCompound {
        val tag = componentEnergyContainer.writeNbt(tag!!)
        tag.putBoolean("autoconfig_override", autoConfigOverridden)
        return super.writeNbt(tag)
    }
}