package sigmaone.industrialism.util.content

import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.block.Block
import net.minecraft.item.BlockItem
import net.minecraft.util.Identifier
import net.minecraft.util.math.Direction
import net.minecraft.util.registry.Registry
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.util.datagen.blockstate.BlockStateGenerator
import sigmaone.industrialism.util.datagen.model.ModelGenerator

class RegisteredBlockBuilder<T: Block>(val id: String, val block: T) {
    fun withGeneratedItem(settings: FabricItemSettings = FabricItemSettings()): RegisteredBlockBuilder<T> {
        Registry.register(Registry.ITEM, Identifier(Industrialism.MOD_ID, id), BlockItem(block, settings))
        ModelGenerator.generateBlockItemModel(Identifier(Industrialism.MOD_ID, id))
        return this
    }

    fun withNoModelItem(settings: FabricItemSettings = FabricItemSettings()): RegisteredBlockBuilder<T> {
        Registry.register(Registry.ITEM, Identifier(Industrialism.MOD_ID, id), BlockItem(block, settings))
        return this
    }

    fun withGeneratedBlockState(): RegisteredBlockBuilder<T> {
        BlockStateGenerator.generateBlockState(
            Identifier(Industrialism.MOD_ID, id),
            hashMapOf("" to Identifier(Industrialism.MOD_ID, "block/$id"))
        )
        return this
    }

    fun withFacingBlockState(): RegisteredBlockBuilder<T> {
        BlockStateGenerator.generateBlockState(
            Identifier(Industrialism.MOD_ID, id),
            hashMapOf(
                "facing=${Direction.UP.name.toLowerCase()}"    to Triple(Identifier(Industrialism.MOD_ID, "block/$id"), 0, 0),
                "facing=${Direction.DOWN.name.toLowerCase()}"  to Triple(Identifier(Industrialism.MOD_ID, "block/$id"), 180, 0),
                "facing=${Direction.NORTH.name.toLowerCase()}" to Triple(Identifier(Industrialism.MOD_ID, "block/$id"), 90, 0),
                "facing=${Direction.SOUTH.name.toLowerCase()}" to Triple(Identifier(Industrialism.MOD_ID, "block/$id"), 90, 180),
                "facing=${Direction.EAST.name.toLowerCase()}"  to Triple(Identifier(Industrialism.MOD_ID, "block/$id"), 90, 90),
                "facing=${Direction.WEST.name.toLowerCase()}"  to Triple(Identifier(Industrialism.MOD_ID, "block/$id"), 90, 270),
            )
        )
        return this
    }

    fun withEmptyBlock(): RegisteredBlockBuilder<T> {
        BlockStateGenerator.generateBlockState(
            Identifier(Industrialism.MOD_ID, id),
            hashMapOf("" to Identifier(Industrialism.MOD_ID, "empty"))
        )
        return this
    }

    fun withGeneratedModel(): RegisteredBlockBuilder<T> {
        ModelGenerator.generateBlockModel(Identifier(Industrialism.MOD_ID, id))
        return this
    }

    fun withSimpleSidedModel(): RegisteredBlockBuilder<T> {
        ModelGenerator.generateBlockModel(
            Identifier(Industrialism.MOD_ID, id),
            hashMapOf(
                Direction.UP    to Identifier(Industrialism.MOD_ID, "${id}/top"),
                Direction.DOWN  to Identifier(Industrialism.MOD_ID, "${id}/bottom"),
                Direction.NORTH to Identifier(Industrialism.MOD_ID, "${id}/side"),
                Direction.SOUTH to Identifier(Industrialism.MOD_ID, "${id}/side"),
                Direction.EAST  to Identifier(Industrialism.MOD_ID, "${id}/side"),
                Direction.WEST  to Identifier(Industrialism.MOD_ID, "${id}/side"),
            )
        )
        return this
    }

    fun build(): T {
        return Registry.register(Registry.BLOCK, Identifier(Industrialism.MOD_ID, id), block)
    }
}