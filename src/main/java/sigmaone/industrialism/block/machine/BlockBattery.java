package sigmaone.industrialism.block.machine;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import sigmaone.industrialism.Industrialism;
import sigmaone.industrialism.item.ItemScrewdriver;


public class BlockBattery extends Block implements BlockEntityProvider {
    public BlockBattery(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new BlockEntityBattery();
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) {
            return ActionResult.PASS;
        }

        if (!world.isClient) {
            if (blockEntity instanceof BlockEntityBattery) {
                if (player.getStackInHand(hand).getItem() == Industrialism.SCREWDRIVER) {
                    String sideTranslated;
                    String modeTranslated;
                    if (!((ItemScrewdriver) player.getStackInHand(hand).getItem()).opposite) {
                        ((BlockEntityBattery) blockEntity).cycleSideConfig(hit.getSide());
                        sideTranslated = new TranslatableText("variable."+Industrialism.MOD_ID+".side."+hit.getSide()
                                .toString().toLowerCase()).getString();
                        modeTranslated = new TranslatableText("variable."+Industrialism.MOD_ID+".ioconfig."+((BlockEntityBattery) blockEntity)
                                .getSideConfig(hit.getSide())
                                .toString().toLowerCase()).getString();
                    }
                    else {
                        ((BlockEntityBattery) blockEntity).cycleSideConfig(hit.getSide().getOpposite());
                        sideTranslated = new TranslatableText("variable."+Industrialism.MOD_ID+".side."+hit.getSide()
                                .getOpposite()
                                .toString().toLowerCase()).getString();
                        modeTranslated = new TranslatableText("variable."+Industrialism.MOD_ID+".ioconfig."+((BlockEntityBattery) blockEntity)
                                .getSideConfig(hit.getSide().getOpposite())
                                .toString().toLowerCase()).getString();
                    }
                    player.sendMessage(new TranslatableText("popup."+Industrialism.MOD_ID+".ioconfig.set", sideTranslated, modeTranslated), true);
                }
                else {
                    player.sendMessage(new TranslatableText("popup."+ Industrialism.MOD_ID+".energyamount.get", ((BlockEntityBattery) blockEntity).getStoredEnergy(), ((BlockEntityBattery) blockEntity).getMaxEnergy()), true);
                }
            }
        }
        return ActionResult.SUCCESS;
    }
}
