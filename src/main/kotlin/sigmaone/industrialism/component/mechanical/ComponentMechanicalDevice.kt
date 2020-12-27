package sigmaone.industrialism.component.mechanical

import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.CompoundTag
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

    fun toClientTag(tag: CompoundTag?): CompoundTag {
        tag!!.putDouble("rpm", rpm)
        tag.putDouble("degrees", visualDegrees)
        return tag
    }

    fun fromClientTag(tag: CompoundTag?) {
        rpm = tag!!.getDouble("rpm")
        visualDegrees = tag.getDouble("degrees")
    }

    fun toTag(tag: CompoundTag?): CompoundTag {
        tag!!.putDouble("rpm", rpm)
        return tag
    }

    fun fromTag(tag: CompoundTag?) {
        rpm = tag!!.getDouble("rpm")
    }

    val neighbours: ArrayList<ComponentMechanicalDevice>
    get() {
        val list = ArrayList<ComponentMechanicalDevice>()
        for (dir in Direction.values()) {
            if (sideConfig[dir] == IO.OUTPUT) {
                val neighbour = owner.world!!.getBlockEntity(owner.pos.offset(dir))
                if (neighbour is IComponentMechanicalDevice) {
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
            if (n.rpm > rpm) {
                rpm = n.rpm
            }
            else {
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