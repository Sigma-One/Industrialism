package sigmaone.industrialism.component.energy

import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.Direction
import sigmaone.industrialism.block.IBlockEntityRefreshable
import sigmaone.industrialism.component.IBlockEntityComponent
import sigmaone.industrialism.util.IO
import team.reborn.energy.*

class ComponentEnergyContainer<T: BlockEntity>(
    override val owner: T,
    var energyTier: EnergyTier,
    var maxEnergy: Double,
    var storedEnergy: Double,
    var sideConfig: HashMap<Direction, IO>,
    var lockedSides: ArrayList<Direction> = arrayListOf()
):
    IBlockEntityComponent<T>,
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
        if (owner is IBlockEntityRefreshable) { owner.refresh() }
    }

    val pushableNeighbours: ArrayList<EnergyHandler>
    get() {
        val list = ArrayList<EnergyHandler>()
        for (dir in Direction.values()) {
            if (sideConfig[dir] == IO.OUTPUT) {
                val neighbour = owner.world!!.getBlockEntity(owner.pos.offset(dir))
                if (
                   neighbour != null
                && neighbour is IComponentEnergyContainer<*>
                ) {
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
        if (owner is IBlockEntityRefreshable && amount != 0.0) { owner.refresh() }
    }

    fun writeNbt(tag: NbtCompound): NbtCompound {
        val subTag = NbtCompound()
        subTag.putDouble("energy", storedEnergy)
        for (dir in sideConfig.keys) {
            subTag.putInt(dir.toString(), sideConfig[dir]!!.ordinal)
        }
        tag.put("energyComponent", subTag)
        return tag
    }

    fun readNbt(tag: NbtCompound) {
        val subTag = tag.getCompound("energyComponent")
        storedEnergy = subTag.getDouble("energy")
        for (dir in sideConfig.keys) {
            sideConfig[dir] = IO.values()[subTag.getInt(dir.toString())]
        }
    }

    fun toClientTag(tag: NbtCompound): NbtCompound {
        val subTag = NbtCompound()
        subTag.putDouble("energy", storedEnergy)
        for (dir in sideConfig.keys) {
            subTag.putInt(dir.toString(), sideConfig[dir]!!.ordinal)
        }
        tag.put("energyComponent", subTag)
        return tag
    }

    fun fromClientTag(tag: NbtCompound) {
        val subTag = tag.getCompound("energyComponent")
        storedEnergy = subTag.getDouble("energy")
        for (dir in sideConfig.keys) {
            sideConfig[dir] = IO.values()[subTag.getInt(dir.toString())]
        }
    }
}