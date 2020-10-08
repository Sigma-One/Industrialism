package sigmaone.industrialism.block.multiblock.machine.cokeoven

import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import io.github.cottonmc.cotton.gui.widget.WBar
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.data.Texture
import net.minecraft.client.util.SpriteIdentifier
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import sigmaone.industrialism.Industrialism


class CokeOvenGui(gui: CokeOvenGuiDescription, player: PlayerEntity, title: Text) :
        CottonInventoryScreen<CokeOvenGuiDescription>(gui, player, title)

class CokeOvenGuiDescription(syncId: Int, playerInventory: PlayerInventory?, context: ScreenHandlerContext?) :
        SyncedGuiDescription(
                Industrialism.COKE_OVEN_SCREEN_HANDLER_TYPE,
                syncId,
                playerInventory,
                getBlockInventory(context, INVENTORY_SIZE),
                getBlockPropertyDelegate(context, 2)

        )
{
    companion object {
        private const val INVENTORY_SIZE = 3
    }

    init {
        val root = WGridPanel(1)
        setRootPanel(root)
        val ingredientSlot = WItemSlot.of(blockInventory, 0)
        val resultSlot     = WItemSlot.outputOf(blockInventory, 1)
        val progressBar    = WBar(
                Texture(Identifier(Industrialism.MOD_ID, "textures/gui/arrow_base.png")),
                Texture(Identifier(Industrialism.MOD_ID, "textures/gui/arrow_filled.png")),
                0, 1, WBar.Direction.RIGHT
        )
        root.add(ingredientSlot, 40, 25)
        root.add(progressBar, 66, 22)
        root.add(resultSlot, 102, 25)
        root.add(this.createPlayerInventoryPanel(), 0, 60)
        progressBar.setSize(24, 24)
        root.validate(this)
    }
}