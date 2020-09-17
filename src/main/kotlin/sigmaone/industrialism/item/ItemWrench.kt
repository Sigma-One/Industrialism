package sigmaone.industrialism.item

import net.minecraft.block.Block
import net.minecraft.item.ItemUsageContext
import net.minecraft.item.MiningToolItem
import net.minecraft.item.ToolMaterial
import net.minecraft.state.property.Properties
import net.minecraft.util.ActionResult
import net.minecraft.util.math.Direction
import sigmaone.industrialism.Industrialism
import sigmaone.industrialism.block.multiblock.BlockEntityMultiblockChildBase
import sigmaone.industrialism.block.multiblock.BlockEntityMultiblockRootBase
import sigmaone.industrialism.block.multiblock.BlockMultiblockChildBase
import sigmaone.industrialism.block.multiblock.BlockMultiblockRootBase
import java.util.*

class ItemWrench(material: ToolMaterial?, attackDamage: Int, attackSpeed: Float, settings: Settings?) : MiningToolItem(attackDamage.toFloat(), attackSpeed, material, HashSet(), settings) {
    override fun useOnBlock(context: ItemUsageContext): ActionResult {
        var foundMultiblock: BlockMultiblockRootBase? = null
        for (multiblock in Industrialism.MULTIBLOCKS) {
            val result = true
            if (context.world.getBlockState(context.blockPos).block === multiblock.layout!![multiblock.rootPos!![0]]!![multiblock.rootPos!![1]]!![multiblock.rootPos!![2]]) {
                var x = 0
                var y = 0
                var z = 0
                val rootXOffset = -multiblock.rootPos!![0]
                val rootYOffset = -multiblock.rootPos!![1]
                val rootZOffset = -multiblock.rootPos!![2]
                val toTest = Array(multiblock.layout!!.size) { Array(multiblock.layout!![0]!!.size) { arrayOfNulls<Block>(multiblock.layout!![0]!![0]!!.size) } }
                /*for (int i = 0; i < multiblock.getLayout().length; i++) {
                    for (int j = 0; j < multiblock.getLayout()[0].length; j++) {
                        for (int k = 0; k < multiblock.getLayout()[0][0].length;) {
                            toTest[i][j][k] = context.getWorld().getBlockState(new BlockPos(y + i - rootYOffset, x + j - rootXOffset, z + k - rootXOffset)).getBlock();
                        }
                    }
                }*/
                multiblockCheckLoop@for (layer in multiblock.layout!!) {
                    when (context.side) {
                        Direction.WEST -> x = 0
                        Direction.EAST -> x = 0
                        Direction.NORTH -> z = 0
                        Direction.SOUTH -> z = 0
                        else -> throw IllegalArgumentException()
                    }
                    for (row in layer!!) {
                        when (context.side) {
                            Direction.WEST -> z = 0
                            Direction.EAST -> z = 0
                            Direction.NORTH -> x = 0
                            Direction.SOUTH -> x = 0
                            else -> throw IllegalArgumentException()
                        }
                        for (block in row!!) {
                            if (z >= row.size && x >= layer.size && y >= multiblock.layout!!.size) {
                                break@multiblockCheckLoop
                            }
                            val pos = context.blockPos.add(x, y, z).add(rootXOffset, rootYOffset, rootZOffset)
                            val testBlock = context.world.getBlockState(pos).block
                            toTest[Math.abs(y)][Math.abs(x)][Math.abs(z)] = testBlock
                            when (context.side) {
                                Direction.EAST -> z -= 1
                                Direction.WEST -> z += 1
                                Direction.NORTH -> x -= 1
                                Direction.SOUTH -> x += 1
                                else -> throw IllegalArgumentException()
                            }
                        }
                        when (context.side) {
                            Direction.EAST -> x -= 1
                            Direction.WEST -> x += 1
                            Direction.NORTH -> z += 1
                            Direction.SOUTH -> z -= 1
                            else -> throw IllegalArgumentException()
                        }
                    }
                    y += 1
                }
                val trueLayout = multiblock.layout
                if (Arrays.deepEquals(toTest, trueLayout)) {
                    foundMultiblock = multiblock
                }
            }
            if (foundMultiblock != null) {
                val rootXOffset = -foundMultiblock.rootPos!![0]
                val rootYOffset = -foundMultiblock.rootPos!![1]
                val rootZOffset = -foundMultiblock.rootPos!![2]
                val children = HashSet<BlockEntityMultiblockChildBase?>()
                var x = 0
                var y = 0
                var z = 0
                // Clean up all blocks forming the multiblock by replacing with correct multiblock child blocks
                for (layer in foundMultiblock.layout!!) {
                    when (context.side) {
                        Direction.WEST -> x = 0
                        Direction.EAST -> x = 0
                        Direction.NORTH -> z = 0
                        Direction.SOUTH -> z = 0
                        else -> throw IllegalArgumentException()
                    }
                    for (row in layer!!) {
                        when (context.side) {
                            Direction.WEST -> z = 0
                            Direction.EAST -> z = 0
                            Direction.NORTH -> x = 0
                            Direction.SOUTH -> x = 0
                            else -> throw IllegalArgumentException()
                        }
                        for (ignored in row!!) {
                            val pos = context.blockPos.add(x, y, z).add(rootXOffset, rootYOffset, rootZOffset)
                            //context.world.setBlockState(pos, Industrialism.MULTIBLOCK_CHILD_BLOCK.defaultState)
                            //(context.world.getBlockState(pos).block as BlockMultiblockChildBase).shape = foundMultiblock.shape!![Math.abs(x)]!![Math.abs(y)]!![Math.abs(z)]!!
                            //(context.world.getBlockEntity(pos) as BlockEntityMultiblockChildBase?)!!.PosInLayout() = intArrayOf(Math.abs(x), Math.abs(y), Math.abs(z))
                            //children.add(context.world.getBlockEntity(pos) as BlockEntityMultiblockChildBase?)
                            when (context.side) {
                                Direction.EAST -> z -= 1
                                Direction.WEST -> z += 1
                                Direction.NORTH -> x -= 1
                                Direction.SOUTH -> x += 1
                                else -> throw IllegalArgumentException()
                            }
                        }
                        when (context.side) {
                            Direction.EAST -> x -= 1
                            Direction.WEST -> x += 1
                            Direction.NORTH -> z += 1
                            Direction.SOUTH -> z -= 1
                            else -> throw IllegalArgumentException()
                        }
                    }
                    y += 1
                }
                // Set root block to multiblock root
                context.world.setBlockState(context.blockPos, foundMultiblock.defaultState.with(Properties.HORIZONTAL_FACING, context.side.opposite))
                // Set child/parent relation stuff
                for (child in children) {
                //    child!!.parent = context.world.getBlockEntity(context.blockPos) as BlockEntityMultiblockRootBase?
                }
            }
        }
        return super.useOnBlock(context)
    }
}