package sigmaone.industrialism.block

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Tickable
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import sigmaone.industrialism.Industrialism.InputConfig
import sigmaone.industrialism.energy.IBlockEnergyHandler
import sigmaone.industrialism.energy.ISidedBlockEnergyHandler
import java.util.*
import kotlin.collections.HashMap

abstract class BlockEntitySidedEnergyContainer(blockEntity: BlockEntityType<*>?, maxEnergy: Float, sideConfig: Map<Direction, InputConfig>) : BlockEntityEnergyContainer(blockEntity, maxEnergy), BlockEntityClientSerializable, IBlockEnergyHandler, ISidedBlockEnergyHandler, Tickable {
    open var sideConfig: HashMap<Direction, InputConfig> = sideConfig.toMap() as HashMap<Direction, InputConfig>

    var maxTransfer: Float = 1f
    override fun refresh() {
        super.refresh()
        if (getWorld() != null && !getWorld()!!.isClient) {
            sync()
        }
    }

    fun cycleSideConfig(side: Direction) {
        var ord = sideConfig[side]!!.ordinal
        if (ord == 2) {
            ord = 0
        } else {
            ord += 1
        }
        setSideConfig(side, InputConfig.values()[ord])
    }

    fun setSideConfig(side: Direction, state: InputConfig) {
        sideConfig[side] = state
        refresh()
    }


    override fun getNeighbour(dir: Direction?): BlockEntity? {
        return getWorld()!!.getBlockEntity(getPos().offset(dir))
    }

    override val acceptingNeighbours: Vector<Direction> get() {
            val acceptors: Vector<Direction> = Vector<Direction> ()
            for (dir in Direction.values()) {
                if (getNeighbour(dir) != null) {
                    if (sideConfig[dir] == InputConfig.OUTPUT) {
                        if (getNeighbour(dir) is BlockEntitySidedEnergyContainer) {
                            if ((getNeighbour(dir) as BlockEntitySidedEnergyContainer?)!!.sideConfig[dir.opposite] == InputConfig.INPUT) {
                                acceptors.add(dir)
                            }
                        }
                    }
                }
            }
            return acceptors
        }

    override fun sendEnergy(pos: BlockPos?, amount: Float) {
        if (getWorld()!!.getBlockEntity(pos) is BlockEntityEnergyContainer) {
            val blockEntity = getWorld()!!.getBlockEntity(pos) as BlockEntityEnergyContainer
            var packet = amount

            if (amount > maxTransfer) {
                packet = maxTransfer
            }

            if (blockEntity.availableEnergyCapacity < amount) {
                packet = blockEntity.availableEnergyCapacity
            }

            packet = takeEnergy(packet)
            val leftover = blockEntity.putEnergy(packet)
            putEnergy(leftover)
            refresh()
        }
    }

    override fun tick() {
        val acceptors = acceptingNeighbours
        for (d in acceptors) {
            if (getNeighbour(d) is BlockEntitySidedEnergyContainer) {
                sendEnergy(getNeighbour(d)?.pos, maxTransfer)
            }
        }
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)
        for (dir in sideConfig.keys) {
            tag.putInt(dir.toString(), sideConfig[dir]!!.ordinal)
        }
        return tag
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)
        for (dir in sideConfig.keys) {
            sideConfig[dir] = InputConfig.values()[tag.getInt(dir.toString())]
        }
        refresh()
    }

    override fun toClientTag(tag: CompoundTag): CompoundTag {
        for (dir in sideConfig.keys) {
            tag.putInt(dir.toString(), sideConfig[dir]!!.ordinal)
        }
        return tag
    }

    override fun fromClientTag(tag: CompoundTag) {
        for (dir in sideConfig.keys) {
            setSideConfig(dir, InputConfig.values()[tag.getInt(dir.toString())])
        }
    }

}