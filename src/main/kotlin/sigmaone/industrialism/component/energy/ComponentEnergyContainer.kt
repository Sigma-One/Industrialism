package sigmaone.industrialism.component.energy

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.Direction
import sigmaone.industrialism.block.IBlockEntityRefreshable
import sigmaone.industrialism.component.Component
import sigmaone.industrialism.util.IO
import team.reborn.energy.*
import java.util.*
import kotlin.collections.ArrayList

class ComponentEnergyContainer(
    owner: BlockEntity,
    var energyTier: EnergyTier,
    var maxEnergy: Double,
    var storedEnergy: Double,
    var sideConfig: HashMap<Direction, IO>,
    var lockedSides: ArrayList<Direction> = arrayListOf()
):
    Component(owner),
    EnergyStorage
{
    override fun getMaxStoredPower(): Double {
        return maxEnergy
    }

    override fun getTier(): EnergyTier {
        return energyTier
    }

    override fun getStored(side: EnergySide?): Double {
        return storedEnergy
    }

    override fun setStored(amount: Double) {
        storedEnergy = amount
        if (owner is IBlockEntityRefreshable) {owner.refresh()}
    }

    val pushableNeighbours: ArrayList<EnergyHandler>
    get() {
        val list = ArrayList<EnergyHandler>()
        for (dir in Direction.values()) {
            if (sideConfig[dir] == IO.OUTPUT) {
                val neighbour = owner.world!!.getBlockEntity(owner.pos.offset(dir))
                if (neighbour is IComponentEnergyContainer) {
                    if (neighbour.componentEnergyContainer.sideConfig[dir.opposite] == IO.INPUT) {
                        list.add(Energy.of(neighbour.componentEnergyContainer))
                    }
                }
                else if (neighbour is EnergyStorage) {
                    list.add(Energy.of(neighbour))
                }
            }
        }
        return list
    }

    fun tick() {
        val amount = this.storedEnergy / pushableNeighbours.size
        for (handler in pushableNeighbours) {
            Energy.of(this).into(handler).move(amount)
        }
    }

    fun toTag(tag: CompoundTag): CompoundTag {
        val subTag = CompoundTag()
        subTag.putDouble("energy", storedEnergy)
        for (dir in sideConfig.keys) {
            subTag.putInt(dir.toString(), sideConfig[dir]!!.ordinal)
        }
        tag.put("energyComponent", subTag)
        return tag
    }

    fun fromTag(state: BlockState, tag: CompoundTag) {
        val subTag = tag.getCompound("energyComponent")
        storedEnergy = subTag.getDouble("energy")
        for (dir in sideConfig.keys) {
            sideConfig[dir] = IO.values()[subTag.getInt(dir.toString())]
        }
    }

    fun toClientTag(tag: CompoundTag): CompoundTag {
        val subTag = CompoundTag()
        subTag.putDouble("energy", storedEnergy)
        for (dir in sideConfig.keys) {
            subTag.putInt(dir.toString(), sideConfig[dir]!!.ordinal)
        }
        tag.put("energyComponent", subTag)
        return tag
    }

    fun fromClientTag(tag: CompoundTag) {
        val subTag = tag.getCompound("energyComponent")
        storedEnergy = subTag.getDouble("energy")
        for (dir in sideConfig.keys) {
            sideConfig[dir] = IO.values()[subTag.getInt(dir.toString())]
        }
    }

}