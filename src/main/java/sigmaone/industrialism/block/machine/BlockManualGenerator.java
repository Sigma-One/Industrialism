package sigmaone.industrialism.block.machine;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import sigmaone.industrialism.Industrialism;

public class BlockManualGenerator extends Block implements BlockEntityProvider {
    public BlockManualGenerator(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new BlockEntityManualGenerator();
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) {
            return ActionResult.PASS;
        }

        if (!world.isClient) {
            if (blockEntity instanceof BlockEntityManualGenerator) {
                ((BlockEntityManualGenerator) blockEntity).putEnergy(10);
                player.sendMessage(new TranslatableText("popup."+ Industrialism.MOD_ID+".energyamount.get", ((BlockEntityManualGenerator) blockEntity).getStoredEnergy(), ((BlockEntityManualGenerator) blockEntity).getMaxEnergy()), true);
            }
        }
        return ActionResult.SUCCESS;
    }
}
