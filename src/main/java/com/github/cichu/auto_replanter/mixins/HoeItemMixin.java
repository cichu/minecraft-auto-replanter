package com.github.cichu.auto_replanter.mixins;

import com.github.cichu.auto_replanter.harvest.CropsHarvestingHandler;
import net.minecraft.block.BeetrootsBlock;
import net.minecraft.block.CarrotsBlock;
import net.minecraft.block.PotatoesBlock;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoeItem.class)
public class HoeItemMixin {

    private static final CropsHarvestingHandler HARVESTING_HANDLER = new CropsHarvestingHandler(
            SoundEvents.ITEM_HOE_TILL, BeetrootsBlock.class, CarrotsBlock.class, PotatoesBlock.class);

    @Inject(at = @At("HEAD"), method = "useOnBlock", cancellable = true)
    private void injectAtHeadIntoUseOnBlockMethod(
            final ItemUsageContext context,
            final CallbackInfoReturnable<ActionResult> callbackInfo) {
        HARVESTING_HANDLER.attemptHarvesting(context, callbackInfo);
    }
}
