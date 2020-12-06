package sigmaone.industrialism.mixin;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
import sigmaone.industrialism.block.multiblock.BlockEntityMultiblockChild;
import sigmaone.industrialism.block.multiblock.BlockEntityMultiblockRoot;

import java.util.Iterator;

@Mixin(Explosion.class)
public class MixinMultiblockExplosionFix {
    @Shadow @Final private World world;

    @Inject(method="affectWorld", at=@At(
            value="INVOKE",
            target="Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z",
            ordinal=0
    ), locals=LocalCapture.CAPTURE_FAILHARD)
    private void callDisassemble(boolean bl, CallbackInfo ci, boolean bl2, ObjectArrayList objectArrayList, Iterator var4, BlockPos blockPos, BlockState blockState, Block block) {
        if (this.world.getBlockEntity(blockPos) instanceof BlockEntityMultiblockRoot) {
            ((BlockEntityMultiblockRoot) this.world.getBlockEntity(blockPos)).disassemble();
        }
        else if (this.world.getBlockEntity(blockPos) instanceof BlockEntityMultiblockChild) {
            BlockPos parent = ((BlockEntityMultiblockChild) this.world.getBlockEntity(blockPos)).getParent();
            ((BlockEntityMultiblockRoot) this.world.getBlockEntity(parent)).disassemble();
        }
    }
}
