package sigmaone.industrialism.component.mechanical

import net.minecraft.block.entity.BlockEntity

/**
 * Energy container component interface
 *
 * @property componentMechanicalDevice The [ComponentMechanicalDevice] for the implementing [BlockEntity]
 */
interface IComponentMechanicalDevice {
    val componentMechanicalDevice: ComponentMechanicalDevice
}