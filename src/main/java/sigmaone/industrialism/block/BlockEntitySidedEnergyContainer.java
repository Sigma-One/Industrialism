package sigmaone.industrialism.block;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import sigmaone.industrialism.Industrialism.InputConfig;
import sigmaone.industrialism.energy.IBlockEnergyHandler;
import sigmaone.industrialism.energy.ISidedBlockEnergyHandler;

import java.util.HashMap;
import java.util.Vector;

public abstract class BlockEntitySidedEnergyContainer extends BlockEntityEnergyContainer implements BlockEntityClientSerializable, IBlockEnergyHandler, ISidedBlockEnergyHandler, Tickable {

    private HashMap<Direction, InputConfig> sideConfig;
    protected float maxTransfer;

    @Override
    protected void refresh() {
        super.refresh();
        if (this.getWorld() != null && !this.getWorld().isClient) {
            this.sync();
        }
    }

    public BlockEntitySidedEnergyContainer(BlockEntityType<?> blockEntity, float maxEnergy, HashMap<Direction, InputConfig> sideConfig) {
        super(blockEntity, maxEnergy);
        this.sideConfig = (HashMap<Direction, InputConfig>)sideConfig.clone();
        this.maxTransfer = 1;
    }

    public void cycleSideConfig(Direction side) {
        int ord = this.getSideConfig(side).ordinal();
        if (ord == 2) {
            ord = 0;
        }
        else {
            ord += 1;
        }
        this.setSideConfig(side, InputConfig.values()[ord]);
    }

    public void setSideConfig(Direction side, InputConfig state) {
        this.sideConfig.put(side, state);
        this.refresh();
    }

    public InputConfig getSideConfig(Direction side) {
        return this.sideConfig.get(side);
    }

    public BlockEntity getNeighbour(Direction dir) {
        return this.getWorld().getBlockEntity(this.getPos().offset(dir));
    }

    @Override
    public Vector<Direction> getAcceptingNeighbours() {
        Vector<Direction> acceptors = new Vector<Direction>() {};
        for (Direction dir : Direction.values()) {
            if (!(this.getNeighbour(dir) == null)) {
                if (this.getSideConfig(dir) == InputConfig.OUTPUT) {
                    if (this.getNeighbour(dir) instanceof BlockEntitySidedEnergyContainer) {
                        if (((BlockEntitySidedEnergyContainer) this.getNeighbour(dir)).getSideConfig(dir.getOpposite()) == InputConfig.INPUT) {
                            acceptors.add(dir);
                        }
                    }
                }
            }
        }
        return acceptors;
    }

    @Override
    public void sendEnergy(BlockPos pos, float amount) {
        if (this.getWorld().getBlockEntity(pos) instanceof BlockEntityEnergyContainer) {
            BlockEntityEnergyContainer blockEntity = (BlockEntityEnergyContainer) this.getWorld().getBlockEntity(pos);

            float packet = amount;
            if (amount > this.maxTransfer) {
                amount = this.maxTransfer;
            }
            if(blockEntity.getAvailableEnergyCapacity() < amount) {
                packet = blockEntity.getAvailableEnergyCapacity();
            }

            packet = this.takeEnergy(packet);
            float leftover = blockEntity.putEnergy(packet);
            this.putEnergy(leftover);
            this.refresh();
        }
    }

    @Override
    public void tick() {
        Vector<Direction> acceptors = this.getAcceptingNeighbours();
        for (Direction d : acceptors) {
            if (this.getNeighbour(d) instanceof BlockEntitySidedEnergyContainer) {
                this.sendEnergy(this.getNeighbour(d).getPos(), this.maxTransfer);
            }
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        for (Direction direction : this.sideConfig.keySet()) {
            tag.putInt(direction.toString(), this.getSideConfig(direction).ordinal());
        }
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        for (Direction direction : sideConfig.keySet()) {
            this.sideConfig.put(direction, InputConfig.values()[tag.getInt(direction.toString())]);
        }
        this.refresh();
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        for (Direction direction : this.sideConfig.keySet()) {
            tag.putInt(direction.toString(), this.getSideConfig(direction).ordinal());
        }
        return tag;
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        for (Direction direction : this.sideConfig.keySet()) {
            this.setSideConfig(direction, InputConfig.values()[tag.getInt(direction.toString())]);
        }
    }
}
