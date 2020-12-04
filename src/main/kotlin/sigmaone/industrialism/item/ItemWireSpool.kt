package sigmaone.industrialism.item

import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemUsageContext
import net.minecraft.state.property.Properties
import net.minecraft.text.TranslatableText
import net.minecraft.util.ActionResult
import sigmaone.industrialism.block.wiring.BlockEntityWireConnectorT0
import sigmaone.industrialism.component.wiring.IComponentWireNode
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
            val entity = context.world.getBlockEntity(context.blockPos)
            if (entity is IComponentWireNode) {
                if (this !in entity.componentWireNode.wireTypes) {
                    context.player!!.sendMessage(
                        TranslatableText("popup.industrialism.wire.failed.type"),
                        true
                    )
                    return ActionResult.PASS
                }

                if (linkA == null) {
                    linkA = entity
                    context.player!!.sendMessage(
                        TranslatableText(
                            "popup.industrialism.wire.source", "(" + linkA!!.pos.toShortString() + ")"
                        ),
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
                    val linkEntity = linkA!!
                    if (linkEntity is IComponentWireNode && linkA != null) {
                        if (!linkEntity.componentWireNode.connections.contains(context.blockPos)
                         && !linkEntity.componentWireNode.connections.contains((linkA as BlockEntityWireConnectorT0).pos)
                        ) {
                            val diff = sqrt(
                                (context.blockPos.y - linkEntity.pos.y).toFloat().pow(2)
                                + (
                                        (context.blockPos.x - linkEntity.pos.x).toFloat().pow(2)
                                        + (context.blockPos.z - linkEntity.pos.z).toFloat().pow(2)
                                  )
                            )
                            if (diff > maxLength) {
                                context.player!!.sendMessage(TranslatableText("popup.industrialism.wire.failed.toofar"), true)
                                linkA = null
                                return ActionResult.PASS
                            }
                            if (!linkEntity.componentWireNode.addConnection(
                                        context.blockPos,
                                        context.world.getBlockState(context.blockPos).get(Properties.FACING),
                                        entity.componentWireNode.height,
                                        this
                                    )
                            ) {
                                context.player!!.sendMessage(TranslatableText("popup.industrialism.wire.failed.generic"), true)
                                linkA = null
                                return ActionResult.PASS
                            }
                            if (!entity.componentWireNode.addConnection(
                                        linkEntity.pos,
                                        context.world.getBlockState(linkEntity.pos).get(Properties.FACING),
                                        linkEntity.componentWireNode.height,
                                        this
                                    )
                            ) {
                                context.player!!.sendMessage(TranslatableText("popup.industrialism.wire.failed.generic"), true)
                                linkEntity.componentWireNode.removeConnection(context.blockPos)
                                linkA = null
                                return ActionResult.PASS
                            }

                            context.player!!.sendMessage(
                                TranslatableText(
                                    "popup.industrialism.wire.linked", "(" + (linkA as BlockEntityWireConnectorT0).pos.toShortString() + ")",
                                    "(" + context.blockPos.toShortString() + ")"
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
        }
        return ActionResult.PASS
    }
}