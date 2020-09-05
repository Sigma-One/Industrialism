package sigmaone.industrialism.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import sigmaone.industrialism.energy.IEnergyContainer;
import sigmaone.industrialism.energy.IEnergyHandler;

public abstract class BlockEntityEnergyContainer extends BlockEntity implements IEnergyContainer, IEnergyHandler {

    private float maxEnergy;
    private float storedEnergy;

    protected void refresh() {
        this.markDirty();
    }

    public BlockEntityEnergyContainer(BlockEntityType<?> blockEntity, float maxEnergy) {
        super(blockEntity);
        this.maxEnergy = maxEnergy;
    }

    public void setMaxEnergy(float amount) {
        this.maxEnergy = amount;
        this.refresh();
    }

    public float takeEnergy(float amount) {
        if (this.storedEnergy - amount < 0) {
            amount = this.storedEnergy;
        }
        this.storedEnergy -= amount;
        this.refresh();
        return amount;
    }

    public float putEnergy(float amount) {
        float leftover = 0;
        if (this.storedEnergy + amount > this.maxEnergy) {
            amount -= this.storedEnergy + amount - this.maxEnergy;
            leftover = this.storedEnergy + amount - this.maxEnergy;
        }
        this.storedEnergy += amount;
        this.refresh();
        return leftover;
    }

    public float getMaxEnergy() {
        return this.maxEnergy;
    }

    public float getStoredEnergy() {
        return this.storedEnergy;
    }

    public float getAvailableEnergyCapacity() {
        return this.maxEnergy - this.storedEnergy;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putFloat("max_energy", this.maxEnergy);
        tag.putFloat("energy", this.storedEnergy);
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.maxEnergy = tag.getFloat("max_energy");
        this.storedEnergy = tag.getFloat("energy");
        this.refresh();
    }
}
