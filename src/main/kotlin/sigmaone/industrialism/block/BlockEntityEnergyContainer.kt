package sigmaone.industrialism.block

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.CompoundTag
import team.reborn.energy.EnergySide
import team.reborn.energy.EnergyStorage
import team.reborn.energy.EnergyTier

abstract class BlockEntityEnergyContainer(
        blockEntity: BlockEntityType<*>?,
        val maxEnergy: Double,
        val energyTier: EnergyTier,
        var storedEnergy: Double = 0f.toDouble()
) :
        BlockEntity(blockEntity),
        EnergyStorage
{

    protected open fun refresh() {
        markDirty()
    }

    override fun getMaxStoredPower(): Double {
        return maxEnergy
    }

    override fun getStored(side: EnergySide?): Double {
        return storedEnergy
    }

    override fun getTier(): EnergyTier {
        return energyTier
    }

    override fun setStored(amount: Double) {
        storedEnergy = amount
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)
        tag.putDouble("energy", storedEnergy)
        return tag
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)
        storedEnergy = tag.getDouble("energy")
        refresh()
    }
}