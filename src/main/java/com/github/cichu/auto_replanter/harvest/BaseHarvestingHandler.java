package com.github.cichu.auto_replanter.harvest;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class BaseHarvestingHandler<B extends Block> {

    private final Class<B> handledClass;

    private final SoundEvent soundEvent;

    protected BaseHarvestingHandler(final Class<B> blockToHandle, final SoundEvent soundEvent) {
        validate(blockToHandle, soundEvent);
        this.handledClass = blockToHandle;
        this.soundEvent = soundEvent;
    }

    private static void validate(final Class<? extends Block> blockToHandle, final SoundEvent soundEvent) {
        if (blockToHandle == null) {
            throw new HarvestingHandlerInstantiationException("Class of Block to handle is required");
        }
        if (soundEvent == null) {
            throw new HarvestingHandlerInstantiationException("Sound event is required");
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

        if (handledClass.isInstance(block)) {
            B handledBlock = handledClass.cast(block);
            if (canHandle(handledBlock) && isMature(handledBlock, blockState)) {
                if (!world.isClient) {
                    handleHarvesting(world, pos, blockState, handledBlock, context);
                }
                callbackInfo.setReturnValue(ActionResult.success(world.isClient));
                callbackInfo.cancel();
            }
        }
    }

    protected abstract boolean canHandle(B block);

    protected abstract boolean isMature(B block, BlockState blockState);

    protected abstract BlockState getUpdatedBlockState(B block, BlockState currentState);

    private void handleHarvesting(
            final World world,
            final BlockPos pos,
            final BlockState blockState,
            final B block,
            final ItemUsageContext context) {
        playSound(world, pos);
        updateBlockState(world, pos, block, blockState);
        dropLoot(world, pos, blockState, block);
        updateToolState(world, pos, context.getPlayer(), context.getHand(), context.getStack());
    }

    private void playSound(final World world, final BlockPos pos) {
        world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1, 1);
    }

    private void updateBlockState(
            final World world,
            final BlockPos pos,
            final B block,
            final BlockState blockState) {
        world.setBlockState(pos, getUpdatedBlockState(block, blockState), Block.NOTIFY_LISTENERS);
    }

    private void dropLoot(final World world, final BlockPos pos, final BlockState state, final Block block) {
        List<ItemStack> stacksToDrop = getStacksToDrop(world, pos, state);
        findSeedStack(block, stacksToDrop).ifPresent(seedStack -> seedStack.decrement(1));
        dropStacks(world, pos, stacksToDrop);
    }

    private List<ItemStack> getStacksToDrop(final World world, final BlockPos pos, final BlockState state) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return Block.getDroppedStacks(state, (ServerWorld) world, pos, blockEntity);
    }

    private Optional<ItemStack> findSeedStack(final Block block, final Collection<ItemStack> stacks) {
        Item seedItem = getSeedItem(block);
        return stacks.stream()
                .filter(stack -> stack.isOf(seedItem))
                .findAny();
    }

    private Item getSeedItem(final Block block) {
        return block.getPickStack(null, null, null).getItem();
    }

    private void dropStacks(final World world, final BlockPos pos, final Collection<ItemStack> droppedStacks) {
        droppedStacks.forEach(droppedStack -> Block.dropStack(world, pos, droppedStack));
    }

    private void updateToolState(
            final World world,
            final BlockPos pos,
            final PlayerEntity player,
            final Hand hand,
            final ItemStack stack) {
        stack.damage(1, player, playerEntity -> playerEntity.sendToolBreakStatus(hand));
        world.emitGameEvent(player, GameEvent.ITEM_INTERACT_FINISH, pos);
    }
}
