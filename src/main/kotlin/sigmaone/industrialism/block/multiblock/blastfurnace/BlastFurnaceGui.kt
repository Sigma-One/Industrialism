package sigmaone.industrialism.block.multiblock.blastfurnace

import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import io.github.cottonmc.cotton.gui.widget.WBar
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.WItemSlot
import io.github.cottonmc.cotton.gui.widget.data.Insets
import io.github.cottonmc.cotton.gui.widget.data.Texture
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import sigmaone.industrialism.Industrialism

class BlastFurnaceGui(gui: BlastFurnaceGuiDescription, player: PlayerEntity, title: Text) :
        CottonInventoryScreen<BlastFurnaceGuiDescription>(gui, player, title)

class BlastFurnaceGuiDescription(syncId: Int, playerInventory: PlayerInventory?, context: ScreenHandlerContext?) :
        SyncedGuiDescription(
                Industrialism.BLAST_FURNACE_SCREEN_HANDLER_TYPE,
                syncId,
                playerInventory,
                getBlockInventory(context, INVENTORY_SIZE),
                getBlockPropertyDelegate(context, 3)

        )
{
    companion object {
        private const val INVENTORY_SIZE = 3
    }

    init {
        val root = WGridPanel(1)
        setRootPanel(root)
        root.insets = Insets.ROOT_PANEL
        val ingredientSlot = WItemSlot.of(blockInventory, 0)
        val resultSlot     = WItemSlot.outputOf(blockInventory, 1)
        val fuelSlot       = WItemSlot.of(blockInventory, 2)
        resultSlot.isInsertingAllowed = false
        val progressBar = WBar(
                Texture(Identifier(Industrialism.MOD_ID, "textures/gui/arrow_base.png")),
                Texture(Identifier(Industrialism.MOD_ID, "textures/gui/arrow_filled.png")),
                1, 0, WBar.Direction.RIGHT
        )
        val fuelBar = WBar(
                Texture(Identifier(Industrialism.MOD_ID, "textures/gui/flame_filled.png")),
                Texture(Identifier(Industrialism.MOD_ID, "textures/gui/flame_base.png")),
                2, 0, WBar.Direction.DOWN
        )
        root.add(ingredientSlot, 40, 17)
        root.add(fuelSlot, 40, 53)
        root.add(progressBar, 66, 31)
        root.add(fuelBar, 40, 36)
        root.add(resultSlot, 102, 35)
        root.add(this.createPlayerInventoryPanel(), 0, 84)
        progressBar.setSize(24, 24)
        fuelBar.setSize(16, 16)
        root.validate(this)
    }
}