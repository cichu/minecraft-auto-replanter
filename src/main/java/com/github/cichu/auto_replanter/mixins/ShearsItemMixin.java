package com.github.cichu.auto_replanter.mixins;

import com.github.cichu.auto_replanter.harvest.HarvestingHandler;
import net.minecraft.block.CropBlock;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ShearsItem;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShearsItem.class)
public class ShearsItemMixin {

    private static final HarvestingHandler HARVESTING_HANDLER
            = new HarvestingHandler(SoundEvents.ENTITY_SHEEP_SHEAR, CropBlock.class);

    @Inject(at = @At("HEAD"), method = "useOnBlock", cancellable = true)
    private void injectAtHeadIntoUseOnBlockMethod(
            final ItemUsageContext context,
            final CallbackInfoReturnable<ActionResult> callbackInfo) {
        HARVESTING_HANDLER.attemptHarvesting(context, callbackInfo);
    }
}
