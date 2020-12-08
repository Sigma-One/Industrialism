package sigmaone.industrialism.item

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World
import sigmaone.industrialism.Industrialism
import vazkii.patchouli.api.PatchouliAPI

class ItemEngineersJournal(settings: Settings) : Item(settings) {
    override fun use(world: World?, user: PlayerEntity?, hand: Hand?): TypedActionResult<ItemStack> {
        if(!world!!.isClient) {
            PatchouliAPI.instance.openBookGUI(user as ServerPlayerEntity, Identifier(Industrialism.MOD_ID, "engineers_journal"))
        }
        return TypedActionResult.success(user!!.getStackInHand(hand))
    }
}