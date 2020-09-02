package sigmaone.industrialism.block.wiring;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import sigmaone.industrialism.Industrialism;
import sigmaone.industrialism.block.BlockEntitySidedEnergyContainer;
import sigmaone.industrialism.block.IConfigurable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class BlockEntityWireNode extends BlockEntitySidedEnergyContainer implements IWireNode, BlockEntityClientSerializable, IConfigurable {
    protected int maxConnections;
    protected ArrayList<BlockPos> connections;
    protected Industrialism.InputConfig state;
    private static final HashMap<Direction, Industrialism.InputConfig> sideConfig = new HashMap<>();

    static {
        sideConfig.put(Direction.NORTH, Industrialism.InputConfig.NONE);
        sideConfig.put(Direction.SOUTH, Industrialism.InputConfig.NONE);
        sideConfig.put(Direction.EAST , Industrialism.InputConfig.NONE);
        sideConfig.put(Direction.WEST , Industrialism.InputConfig.NONE);
        sideConfig.put(Direction.UP   , Industrialism.InputConfig.NONE);
        sideConfig.put(Direction.DOWN , Industrialism.InputConfig.NONE);
    }

    @Override
    protected void refresh() {
        super.refresh();
        if (this.getWorld() != null && !this.getWorld().isClient) {
            this.sync();
        }
    }

    public BlockEntityWireNode() {
        super(Industrialism.CONNECTOR_T0, 10, sideConfig);
        this.maxConnections = 16;
        this.connections = new ArrayList<>();
        this.state = Industrialism.InputConfig.NONE;
    }

    public Industrialism.InputConfig getIOState() {
        return this.state;
    }

    public void setIOState(Industrialism.InputConfig state) {
        this.state = state;
        this.refresh();
    }

    public void cycleIOState() {
        switch (this.state) {
            case NONE  : this.setIOState(Industrialism.InputConfig.INPUT); break;  // Block -> Network
            case INPUT : this.setIOState(Industrialism.InputConfig.OUTPUT); break; // Relay
            case OUTPUT: this.setIOState(Industrialism.InputConfig.NONE); break;   // Network -> Block
        }
    }

    public HashSet<BlockEntityWireNode> getConnectedSources() {
        HashSet<BlockPos> visited = new HashSet<>();
        HashSet<BlockEntityWireNode> sources = new HashSet<>();
        Stack<BlockEntityWireNode> nodeStack = new Stack<>();

        nodeStack.push(this);
        visited.add(this.getPos());

        // Loop until stack is empty
        while (!nodeStack.isEmpty()) {
            BlockEntityWireNode currentNode = nodeStack.pop();
            // Loop through connections
            for (BlockPos connectedPos : currentNode.getConnections()) {
                if (!visited.contains(connectedPos)) {
                    // Add any found sources to source set
                    BlockEntityWireNode blockEntity = (BlockEntityWireNode) this.getWorld().getBlockEntity(connectedPos);
                    if (blockEntity != null) {
                        if (blockEntity.getIOState() == Industrialism.InputConfig.OUTPUT) {
                            sources.add(blockEntity);
                        }
                        // Add connected nodes to stack and self to visited nodes
                        nodeStack.push(blockEntity);
                        visited.add(connectedPos);
                    }
                }
            }
        }
        return sources;
    }

    @Override
    public boolean addConnection(BlockPos pos) {
        if (this.connections.size() < this.maxConnections) {
            this.connections.add(pos);
            this.refresh();
            return true;
        }
        return false;
    }

    public void removeAllConnections() {
        while (!this.connections.isEmpty()) {
            this.removeConnection(this.connections.get(0));
        }
    }

    @Override
    public void removeConnection(BlockPos pos) {
        BlockEntityWireNode blockEntity = (BlockEntityWireNode) this.getWorld().getBlockEntity(pos);
        if (blockEntity != null) {
            blockEntity.rawRemoveConnection(this.getPos());
        }
        this.connections.remove(pos);
        this.refresh();
    }

    public void rawRemoveConnection(BlockPos pos) {
        this.connections.remove(pos);
    }

    @Override
    public ArrayList<BlockPos> getConnections() {
        return this.connections;
    }

    @Override
    public void tick() {
        super.tick();
        // Remove nonexistent connections, just in case
        if (this.connections.size() > 0) {
            HashSet<BlockPos> removalBuffer = new HashSet<>();
            for (BlockPos pos : this.getConnections()) {
                if (this.getWorld() != null && this.getWorld().getBlockEntity(pos) == null) {
                    removalBuffer.add(pos);
                }
            }
            this.connections.removeAll(removalBuffer);
        }
        if (this.state == Industrialism.InputConfig.INPUT) {
            ArrayList<BlockPos> acceptors = new ArrayList<>();
            for (BlockEntityWireNode node : this.getConnectedSources()) {
                acceptors.add(node.getPos());
            }
            float packet = this.maxTransfer / acceptors.size();
            for (BlockPos node : acceptors) {
                this.sendEnergy(node, packet);
            }
        }
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        int connectionAmount = tag.getInt("connection_amount");
        if (connectionAmount > 0) {
            for (int i = 0; i < connectionAmount; i++) {
                int[] pos = tag.getIntArray(String.valueOf(i));
                this.addConnection(new BlockPos(pos[0], pos[1], pos[2]));
            }
        }
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        tag.putInt("connection_amount", this.connections.size());
        if (this.connections.size() > 0) {
            for (int i = 0; i < this.connections.size(); i++) {
                tag.putIntArray(String.valueOf(i), new int[] {this.connections.get(i).getX(), this.connections.get(i).getY(), this.connections.get(i).getZ()});
            }
        }
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        int connectionAmount = tag.getInt("connection_amount");
        this.setIOState(Industrialism.InputConfig.values()[tag.getInt("io_mode")]);

        if (connectionAmount > 0) {
            for (int i = 0; i < connectionAmount; i++) {
                int[] pos = tag.getIntArray(String.valueOf(i));
                if (!this.getConnections().contains(new BlockPos(pos[0], pos[1], pos[2]))) {
                    this.addConnection(new BlockPos(pos[0], pos[1], pos[2]));
                }
            }
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag = super.toTag(tag);
        tag.putInt("connection_amount", this.connections.size());
        tag.putInt("io_mode", this.getIOState().ordinal());

        if (this.connections.size() > 0) {
            for (int i = 0; i < this.connections.size(); i++) {
                tag.putIntArray(String.valueOf(i), new int[] {this.connections.get(i).getX(), this.connections.get(i).getY(), this.connections.get(i).getZ()});
            }
        }
        return tag;
    }
}
