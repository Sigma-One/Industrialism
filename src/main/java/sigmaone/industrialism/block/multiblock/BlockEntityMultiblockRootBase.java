package sigmaone.industrialism.block.multiblock;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

import java.util.HashSet;

public class BlockEntityMultiblockRootBase extends BlockEntity {
    protected HashSet<BlockEntityMultiblockChildBase> children;

    public BlockEntityMultiblockRootBase(BlockEntityType<?> type) {
        super(type);
        this.children = new HashSet<>();
    }

    public void addChild(BlockEntityMultiblockChildBase child) {
        this.children.add(child);
    }

    public void disassemble() {
        for (BlockEntityMultiblockChildBase child : this.children) {
            if (this.getWorld() != null && this.getWorld().getBlockState(child.getPos()) != null) {
                this.getWorld().removeBlock(child.getPos(), false);
                this.getWorld().removeBlockEntity(child.getPos());
            }
        }
    }
}
