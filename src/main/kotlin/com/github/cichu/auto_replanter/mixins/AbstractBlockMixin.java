package com.github.cichu.auto_replanter.mixins;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {

    // TODO: injects into deprecated method, inject into AbstractBlock.AbstractBlockState.onUse instead
    @SuppressWarnings("CancellableInjectionUsage") // cancellable used in inheriting class
    @Inject(at = @At(value = "HEAD"), method = "onUse", cancellable = true)
    protected void injectAtHeadIntoOnUseMethod(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            Hand hand,
            BlockHitResult hit,
            CallbackInfoReturnable<ActionResult> callbackInfo) {
        // empty on purpose
    }
}
