package sigmaone.industrialism.item;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

public class ItemWrench extends Item {
    public static Block[][][] testmb = new Block[][][] {
            { // layer
                    { Blocks.COBBLESTONE, Blocks.COBBLESTONE }, // row
                    { Blocks.COBBLESTONE, Blocks.COBBLESTONE }  // row
            }, // \layer
            { // layer
                    { Blocks.COBBLESTONE, Blocks.COBBLESTONE }, // row
                    { Blocks.COBBLESTONE, Blocks.COBBLESTONE }  // row
            } // \layer
    };
    public static int[] root = new int[] {0, 1, 0};

    public ItemWrench(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        boolean result = true;
        if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() == testmb[root[0]][root[1]][root[2]]) {
            int x = 0; int y = 0; int z = 0;
            int rootXOffset = -root[0]; int rootYOffset = -root[1]; int rootZOffset = -root[2];
            multiblockCheckLoop:
            for (Block[][] layer : testmb) {
                if (!result || y >= testmb.length) {
                    break;
                }

                switch (context.getSide()) {
                    case EAST,  WEST  -> x = 0;
                    case NORTH, SOUTH -> z = 0;
                }

                for (Block[] row : layer) {
                    if (!result || x >= layer.length) {
                        break multiblockCheckLoop;
                    }

                    switch (context.getSide()) {
                        case EAST,  WEST  -> z = 0;
                        case NORTH, SOUTH -> x = 0;
                    }

                    for (Block block : row) {
                        if (!result || z >= row.length) {
                            break multiblockCheckLoop;
                        }
                        BlockPos testPos = context.getBlockPos().add(x, y, z).add(rootXOffset, rootYOffset, rootZOffset);
                        if (context.getWorld().getBlockState(testPos).getBlock() != block) {
                            result = false;
                        }

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
            context.getPlayer().sendMessage(new LiteralText(String.valueOf(result)), false);
        }
        return super.useOnBlock(context);
    }
}
