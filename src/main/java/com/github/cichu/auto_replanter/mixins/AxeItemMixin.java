package com.github.cichu.auto_replanter.mixins;

import com.github.cichu.auto_replanter.harvest.CocoaHarvestingHandler;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AxeItem.class)
public class AxeItemMixin {

    private static final CocoaHarvestingHandler HARVESTING_HANDLER = new CocoaHarvestingHandler();

    @Inject(at = @At("HEAD"), method = "useOnBlock", cancellable = true)
    private void injectAtHeadIntoUseOnBlockMethod(
            final ItemUsageContext context,
            final CallbackInfoReturnable<ActionResult> callbackInfo) {
        HARVESTING_HANDLER.attemptHarvesting(context, callbackInfo);
    }
}
