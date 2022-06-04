package com.github.cichu.auto_replanter.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(CropBlock.class)
public class CropBlockMixin extends AbstractBlockMixin {

    // FIXME: fix used tools all types of crops
    @Override
    protected void injectAtHeadIntoOnUseMethod(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            Hand hand,
            BlockHitResult hit,
            CallbackInfoReturnable<ActionResult> callbackInfo) {
        handleOnUseHarvesting(state, world, pos, player, hand, callbackInfo);
    }

    private void handleOnUseHarvesting(
            BlockState state,
            World world,
            BlockPos pos,
            PlayerEntity player,
            Hand hand,
            CallbackInfoReturnable<ActionResult> callbackInfo) {
        ItemStack stackInHand = player.getStackInHand(hand);
        if (isFullyGrown(state) && stackInHand.isOf(getPickUpTool())) {
            if (!world.isClient) {
                harvestAndReplantCrop(state, world, pos, player, hand, stackInHand);
            }
            callbackInfo.setReturnValue(ActionResult.success(world.isClient));
            callbackInfo.cancel();
        }
    }

    private boolean isFullyGrown(BlockState state) {
        return getThisInstance().isMature(state);
    }

    private Item getPickUpTool() {
        return Items.SHEARS;
    }

    private void harvestAndReplantCrop(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stackInHand) {
        playSound(world, pos);
        updateCropState(world, pos);
        dropLoot(state, world, pos);
        updateToolState(world, pos, player, hand, stackInHand);
    }

    private CropBlock getThisInstance() {
        return (CropBlock) (Object) this;
    }

    private void playSound(World world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1, 1);
    }

    private void updateCropState(World world, BlockPos pos) {
        world.setBlockState(pos, getThisInstance().withAge(0), Block.NOTIFY_LISTENERS);
    }

    private void dropLoot(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        List<ItemStack> droppedStacks = Block.getDroppedStacks(state, (ServerWorld) world, pos, blockEntity);
        for (ItemStack droppedStack : droppedStacks) {
            // no need for player to manually replant crop, so every harvest automatically drops one seed less
            if (isSeedItem(droppedStack)) {
                droppedStack.decrement(1);
            }
            Block.dropStack(world, pos, droppedStack);
        }
    }

    private boolean isSeedItem(ItemStack droppedStack) {
        Item seedItem = getThisInstance()
                .getPickStack(null, null, null)
                .getItem();
        return droppedStack.isOf(seedItem);
    }

    private void updateToolState(World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack tool) {
        tool.damage(1, player, playerEntity -> playerEntity.sendToolBreakStatus(hand));
        world.emitGameEvent(player, GameEvent.SHEAR, pos);
        player.incrementStat(Stats.USED.getOrCreateStat(getPickUpTool()));
    }
}
