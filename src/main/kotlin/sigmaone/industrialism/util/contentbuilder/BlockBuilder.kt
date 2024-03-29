package sigmaone.industrialism.util.contentbuilder

import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.util.registry.Registry
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.util.RegistryHelper
import sigmaone.industrialism.util.datagen.blockstate.BlockStateGenerator
import sigmaone.industrialism.util.datagen.model.ModelGenerator

class BlockBuilder<T: Block>(val id: String, val block: T) {
    companion object {
        fun <T: Block> getStandard(id: String, block: T, itemGroup: ItemGroup): T {
            return BlockBuilder(id, block)
                .generateBlockState()
                .generateItem(Item.Settings().group(itemGroup))
                .generateModel()
                .get()
        }

        fun <T: Block> getHorizontalFacing(id: String, block: T): T {
            return BlockBuilder(id, block)
                .generateBlockState(withFacing = true)
                .generateItem(Item.Settings())
                .generateModel()
                .get()
        }
    }

    fun generateItem(settings: Item.Settings, withModel: Boolean = true): BlockBuilder<T> {
        Registry.register(Registry.ITEM, Identifier(Industrialism.MOD_ID, id), BlockItem(block, settings) as Item)
        if (withModel) {
            ModelGenerator.generateBlockItemModel(Identifier(Industrialism.MOD_ID, id))
        }
        return this
    }

    fun generateBlockState(withFacing: Boolean = false, empty: Boolean = false): BlockBuilder<T> {
        if (empty) {
            BlockStateGenerator.generateBlockState(
                Identifier(Industrialism.MOD_ID, id),
                hashMapOf("" to Identifier(Industrialism.MOD_ID, "empty"))
            )
            return this
        }
        if (withFacing) {
            BlockStateGenerator.generateBlockState(
                Identifier(Industrialism.MOD_ID, id),
                hashMapOf(
                    "facing=${Direction.UP.name.toLowerCase()}"    to Triple(Identifier(Industrialism.MOD_ID, id), 0, 0),
                    "facing=${Direction.DOWN.name.toLowerCase()}"  to Triple(Identifier(Industrialism.MOD_ID, id), 180, 0),
                    "facing=${Direction.NORTH.name.toLowerCase()}" to Triple(Identifier(Industrialism.MOD_ID, id), 90, 0),
                    "facing=${Direction.SOUTH.name.toLowerCase()}" to Triple(Identifier(Industrialism.MOD_ID, id), 90, 180),
                    "facing=${Direction.EAST.name.toLowerCase()}"  to Triple(Identifier(Industrialism.MOD_ID, id), 90, 90),
                    "facing=${Direction.WEST.name.toLowerCase()}"  to Triple(Identifier(Industrialism.MOD_ID, id), 90, 270),
                )
            )
        }
        else {
            BlockStateGenerator.generateBlockState(
                Identifier(Industrialism.MOD_ID, id),
                hashMapOf("" to Identifier(Industrialism.MOD_ID, id))
            )
        }
        return this
    }

    fun generateModel(customSides: Array<Direction> = arrayOf()): BlockBuilder<T> {
        if (customSides.isEmpty()) {
            ModelGenerator.generateBlockModel(Identifier(Industrialism.MOD_ID, id))
            return this
        }
        val sideMap = HashMap<Direction, Identifier>()
        for (d in Direction.values()) {
            if (d in customSides) {
                val dName = when (d) {
                    Direction.UP   -> { "top" }
                    Direction.DOWN -> { "bottom" }
                    else           -> { d.toString().toLowerCase() }
                }
                sideMap[d] = Identifier(Industrialism.MOD_ID, "${id}/${dName}")
            }
            else {
                sideMap[d] = Identifier(Industrialism.MOD_ID, "${id}/side")
            }
        }
        ModelGenerator.generateBlockModel(Identifier(Industrialism.MOD_ID, id), sideMap)
        return this
    }

    fun get(): T {
        return block
    }

    init {
        RegistryHelper.registerBlock(id, block)
    }
}