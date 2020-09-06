package sigmaone.industrialism.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import sigmaone.industrialism.Industrialism;
import sigmaone.industrialism.block.multiblock.BlockMultiblockChild;
import sigmaone.industrialism.block.multiblock.BlockMultiblockRootBase;
import net.minecraft.state.property.Properties;
import java.util.HashSet;

public class ItemWrench extends MiningToolItem {
    public ItemWrench(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(attackDamage, attackSpeed, material, new HashSet<>(), settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockMultiblockRootBase foundMultiblock = null;
        for (BlockMultiblockRootBase multiblock : Industrialism.MULTIBLOCKS) {
            boolean result = true;
            if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() == multiblock.getLayout()[multiblock.getRootPos()[0]][multiblock.getRootPos()[1]][multiblock.getRootPos()[2]]) {
                int x = 0;
                int y = 0;
                int z = 0;
                int rootXOffset = -multiblock.getRootPos()[0];
                int rootYOffset = -multiblock.getRootPos()[1];
                int rootZOffset = -multiblock.getRootPos()[2];
                multiblockCheckLoop:
                for (Block[][] layer : multiblock.getLayout()) {
                    if (!result || y >= multiblock.getLayout().length) {
                        break;
                    }

                    switch (context.getSide()) {
                        case EAST, WEST -> x = 0;
                        case NORTH, SOUTH -> z = 0;
                    }

                    for (Block[] row : layer) {
                        if (!result || x >= layer.length) {
                            break multiblockCheckLoop;
                        }

                        switch (context.getSide()) {
                            case EAST, WEST -> z = 0;
                            case NORTH, SOUTH -> x = 0;
                        }

                        for (Block block : row) {
                            if (!result || z >= row.length) {
                                break multiblockCheckLoop;
                            }
                            BlockPos pos = context.getBlockPos().add(x, y, z).add(rootXOffset, rootYOffset, rootZOffset);
                            if (context.getWorld().getBlockState(pos).getBlock() != block) {
                                result = false;
                            }

                            switch (context.getSide()) {
                                case EAST -> z -= 1;
                                case WEST -> z += 1;
                                case NORTH -> x -= 1;
                                case SOUTH -> x += 1;
                            }
                        }
                        switch (context.getSide()) {
                            case EAST -> x -= 1;
                            case WEST -> x += 1;
                            case NORTH -> z += 1;
                            case SOUTH -> z -= 1;
                        }
                    }
                    y += 1;
                }
            }
            if (result) {
                foundMultiblock = multiblock;
                break;
            }
        }

        if (foundMultiblock != null) {
            int rootXOffset = -foundMultiblock.getRootPos()[0];
            int rootYOffset = -foundMultiblock.getRootPos()[1];
            int rootZOffset = -foundMultiblock.getRootPos()[2];
            int x = 0; int y = 0; int z = 0;
            // Clean up all blocks forming the multiblock by replacing with correct multiblock child blocks
            for (Block[][] layer : foundMultiblock.getLayout()) {
                switch (context.getSide()) {
                    case EAST,  WEST  -> x = 0;
                    case NORTH, SOUTH -> z = 0;
                }
                for (Block[] row : layer) {

                    switch (context.getSide()) {
                        case EAST,  WEST  -> z = 0;
                        case NORTH, SOUTH -> x = 0;
                    }
                    for (Block ignored : row) {

                        BlockPos pos = context.getBlockPos().add(x, y, z).add(rootXOffset, rootYOffset, rootZOffset);
                        context.getWorld().setBlockState(pos, Industrialism.MULTIBLOCK_CHILD_BLOCK.getDefaultState());
                        ((BlockMultiblockChild) context.getWorld().getBlockState(pos).getBlock()).setShape(foundMultiblock.getShape()[Math.abs(x)][Math.abs(y)][Math.abs(z)]);

                        switch (context.getSide()) {
                            case EAST  -> z -= 1;
                            case WEST  -> z += 1;
                            case NORTH -> x -= 1;
                            case SOUTH -> x += 1;
                        }
                    }
                    switch (context.getSide()) {
                        case EAST  -> x -= 1;
                        case WEST  -> x += 1;
                        case NORTH -> z += 1;
                        case SOUTH -> z -= 1;
                    }
                }
                y += 1;
            }
            // Set root block to multiblock root
            context.getWorld().setBlockState(context.getBlockPos(), Industrialism.TEST_MULTIBLOCK_BLOCK.getDefaultState().with(Properties.HORIZONTAL_FACING, context.getSide().getOpposite()));
        }
        return super.useOnBlock(context);
    }
}
