package sigmaone.industrialism.component

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.entity.BlockEntity

abstract class Component(val owner: BlockEntity) {
    fun refreshOwner() {
        owner.markDirty()
        if (owner.hasWorld() && owner.world!!.isClient && owner is BlockEntityClientSerializable) {
            owner.sync()
        }
    }
}