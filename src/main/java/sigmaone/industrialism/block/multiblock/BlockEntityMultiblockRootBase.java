package sigmaone.industrialism.block.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

import java.util.HashSet;

public class BlockEntityMultiblockRootBase extends BlockEntity {
    protected HashSet<BlockEntityMultiblockChildBase> children;
    protected Block[][][] layout;
    protected int[] posInLayout;

    public BlockEntityMultiblockRootBase(BlockEntityType<?> type) {
        super(type);
        this.children = new HashSet<>();
    }

    public void setLayout(Block[][][] layout) {
        this.layout = layout;
    }

    public void setPosInLayout(int[] pos) {
        this.posInLayout = pos;
    }

    public void addChild(BlockEntityMultiblockChildBase child) {
        this.children.add(child);
    }

    public void disassemble() {
        for (BlockEntityMultiblockChildBase child : this.children) {
            if (this.getWorld() != null && this.getWorld().getBlockState(child.getPos()) != null) {
                this.getWorld().setBlockState(child.getPos(), (this.layout[child.getPosInLayout()[1]][child.getPosInLayout()[0]][child.getPosInLayout()[2]].getDefaultState()));
                this.getWorld().removeBlockEntity(child.getPos());
            }
        }
        if (this.getWorld() != null) {
            this.getWorld().setBlockState(this.getPos(), this.layout[this.posInLayout[1]][this.posInLayout[0]][this.posInLayout[2]].getDefaultState());
            this.getWorld().removeBlockEntity(this.getPos());
        }
    }
}
