package sigmaone.industrialism.block;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import sigmaone.industrialism.Industrialism.SideEnergyConfig;
import sigmaone.industrialism.energy.IBlockEnergyHandler;
import sigmaone.industrialism.energy.ISidedBlockEnergyHandler;

import java.util.HashMap;
import java.util.Vector;

public abstract class BlockEntitySidedEnergyContainer extends BlockEntityEnergyContainer implements BlockEntityClientSerializable, IBlockEnergyHandler, ISidedBlockEnergyHandler, Tickable {

    private HashMap<Direction, SideEnergyConfig> sideConfig = new HashMap<>();
    private int maxTransfer;

    @Override
    protected void refresh() {
        super.refresh();
        if (!this.getWorld().isClient) {
            this.sync();
        }
    }

    public BlockEntitySidedEnergyContainer(BlockEntityType<?> blockEntity, int maxEnergy, HashMap<Direction, SideEnergyConfig> sideConfig) {
        super(blockEntity, maxEnergy);
        this.sideConfig = (HashMap<Direction, SideEnergyConfig>)sideConfig.clone();
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
        this.setSideConfig(side, SideEnergyConfig.values()[ord]);
    }

    public void setSideConfig(Direction side, SideEnergyConfig state) {
        this.sideConfig.put(side, state);
        refresh();
    }

    public SideEnergyConfig getSideConfig(Direction side) {
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
                if (this.getSideConfig(dir) == SideEnergyConfig.OUTPUT) {
                    if (this.getNeighbour(dir) instanceof BlockEntitySidedEnergyContainer) {
                        if (((BlockEntitySidedEnergyContainer) this.getNeighbour(dir)).getSideConfig(dir.getOpposite()) == SideEnergyConfig.INPUT) {
                            acceptors.add(dir);
                        }
                    }
                }
            }
        }
        return acceptors;
    }

    @Override
    public void sendEnergy(BlockPos pos) {
        if (this.getWorld().getBlockEntity(pos) instanceof BlockEntityEnergyContainer) {
            BlockEntityEnergyContainer blockEntity = (BlockEntityEnergyContainer) this.getWorld().getBlockEntity(pos);
            int packet = this.maxTransfer;
            if(blockEntity.getAvailableEnergyCapacity() < this.maxTransfer) {
                packet = blockEntity.getAvailableEnergyCapacity();
            }
            packet = this.takeEnergy(packet);
            blockEntity.putEnergy(packet);
        }
    }

    @Override
    public void tick() {
        Vector<Direction> acceptors = this.getAcceptingNeighbours();
        for (Direction d : acceptors) {
            if (this.getNeighbour(d) instanceof BlockEntitySidedEnergyContainer) {
                this.sendEnergy(this.getNeighbour(d).getPos());
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
            this.sideConfig.put(direction, SideEnergyConfig.values()[tag.getInt(direction.toString())]);
        }
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
            this.setSideConfig(direction, SideEnergyConfig.values()[tag.getInt(direction.toString())]);
        }
    }
}
