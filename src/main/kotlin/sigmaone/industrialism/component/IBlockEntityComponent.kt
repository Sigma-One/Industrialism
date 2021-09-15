package sigmaone.industrialism.component

import net.minecraft.block.entity.BlockEntity

interface IBlockEntityComponent<T: BlockEntity> {
    val owner: T
}