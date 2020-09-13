package sigmaone.industrialism.block.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;


public class BlockMultiblockChildBase extends Block implements BlockEntityProvider {
    protected VoxelShape shape = VoxelShapes.cuboid(0d, 0d, 0d, 1d, 1d, 1d);

    public BlockMultiblockChildBase(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new BlockEntityMultiblockChildBase();
    }

    public void setShape(VoxelShape shape) {
        this.shape = shape;
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.shape;
    }


    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.shape;
    }



    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.getBlockEntity(pos) != null) {
            ((BlockEntityMultiblockChildBase) world.getBlockEntity(pos)).getParent().disassemble();
        }
        super.onBreak(world, pos, state, player);
    }
}
