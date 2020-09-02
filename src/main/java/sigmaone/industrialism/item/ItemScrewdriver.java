package sigmaone.industrialism.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import sigmaone.industrialism.Industrialism;
import sigmaone.industrialism.block.IConfigurable;

public class ItemScrewdriver extends Item {
    public boolean opposite;
    public ItemScrewdriver(Settings settings) {
        super(settings.maxCount(1));
    }

    public void switchMode(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            if (this.opposite) {
                this.opposite = false;
                user.sendMessage(new TranslatableText("item."+ Industrialism.MOD_ID+".screwdriver.popup.config_normal"), true);
                user.getStackInHand(hand).getOrCreateTag().putInt("CustomModelData", 0);
            }
            else {
                this.opposite = true;
                user.sendMessage(new TranslatableText("item."+ Industrialism.MOD_ID+".screwdriver.popup.config_opposite"), true);
                user.getStackInHand(hand).getOrCreateTag().putInt("CustomModelData", 1);
            }
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().getBlockEntity(context.getBlockPos()) instanceof IConfigurable) {
            return super.useOnBlock(context);
        }
        else {
            this.switchMode(context.getWorld(), context.getPlayer(), context.getHand());
            return ActionResult.SUCCESS;
        }
    }
}
