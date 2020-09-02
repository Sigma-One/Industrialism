package sigmaone.industrialism.block.wiring;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import sigmaone.industrialism.Industrialism;

public class BlockWireNode extends FacingBlock {
    public static final IntProperty STATE = IntProperty.of("state", 0, 2);
    public BlockWireNode(AbstractBlock.Settings settings) {
        super(settings.nonOpaque());
        this.setDefaultState(this.getStateManager().getDefaultState().with(STATE, 0).with(FACING, Direction.DOWN));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(STATE);
        stateBuilder.add(Properties.FACING);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING)) {
            case UP   : return VoxelShapes.cuboid((1f/16f)*6f, 0f, (1f/16f)*6f, (1f/16f)*10f, (1f/16f)*4f, (1f/16f)*10f);
            case DOWN : return VoxelShapes.cuboid((1f/16f)*6f, (1f/16f)*12f, (1f/16f)*6f, (1f/16f)*10f, (1f/16f)*16f, (1f/16f)*10f);
            case NORTH: return VoxelShapes.cuboid((1f/16f)*6f, (1f/16f)*6f, (1f/16f)*12f, (1f/16f)*10f, (1f/16f)*10f, 1f);
            case SOUTH: return VoxelShapes.cuboid((1f/16f)*6f, (1f/16f)*6f, 0f, (1f/16f)*10f, (1f/16f)*10f, (1f/16f)*4f);
            case EAST : return VoxelShapes.cuboid(0f, (1f/16f)*6f, (1f/16f)*6f, (1f/16f)*4f, (1f/16f)*10f, (1f/16f)*10f);
            case WEST : return VoxelShapes.cuboid(1f, (1f/16f)*6f, (1f/16f)*6f, (1f/16f)*12f, (1f/16f)*10f, (1f/16f)*10f);
            default: throw new IllegalStateException("Illegal side: " + state.get(FACING));
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.getOutlineShape(state, world, pos, context);
    }

    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getSide());
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntityWireNode blockEntity = (BlockEntityWireNode) world.getBlockEntity(pos);
        blockEntity.removeAllConnections();
        super.onBreak(world, pos, state, player);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) {
            return ActionResult.PASS;
        }

        if (!world.isClient) {
            if (blockEntity instanceof BlockEntityWireNode) {
                if (player.getStackInHand(hand).getItem() == Industrialism.SCREWDRIVER) {
                    String modeTranslated;

                    ((BlockEntityWireNode) blockEntity).cycleSideConfig(state.get(FACING).getOpposite());
                    ((BlockEntityWireNode) blockEntity).cycleIOState();
                    world.setBlockState(pos, state.with(STATE, ((BlockEntityWireNode) blockEntity).getIOState().ordinal()));

                    modeTranslated = new TranslatableText("variable."+Industrialism.MOD_ID+".ioconfig."+((BlockEntityWireNode) blockEntity)
                            .getSideConfig(state.get(FACING).getOpposite())
                            .toString().toLowerCase()).getString();
                    player.sendMessage(new TranslatableText("popup."+Industrialism.MOD_ID+".ioconfig.set_noside", modeTranslated), true);

                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.PASS;
    }
}
