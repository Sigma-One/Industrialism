package sigmaone.industrialism.block.multiblock;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import sigmaone.industrialism.Industrialism;

public class BlockEntityMultiblockChildBase extends BlockEntity {
    protected BlockEntityMultiblockRootBase parent;

    public BlockEntityMultiblockChildBase() {
        super(Industrialism.MULTIBLOCK_CHILD);
    }

    public void setParent(BlockEntityMultiblockRootBase parent) {
        this.parent = parent;
        parent.addChild(this);
    }

    public BlockEntityMultiblockRootBase getParent() {
        return this.parent;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (this.getParent() != null) {
            tag.putIntArray("parent", new int[] {this.getParent().getPos().getX(), this.getParent().getPos().getY(), this.getParent().getPos().getZ()});
        }
        return super.toTag(tag);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        if (this.getWorld() != null) {
            int[] pos = tag.getIntArray("parent");
            if (pos.length != 0) {
                this.setParent((BlockEntityMultiblockRootBase) this.getWorld().getBlockEntity(new BlockPos(pos[0], pos[1], pos[2])));
            }
        }
    }
}
