package sigmaone.industrialism.item

import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import sigmaone.industrialism.block.wiring.BlockEntityWireNode
import kotlin.math.pow
import kotlin.math.sqrt

class ItemWireSpool(
        settings: Settings?,
        val maxLength: Int,
        val thickness: Float,
        val colour: IntArray,
        val isInfinite: Boolean = false
    ) : Item(settings) {
    var linkA: BlockEntity? = null

    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        if (!context.world.isClient) {
            if (context.world.getBlockEntity(context.blockPos) is BlockEntityWireNode) {

                if (this !in (context.world.getBlockEntity(context.blockPos) as BlockEntityWireNode).wireTypes) {
                    context.player!!.sendMessage(
                        TranslatableText("popup.industrialism.wire.failed.type"),
                        true
                    )
                    return ActionResult.PASS
                }

                if (linkA == null) {
                    linkA = context.world.getBlockEntity(context.blockPos)
                    context.player!!.sendMessage(
                        TranslatableText(
                            "popup.industrialism.wire.source", "(" + linkA!!.pos.toShortString() + ")"),
                        true
                    )
                    return ActionResult.SUCCESS
                }

                else if (linkA!!.pos == context.blockPos) {
                    context.player!!.sendMessage(
                        TranslatableText("popup.industrialism.wire.failed.self"),
                        true
                    )
                    linkA = null
                    return ActionResult.PASS

                }
                else if (linkA!!.pos !== context.blockPos) {
                    if (!(linkA as BlockEntityWireNode?)!!.connections
                        .contains(context.blockPos)
                     && !(context.world.getBlockEntity(context.blockPos) as BlockEntityWireNode?)!!.connections
                        .contains((linkA as BlockEntityWireNode).pos)
                    ) {
                        val diff = sqrt(
                                (context.blockPos.y - (linkA as BlockEntityWireNode).pos.y).toFloat().pow(2)
                                  + (
                                        (context.blockPos.x - (linkA as BlockEntityWireNode).pos.x).toFloat().pow(2)
                                      + (context.blockPos.z - (linkA as BlockEntityWireNode).pos.z).toFloat().pow(2)
                                    )
                        )
                        if (diff > maxLength) {
                            context.player!!.sendMessage(TranslatableText("popup.industrialism.wire.failed.toofar"), true)
                            linkA = null
                            return ActionResult.PASS
                        }
                        if (!(linkA as BlockEntityWireNode?)!!.addConnection(context.blockPos, wireType = this)) {
                            context.player!!.sendMessage(TranslatableText("popup.industrialism.wire.failed.generic"), true)
                            linkA = null
                            return ActionResult.PASS
                        }
                        if (!(context.world.getBlockEntity(context.blockPos) as BlockEntityWireNode?)!!.addConnection((linkA as BlockEntityWireNode).pos, wireType = this)) {
                            context.player!!.sendMessage(TranslatableText("popup.industrialism.wire.failed.generic"), true)
                            (linkA as BlockEntityWireNode?)!!.removeConnection(context.blockPos)
                            linkA = null
                            return ActionResult.PASS
                        }

                        context.player!!.sendMessage(
                            TranslatableText(
                                "popup.industrialism.wire.linked", "(" + (linkA as BlockEntityWireNode).pos.toShortString() + ")",
                                 "("+ context.blockPos.toShortString() + ")"
                            ),
                            true
                        )

                        linkA = null
                        if (!isInfinite && !context.player!!.isCreative) {
                            context.stack.decrement(1)
                        }
                        return ActionResult.SUCCESS
                    }
                    else {
                        context.player!!.sendMessage(TranslatableText("popup.industrialism.wire.failed.exists"), true)
                        linkA = null
                        return ActionResult.PASS
                    }
                }
            }
        }
        return ActionResult.PASS
    }
}