package sigmaone.industrialism.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import sigmaone.industrialism.block.wiring.BlockEntityWireConnectorT0;
import sigmaone.industrialism.component.wiring.IComponentWireNode;

import java.util.Iterator;

@Mixin(Explosion.class)
public class MixinWiringExplosionFix {
    @Shadow @Final private World world;

    @Inject(method="affectWorld", at=@At(
            value="INVOKE",
            target="Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z",
            ordinal=0
    ), locals=LocalCapture.CAPTURE_FAILHARD)
    private void callDisassemble(boolean bl, CallbackInfo ci, boolean bl2, ObjectArrayList objectArrayList, Iterator var4, BlockPos blockPos, BlockState blockState, Block block) {
        BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
        if (blockEntity instanceof IComponentWireNode) {
            ((IComponentWireNode) blockEntity).getComponentWireNode().removeAllConnections();
        }
    }
}
