package com.github.cichu.auto_replanter.harvest;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class HarvestingHandler {

    private final SoundEvent soundEvent;

    private final Set<Class<? extends CropBlock>> cropsToHandle;

    /**
     * Creates new instance of {@link HarvestingHandler}.
     * @param soundEvent The {@link SoundEvent} to be played on a successful harvest. Cannot be null.
     * @param cropToHandle The class of a {@link CropBlock} or its descendant for this handler to handle. Cannot be null.
     * @param additionalCropsToHandle Additional classes of a {@link CropBlock} or its descendants for this handler
     *                               in case it should handle more than one type of crops. Not required but cannot be null.
     * @throws HarvestingHandlerInstantiationException When {@code soundEvent}, {@code cropToHandle} or any of
     * {@code additionalCropsToHandle} is null.
     */
    @SafeVarargs
    public HarvestingHandler(
            final SoundEvent soundEvent,
            final Class<? extends CropBlock> cropToHandle,
            final Class<? extends CropBlock>... additionalCropsToHandle) {
        validate(soundEvent, cropToHandle, additionalCropsToHandle);
        this.soundEvent = soundEvent;
        this.cropsToHandle = Stream.concat(Stream.of(cropToHandle), Arrays.stream(additionalCropsToHandle))
                .collect(Collectors.toUnmodifiableSet());
    }

    private static void validate(
            final SoundEvent soundEvent,
            final Class<? extends CropBlock> cropToHandle,
            final Class<? extends CropBlock>[] additionalCropsToHandle) {
        if (soundEvent == null) {
            throw new HarvestingHandlerInstantiationException("Sound event is required");
        }
        if (cropToHandle == null || Arrays.stream(additionalCropsToHandle).anyMatch(Objects::isNull)) {
            throw new HarvestingHandlerInstantiationException("No crop to handle can be null");
        }
    }

    /**
     * Attempts to harvest. <br>
     * If it manages to harvest then it sets return value and cancels.
     * Does nothing otherwise.
     * @param context Item usage context to be handled.
     * @param callbackInfo Injection callback info.
     */
    public void attemptHarvesting(
            final ItemUsageContext context,
            final CallbackInfoReturnable<ActionResult> callbackInfo) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();

        if (canHandle(block)) {
            CropBlock cropBlock = (CropBlock) block;

            if (isMature(cropBlock, blockState)) {
                if (!world.isClient) {
                    handleHarvesting(world, pos, blockState, cropBlock, context);
                }
                callbackInfo.setReturnValue(ActionResult.success(world.isClient));
                callbackInfo.cancel();
            }
        }
    }

    private boolean canHandle(final Block block) {
        return cropsToHandle.contains(block.getClass());
    }

    private boolean isMature(final CropBlock cropBlock, final BlockState blockState) {
        return cropBlock.isMature(blockState);
    }

    private void handleHarvesting(
            final World world,
            final BlockPos pos,
            final BlockState blockState,
            final CropBlock cropBlock,
            final ItemUsageContext context) {
        playSound(world, pos);
        updateCropState(world, pos, cropBlock);
        dropLoot(world, pos, blockState, cropBlock);
        updateToolState(world, pos, context.getPlayer(), context.getHand(), context.getStack());
    }

    private void playSound(final World world, final BlockPos pos) {
        world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1, 1);
    }

    private void updateCropState(final World world, final BlockPos pos, final CropBlock cropBlock) {
        world.setBlockState(pos, cropBlock.withAge(0), Block.NOTIFY_LISTENERS);
    }

    private void dropLoot(final World world, final BlockPos pos, final BlockState state, final CropBlock cropBlock) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        List<ItemStack> droppedStacks = Block.getDroppedStacks(state, (ServerWorld) world, pos, blockEntity);
        for (ItemStack droppedStack : droppedStacks) {
            // no need for player to manually replant crop, so every harvest automatically drops one seed less
            if (isSeedItem(cropBlock, droppedStack)) {
                droppedStack.decrement(1);
            }
            Block.dropStack(world, pos, droppedStack);
        }
    }

    private boolean isSeedItem(final CropBlock block, final ItemStack droppedStack) {
        Item seedItem = block.getPickStack(null, null, null)
                .getItem();
        return droppedStack.isOf(seedItem);
    }

    private void updateToolState(
            final World world,
            final BlockPos pos,
            final PlayerEntity player,
            final Hand hand,
            final ItemStack stack) {
        stack.damage(1, player, playerEntity -> playerEntity.sendToolBreakStatus(hand));
        world.emitGameEvent(player, GameEvent.SHEAR, pos);
    }
}
