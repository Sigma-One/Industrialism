package sigmaone.industrialism.component.wiring

import net.minecraft.block.entity.BlockEntity

/**
 * Wire node component interface
 *
 * @property componentWireNode The [ComponentWireNode] for the implementing [BlockEntity]
 */
interface IComponentWireNode<T: BlockEntity> {
    val componentWireNode: ComponentWireNode<T>
}