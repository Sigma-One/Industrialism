package sigmaone.industrialism.component.energy

import net.minecraft.block.entity.BlockEntity

/**
 * Energy container component interface
 *
 * @property componentEnergyContainer The [ComponentEnergyContainer] for the implementing [BlockEntity]
 */
interface IComponentEnergyContainer {
    val componentEnergyContainer: ComponentEnergyContainer
}