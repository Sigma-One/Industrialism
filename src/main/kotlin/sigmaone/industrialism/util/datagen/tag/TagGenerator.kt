package sigmaone.industrialism.util.datagen.tag

import com.google.gson.JsonArray
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import sigmaone.industrialism.util.datagen.DataGenerator

object TagGenerator {
    private fun getOrCreateItemTag(tagId: Identifier): Pair<Identifier, JsonArray> {
        for (tag in DataGenerator.ITEM_TAGS.keys) {
            if (tag == tagId) {
                return Pair(tag, DataGenerator.ITEM_TAGS[tag]!!)
            }
        }

        DataGenerator.ITEM_TAGS[tagId] = JsonArray()
        return Pair(tagId, DataGenerator.ITEM_TAGS[tagId]!!)
    }

    fun tagItem(tagId: Identifier, item: Item) {
        val tag = getOrCreateItemTag(tagId)
        tag.second.add(Registry.ITEM.getId(item).toString())
    }

    private fun getOrCreateBlockTag(tagId: Identifier): Pair<Identifier, JsonArray> {
        for (tag in DataGenerator.BLOCK_TAGS.keys) {
            if (tag == tagId) {
                return Pair(tag, DataGenerator.BLOCK_TAGS[tag]!!)
            }
        }

        DataGenerator.ITEM_TAGS[tagId] = JsonArray()
        return Pair(tagId, DataGenerator.BLOCK_TAGS[tagId]!!)
    }

    fun tagBlock(tagId: Identifier, block: Block) {
        val tag = getOrCreateItemTag(tagId)
        tag.second.add(Registry.ITEM.getId(block.asItem()).toString())
    }
}