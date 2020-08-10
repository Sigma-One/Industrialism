package sigmaone.industrialism.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import sigmaone.industrialism.Industrialism;

public class ItemScrewdriver extends Item {
    public boolean opposite;
    public ItemScrewdriver(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
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
        return new TypedActionResult<>(ActionResult.SUCCESS, user.getStackInHand(hand));
    }
}
