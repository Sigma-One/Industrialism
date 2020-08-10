package sigmaone.industrialism.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import sigmaone.industrialism.energy.IEnergyContainer;
import sigmaone.industrialism.energy.IEnergyHandler;

public abstract class BlockEntityEnergyContainer extends BlockEntity implements IEnergyContainer, IEnergyHandler {

    private int maxEnergy;
    private int storedEnergy;

    protected void refresh() {
        this.markDirty();
    }

    public BlockEntityEnergyContainer(BlockEntityType<?> blockEntity, int maxEnergy) {
        super(blockEntity);
        this.maxEnergy = maxEnergy;
    }

    public int takeEnergy(int amount) {
        if (this.storedEnergy - amount < 0) {
            amount = this.storedEnergy;
        }
        this.storedEnergy -= amount;
        this.refresh();
        return amount;
    }

    public void putEnergy(int amount) {
        if (storedEnergy + amount > maxEnergy) {
            amount -= storedEnergy + amount - maxEnergy;
        }
        this.storedEnergy += amount;
        this.refresh();
    }

    public int getMaxEnergy() {
        return this.maxEnergy;
    }

    public int getStoredEnergy() {
        return this.storedEnergy;
    }

    public int getAvailableEnergyCapacity() {
        return this.maxEnergy - this.storedEnergy;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putInt("energy", this.storedEnergy);
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.storedEnergy = tag.getInt("energy");
    }
}
