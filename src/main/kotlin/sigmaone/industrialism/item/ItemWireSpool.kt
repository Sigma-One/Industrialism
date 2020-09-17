package sigmaone.industrialism.item

import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import sigmaone.industrialism.block.wiring.BlockEntityWireNode

class ItemWireSpool(settings: Settings?) : Item(settings) {
    var linkA: BlockEntity? = null

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        if (!context.world.isClient) {
            if (context.world.getBlockEntity(context.blockPos) is BlockEntityWireNode) {
                if (linkA == null) {
                    linkA = context.world.getBlockEntity(context.blockPos)
                    context.player!!.sendMessage(TranslatableText("item.industrialism.wire.popup.source", "(" + linkA!!.pos.toShortString() + ")"), true)
                    return ActionResult.SUCCESS
                }

                else if (linkA!!.pos === context.blockPos) {
                    context.player!!.sendMessage(TranslatableText("item.industrialism.wire.popup.failed.self"), true)
                    return ActionResult.PASS

                }
                else if (linkA!!.pos !== context.blockPos) {
                    if (!(linkA as BlockEntityWireNode?)!!.connections.contains(context.blockPos) && !(context.world.getBlockEntity(context.blockPos) as BlockEntityWireNode?)!!.connections.contains((linkA as BlockEntityWireNode).pos)) {
                        if (!(linkA as BlockEntityWireNode?)!!.addConnection(context.blockPos)) {
                            context.player!!.sendMessage(TranslatableText("item.industrialism.wire.popup.failed"), true)
                            linkA = null
                            return ActionResult.PASS
                        }
                        if (!(context.world.getBlockEntity(context.blockPos) as BlockEntityWireNode?)!!.addConnection((linkA as BlockEntityWireNode).pos)) {
                            context.player!!.sendMessage(TranslatableText("item.industrialism.wire.popup.failed"), true)
                            (linkA as BlockEntityWireNode?)!!.removeConnection(context.blockPos)
                            linkA = null
                            return ActionResult.PASS
                        }

                        context.player!!.sendMessage(TranslatableText("item.industrialism.wire.popup.linked", "(" + (linkA as BlockEntityWireNode).pos.toShortString() + ")", "(" + context.blockPos.toShortString() + ")"), true)
                        linkA = null
                        return ActionResult.SUCCESS
                    }
                }
            }
        }
        return ActionResult.PASS
    }
}