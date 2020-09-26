package sigmaone.industrialism.util

import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.block.multiblock.BlockMultiblockRootBase

object MultiblockHelper {
    fun testForMultiblock(world: World, blockPos: BlockPos, side: Direction): BlockMultiblockRootBase? {
        multiblockIdentifyLoop@
        for (multiblock in Industrialism.MULTIBLOCKS) {
            val rootBlock = multiblock.layout[multiblock.rootPos[0]][multiblock.rootPos[1]][multiblock.rootPos[2]]
            if (world.getBlockState(blockPos).block == rootBlock) {
                var x: Int
                var y = 0
                var z: Int
                val rootXOffset = multiblock.rootPos[0]
                val rootYOffset = multiblock.rootPos[1]
                val rootZOffset = multiblock.rootPos[2]
                var result = true

                innerMultiblockIdentifyLoop@
                for ((i, _) in multiblock.layout.withIndex()) {
                    x = 0

                    for ((j, _) in multiblock.layout[i].withIndex()) {
                        z = 0

                        for (block in multiblock.layout[i][j]) {
                            val pos = when (side) {
                                Direction.NORTH -> blockPos.add(rootXOffset - x, -rootYOffset + y, rootZOffset + z)
                                Direction.SOUTH -> blockPos.add(-rootXOffset + x, -rootYOffset + y, -rootZOffset - z)
                                Direction.WEST  -> blockPos.add(-rootZOffset + z, -rootYOffset + y, -rootXOffset + x)
                                Direction.EAST  -> blockPos.add(rootZOffset - z, -rootYOffset + y, rootXOffset - x)
                                else -> return null
                            }
                            val worldBlock = world.getBlockState(pos).block

                            if (worldBlock != block) {
                                result = false
                            }

                            z += 1
                        }
                        x += 1
                    }
                    y += 1
                }
                if (result) {
                    return multiblock
                }
            }
        }
        return null
    }

    fun buildMultiblock(world: World, blockPos: BlockPos, side: Direction, multiblock: BlockMultiblockRootBase) {
        var x: Int
        var y = 0
        var z: Int
        val rootXOffset = -multiblock.rootPos[0]
        val rootYOffset = -multiblock.rootPos[1]
        val rootZOffset = -multiblock.rootPos[2]

        multiblockBuildLoop@
        for ((i, _) in multiblock.layout.withIndex()) {
            x = 0

            for ((j, _) in multiblock.layout[i].withIndex()) {
                z = 0

                for (block in multiblock.layout[i][j]) {
                    val pos = when (side) {
                        Direction.NORTH -> blockPos.add(-rootXOffset - x, rootYOffset + y, -rootZOffset + z)
                        Direction.SOUTH -> blockPos.add(rootXOffset + x, rootYOffset + y, rootZOffset - z)
                        Direction.WEST  -> blockPos.add(rootZOffset + z, rootYOffset + y, rootXOffset + x)
                        Direction.EAST  -> blockPos.add(-rootZOffset - z, rootYOffset + y, -rootXOffset - x)
                        else -> break@multiblockBuildLoop
                    }

                    world.setBlockState(pos, Industrialism.MULTIBLOCK_CHILD_BLOCK.defaultState)
                    z += 1
                }
                x += 1
            }
            y += 1
        }
    }
}