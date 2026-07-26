package com.accbdd.reclamation_util.item;

import com.blakebr0.cucumber.helper.NBTHelper;
import com.blakebr0.cucumber.item.BaseWateringCanItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class WateringCanItem extends BaseWateringCanItem {
    public WateringCanItem(int range, double chance) {
        super(range, chance);
    }

    @Override
    protected boolean allowFakePlayerWatering() {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        var isActive = NBTHelper.getBoolean(stack, "Active");

        if (selected && isActive && entity instanceof Player player) {
            var trace = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);

            if (trace.getType() == HitResult.Type.BLOCK) {
                this.doWater(stack, level, player, trace.getBlockPos(), trace.getDirection());
            } else {
                stopPlayingSound(player);
            }
        }

        if (!selected && isActive && entity instanceof Player player) {
            stopPlayingSound(player);
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return NBTHelper.getBoolean(stack, "Active");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        var trace = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);

        if (trace.getType() != HitResult.Type.BLOCK) {
            if (NBTHelper.getBoolean(stack, "Water") && player.isCrouching()) {
                NBTHelper.flipBoolean(stack, "Active");
            }

            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }

        if (NBTHelper.getBoolean(stack, "Water")) {
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }

        var pos = trace.getBlockPos();
        var direction = trace.getDirection();

        if (level.mayInteract(player, pos) && player.mayUseItemAt(pos.relative(direction), direction, stack)) {
            var fluid = level.getFluidState(pos);

            if (fluid.is(FluidTags.WATER)) {
                NBTHelper.setBoolean(stack, "Water", true);

                player.playSound(SoundEvents.BUCKET_FILL, 1.0F, 1.0F);

                return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
            }
        }

        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var player = context.getPlayer();
        if (player == null)
            return InteractionResult.FAIL;

        var hand = context.getHand();
        var stack = player.getItemInHand(hand);

        if (NBTHelper.getBoolean(stack, "Active"))
            return InteractionResult.PASS;

        return super.useOn(context);
    }
}