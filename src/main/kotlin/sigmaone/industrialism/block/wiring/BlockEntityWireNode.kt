package sigmaone.industrialism.block.wiring

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import sigmaone.industrialism.block.IBlockEntityConfigurable
import sigmaone.industrialism.block.IBlockEntityRefreshable
import sigmaone.industrialism.component.wiring.ComponentWireNode
import sigmaone.industrialism.component.wiring.IComponentWireNode
import sigmaone.industrialism.item.ItemWireSpool
import team.reborn.energy.EnergyTier

abstract class BlockEntityWireNode(
    blockPos: BlockPos?,
    blockState: BlockState?,
    type: BlockEntityType<*>,
    energyTier: EnergyTier,
    height: Double,
    maxConnections: Int,
    wireTypes: Array<ItemWireSpool>
) :
    BlockEntity(type, blockPos, blockState),
    IComponentWireNode,
    BlockEntityClientSerializable,
    IBlockEntityRefreshable,
    IBlockEntityConfigurable
{
    override val componentWireNode = ComponentWireNode(
        this,
        height,
        maxConnections,
        wireTypes
    )

    override fun refresh() {
        markDirty()
        if (world != null && !world!!.isClient) {
            sync()
        }
    }

    override fun fromClientTag(tag: NbtCompound?) {
        componentWireNode.fromClientTag(tag)
    }

    override fun toClientTag(tag: NbtCompound?): NbtCompound {
        val tag = componentWireNode.toClientTag(tag)
        return tag
    }

    override fun readNbt(tag: NbtCompound?) {
        super.readNbt(tag)
        componentWireNode.readNbt(tag)
    }

    override fun writeNbt(tag: NbtCompound?): NbtCompound {
        var tag = super.writeNbt(tag)
        tag = componentWireNode.writeNbt(tag)
        return tag
    }

    open fun tick() {
        componentWireNode.tick()
    }
}