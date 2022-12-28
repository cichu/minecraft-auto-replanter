package com.github.cichu.auto_replanter.harvest;

import net.minecraft.block.BlockState;
import net.minecraft.block.CocoaBlock;
import net.minecraft.sound.SoundEvents;

public class CocoaHarvestingHandler extends BaseHarvestingHandler<CocoaBlock> {

    /**
     * Creates new CocoaHarvestingHandler.
     */
    public CocoaHarvestingHandler() {
        super(CocoaBlock.class, SoundEvents.ITEM_AXE_STRIP);
    }

    @Override
    protected boolean canHandle(final CocoaBlock block) {
        return true;
    }

    @Override
    protected boolean isMature(final CocoaBlock block, final BlockState blockState) {
        return blockState.get(CocoaBlock.AGE) == CocoaBlock.MAX_AGE;
    }

    @Override
    protected BlockState getUpdatedBlockState(final CocoaBlock block, final BlockState currentState) {
        return currentState.with(CocoaBlock.AGE, 0);
    }
}
