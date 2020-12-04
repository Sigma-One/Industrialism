package sigmaone.industrialism.block.wiring

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Tickable
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.block.IBlockEntityRefreshable
import sigmaone.industrialism.component.wiring.ComponentWireNode
import sigmaone.industrialism.component.wiring.IComponentWireNode
import sigmaone.industrialism.item.ItemWireSpool
import team.reborn.energy.EnergyTier


class BlockEntityWireConnectorT0(
    energyTier: EnergyTier,
    height: Double,
    maxConnections: Int,
    wireTypes: Array<ItemWireSpool>,
) :
    BlockEntity(Industrialism.CONNECTOR_T0),
    IComponentWireNode,
    BlockEntityClientSerializable,
    Tickable,
    IBlockEntityRefreshable
{
    override val componentWireNode = ComponentWireNode(this, height, maxConnections, wireTypes)

    override fun refresh() {
        markDirty()
        if (world != null && !world!!.isClient) {
            sync()
        }
    }

    override fun fromClientTag(tag: CompoundTag?) {
        componentWireNode.fromClientTag(tag)
    }

    override fun toClientTag(tag: CompoundTag?): CompoundTag {
        val tag = componentWireNode.toClientTag(tag)
        return tag
    }

    override fun fromTag(state: BlockState?, tag: CompoundTag?) {
        super.fromTag(state, tag)
        componentWireNode.fromClientTag(tag)
    }

    override fun toTag(tag: CompoundTag?): CompoundTag {
        var tag = super.toTag(tag)
        tag = componentWireNode.toClientTag(tag)
        return tag
    }

    override fun tick() {
        componentWireNode.tick()
    }
}