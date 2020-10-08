package sigmaone.industrialism.block

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.CompoundTag
import sigmaone.industrialism.energy.IEnergyContainer
import sigmaone.industrialism.energy.IEnergyHandler

abstract class BlockEntityEnergyContainer(blockEntity: BlockEntityType<*>?,
                                          override var maxEnergy: Float) : BlockEntity(blockEntity), IEnergyContainer, IEnergyHandler {

    override var storedEnergy = 0f

    protected open fun refresh() {
        markDirty()
    }

    override fun takeEnergy(amount: Float): Float {
        var amount = amount
        if (storedEnergy - amount < 0) {
            amount = storedEnergy
        }
        storedEnergy -= amount
        refresh()
        return amount
    }

    override fun putEnergy(amount: Float): Float {
        var amount = amount
        var leftover = 0f
        if (storedEnergy + amount > maxEnergy) {
            amount -= storedEnergy + amount - maxEnergy
            leftover = storedEnergy + amount - maxEnergy
        }
        storedEnergy += amount
        refresh()
        return leftover
    }

    override val availableEnergyCapacity: Float
        get() = maxEnergy - storedEnergy

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)
        tag.putFloat("max_energy", maxEnergy)
        tag.putFloat("energy", storedEnergy)
        return tag
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)
        maxEnergy = tag.getFloat("max_energy")
        storedEnergy = tag.getFloat("energy")
        refresh()
    }
}