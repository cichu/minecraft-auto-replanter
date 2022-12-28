package com.github.cichu.auto_replanter.harvest;

import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.sound.SoundEvent;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CropsHarvestingHandler extends BaseHarvestingHandler<CropBlock> {

    private final Set<Class<? extends CropBlock>> cropsToHandle;

    /**
     * Creates new instance of {@link CropsHarvestingHandler}.
     * @param soundEvent The {@link SoundEvent} to be played on a successful harvest. Cannot be null.
     * @param cropToHandle The class of a {@link CropBlock} or its descendant for this handler to handle. Cannot be null.
     * @param additionalCropsToHandle Additional classes of a {@link CropBlock} or its descendants for this handler
     *                               in case it should handle more than one type of crops. Not required but cannot be null.
     * @throws HarvestingHandlerInstantiationException When {@code soundEvent}, {@code cropToHandle} or any of
     * {@code additionalCropsToHandle} is null.
     */
    @SafeVarargs
    public CropsHarvestingHandler(
            final SoundEvent soundEvent,
            final Class<? extends CropBlock> cropToHandle,
            final Class<? extends CropBlock>... additionalCropsToHandle) {
        super(CropBlock.class, soundEvent);
        validate(cropToHandle, additionalCropsToHandle);
        cropsToHandle = Stream.concat(Stream.of(cropToHandle), Arrays.stream(additionalCropsToHandle))
                .collect(Collectors.toUnmodifiableSet());
    }

    private static void validate(
            final Class<? extends CropBlock> cropToHandle,
            final Class<? extends CropBlock>[] additionalCropsToHandle) {
        if (cropToHandle == null || Arrays.stream(additionalCropsToHandle).anyMatch(Objects::isNull)) {
            throw new HarvestingHandlerInstantiationException("Class of crop to handle cannot be null");
        }
    }

    @Override
    protected boolean canHandle(final CropBlock cropBlock) {
        return cropsToHandle.stream().anyMatch(cropBlock.getClass()::equals);
    }

    @Override
    protected boolean isMature(final CropBlock cropBlock, final BlockState blockState) {
        return cropBlock.isMature(blockState);
    }

    @Override
    protected BlockState getUpdatedBlockState(final CropBlock cropBlock, final BlockState currentState) {
        return cropBlock.withAge(0);
    }
}
