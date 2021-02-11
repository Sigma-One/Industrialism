package sigmaone.industrialism.util.contentbuilder

import net.minecraft.item.Item
import sigmaone.industrialism.util.RegistryHelper
import sigmaone.industrialism.util.datagen.model.ModelGenerator

class ItemBuilder<T: Item>(val id: String, val item: T) {
    init {
        RegistryHelper.registerItem(id, item)
    }

    companion object {
        fun <T: Item> getStandard(id: String, item: T): T {
            return ItemBuilder(id, item)
                .generateModel()
                .get()
        }

        fun <T: Item> getTool(id: String, item: T): T {
            return ItemBuilder(id, item)
                .generateModel(true)
                .get()
        }
    }

    fun generateModel(isTool: Boolean = false): ItemBuilder<T> {
        if (isTool) {
            ModelGenerator.generateItemModel(item, isTool)
        }
        else {
            ModelGenerator.generateItemModel(item)
        }
        return this
    }

    fun get(): T {
        return item
    }
}