package sigmaone.industrialism.block

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.FacingBlock
import net.minecraft.state.StateManager
import net.minecraft.state.property.IntProperty
import net.minecraft.state.property.Properties
import net.minecraft.util.math.Direction

class BlockConnectorDummy(settings: Settings?) : FacingBlock(settings) {
    override fun appendProperties(stateBuilder: StateManager.Builder<Block, BlockState>) {
        stateBuilder.add(STATE)
        stateBuilder.add(Properties.FACING)
    }

    companion object {
        private val STATE = IntProperty.of("state", 0, 2)
    }

    init {
        defaultState = getStateManager().defaultState.with(Properties.FACING, Direction.DOWN).with(STATE, 0)
    }
}