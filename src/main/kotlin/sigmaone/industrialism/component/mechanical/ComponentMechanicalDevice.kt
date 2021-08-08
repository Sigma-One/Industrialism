package sigmaone.industrialism.component.mechanical

import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.Direction
import sigmaone.industrialism.component.Component
import sigmaone.industrialism.util.IO

class ComponentMechanicalDevice(
    owner: BlockEntity,
    val mass: Double,
    val maxRpm: Double,
    var sideConfig: HashMap<Direction, IO>,
    var lockedSides: ArrayList<Direction> = arrayListOf(),
    var rpm: Double = 0.0,
    @Deprecated("Usage TBD")
    val maxTorque: Double = 0.0,
    @Deprecated("Usage TBD")
    var torque: Double = 0.0
) : Component(owner) {
    var visualDegrees = 0.0

    fun toClientTag(tag: NbtCompound?): NbtCompound {
        val subTag = NbtCompound()
        subTag.putDouble("rpm", rpm)
        subTag.putDouble("degrees", visualDegrees)
        for (dir in sideConfig.keys) {
            subTag.putInt(dir.toString(), sideConfig[dir]!!.ordinal)
        }
        tag!!.put("mechanicalComponent", subTag)
        return tag
    }

    fun fromClientTag(tag: NbtCompound?) {
        val subTag = tag!!.getCompound("mechanicalComponent")
        rpm = subTag!!.getDouble("rpm")
        visualDegrees = subTag.getDouble("degrees")
        for (dir in sideConfig.keys) {
            sideConfig[dir] = IO.values()[subTag.getInt(dir.toString())]
        }
    }

    fun writeNbt(tag: NbtCompound?): NbtCompound {
        val subTag = NbtCompound()
        subTag.putDouble("rpm", rpm)
        for (dir in sideConfig.keys) {
            subTag.putInt(dir.toString(), sideConfig[dir]!!.ordinal)
        }
        tag!!.put("mechanicalComponent", subTag)
        return tag
    }

    fun readNbt(tag: NbtCompound?) {
        val subTag = tag!!.getCompound("mechanicalComponent")
        rpm = subTag!!.getDouble("rpm")
        for (dir in sideConfig.keys) {
            sideConfig[dir] = IO.values()[subTag.getInt(dir.toString())]
        }
    }

    val neighbours: ArrayList<ComponentMechanicalDevice>
    get() {
        val list = ArrayList<ComponentMechanicalDevice>()
        for (dir in Direction.values()) {
            if (sideConfig[dir] == IO.OUTPUT) {
                val neighbour = owner.world!!.getBlockEntity(owner.pos.offset(dir))
                if (neighbour is IComponentMechanicalDevice && neighbour.componentMechanicalDevice.sideConfig[dir.opposite] == IO.INPUT) {
                    list.add(neighbour.componentMechanicalDevice)
                }
            }
        }
        return list
    }

    fun tick() {
        val rpt = rpm / 60 / 20
        visualDegrees += rpt * 360
        for (n in neighbours) {
            if (rpm > n.rpm) {
                n.rpm = rpm
            }
        }
        if (rpm > 0.0) {
            rpm -= 1 / mass
            if (rpm < 0.0) {
                rpm = 0.0
            }
        }
    }
}