package sigmaone.industrialism.block

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Tickable
import net.minecraft.util.math.Direction
import sigmaone.industrialism.Industrialism.InputConfig
import team.reborn.energy.Energy
import team.reborn.energy.EnergyHandler
import team.reborn.energy.EnergySide
import team.reborn.energy.EnergyTier
import kotlin.collections.HashMap

abstract class BlockEntityConnectableEnergyContainer (
        blockEntity: BlockEntityType<*>?,
        maxEnergy: Double,
        energyTier: EnergyTier
) :
        BlockEntityEnergyContainer(blockEntity, maxEnergy, energyTier),
        BlockEntityClientSerializable,
        Tickable
{
    var neighbourHandlers: HashMap<Direction, EnergyHandler> = HashMap()
    abstract var sideConfig: HashMap<Direction, InputConfig>

    override fun refresh() {
        super.refresh()
        if (getWorld() != null && !getWorld()!!.isClient) {
            sync()
        }
    }

    fun cycleSideConfig(side: Direction) {
        var ord = sideConfig[side]!!.ordinal
        if (ord == 2) {
            ord = 0
        } else {
            ord += 1
        }
        setSideConfig(side, InputConfig.values()[ord])
    }

    fun setSideConfig(side: Direction, state: InputConfig) {
        sideConfig[side] = state
        refresh()
    }


    private fun getNeighbour(dir: Direction?): BlockEntity? {
        return getWorld()!!.getBlockEntity(getPos().offset(dir))
    }

    private fun updateNeighbourHandlers() {
        for (side in Direction.values()) {
            val neighbour = getNeighbour(side)
            if (neighbour != null) {
                if (sideConfig[side] == InputConfig.OUTPUT) {
                    if (neighbour is BlockEntityConnectableEnergyContainer) {
                        if (neighbour.sideConfig[side.opposite] == InputConfig.INPUT) {
                            neighbourHandlers[side] = Energy.of(neighbour).side(EnergySide.fromMinecraft(side))
                            continue
                        }
                    }
                }
            }
            neighbourHandlers.remove(side)
        }
    }

    override fun tick() {
        updateNeighbourHandlers()
        for (h in neighbourHandlers) {
            Energy.of(this).side(h.key).into(h.value).move()
        }
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)
        for (dir in sideConfig.keys) {
            tag.putInt(dir.toString(), sideConfig[dir]!!.ordinal)
        }
        return tag
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)
        for (dir in sideConfig.keys) {
            sideConfig[dir] = InputConfig.values()[tag.getInt(dir.toString())]
        }
        refresh()
    }

    override fun toClientTag(tag: CompoundTag): CompoundTag {
        for (dir in sideConfig.keys) {
            tag.putInt(dir.toString(), sideConfig[dir]!!.ordinal)
        }
        return tag
    }

    override fun fromClientTag(tag: CompoundTag) {
        for (dir in sideConfig.keys) {
            setSideConfig(dir, InputConfig.values()[tag.getInt(dir.toString())])
        }
    }

}