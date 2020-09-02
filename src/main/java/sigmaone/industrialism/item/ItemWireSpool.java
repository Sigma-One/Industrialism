package sigmaone.industrialism.item;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import sigmaone.industrialism.block.wiring.BlockEntityWireNode;

public class ItemWireSpool extends Item {
    public BlockEntity linkA;

    public ItemWireSpool(Settings settings) {
        super(settings);
        this.linkA = null;
    }

    public void setLinkA(BlockEntity entity) {
        this.linkA = entity;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getWorld().isClient) {
            if (context.getWorld().getBlockEntity(context.getBlockPos()) instanceof BlockEntityWireNode) {
                if (this.linkA == null) {
                    this.setLinkA(context.getWorld().getBlockEntity(context.getBlockPos()));
                    context.getPlayer().sendMessage(new TranslatableText("item.industrialism.wire.popup.source", "("+this.linkA.getPos().toShortString()+")"), true);
                    return ActionResult.SUCCESS;
                }
                else if (this.linkA.getPos() == context.getBlockPos()) {
                    context.getPlayer().sendMessage(new TranslatableText("item.industrialism.wire.popup.failed.self"), true);
                    return ActionResult.PASS;
                }
                else if (this.linkA.getPos() != context.getBlockPos()) {
                    if (!((BlockEntityWireNode) this.linkA).getConnections().contains(context.getBlockPos()) && !((BlockEntityWireNode) context.getWorld().getBlockEntity(context.getBlockPos())).getConnections().contains(this.linkA.getPos())) {
                        if (!((BlockEntityWireNode) this.linkA).addConnection(context.getBlockPos())) {
                            context.getPlayer().sendMessage(new TranslatableText("item.industrialism.wire.popup.failed"), true);
                            this.linkA = null;
                            return ActionResult.PASS;
                        }
                        if (!((BlockEntityWireNode) context.getWorld().getBlockEntity(context.getBlockPos())).addConnection(this.linkA.getPos())) {
                            context.getPlayer().sendMessage(new TranslatableText("item.industrialism.wire.popup.failed"), true);
                            ((BlockEntityWireNode) this.linkA).removeConnection(context.getBlockPos());
                            this.linkA = null;
                            return ActionResult.PASS;
                        }
                        context.getPlayer().sendMessage(new TranslatableText("item.industrialism.wire.popup.linked", "("+this.linkA.getPos().toShortString()+")", "("+context.getBlockPos().toShortString()+")"), true);
                        this.linkA = null;
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }
        return ActionResult.PASS;
    }
}
